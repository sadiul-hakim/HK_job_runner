package xyz.sadiulhakim.jobs;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.sadiulhakim.enumeration.ExecutionType;
import xyz.sadiulhakim.enumeration.JobStatus;
import xyz.sadiulhakim.execution.JobExecution;
import xyz.sadiulhakim.execution.JobExecutionService;
import xyz.sadiulhakim.job.JobModel;
import xyz.sadiulhakim.job.JobService;
import xyz.sadiulhakim.util.JobUtility;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Component
public class DataTransferJob implements Job {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataTransferJob.class);

    private JobExecutionService executionService;
    private JobService jobService;

    @Autowired
    public void setJobService(JobService jobService) {
        this.jobService = jobService;
    }

    @Autowired
    public void setExecutionService(JobExecutionService executionService) {
        this.executionService = executionService;
    }

    @Override
    public void execute(JobExecutionContext context) {

        // Extract merged JobDataMap (job + trigger data)
        JobDataMap dataMap = context.getMergedJobDataMap();

        // Read values passed during job scheduling
        long jobId = (long) dataMap.get(JobUtility.JOB_ID);
        int typeId = (int) dataMap.get(JobUtility.EXECUTION_TYPE);

        // Convert type ID to enum
        ExecutionType type = ExecutionType.getById(typeId);

        // Load job metadata from database
        JobModel job = jobService.findById(jobId);

        // Create a new execution record in DB
        JobExecution execution = executionService.create(job, type);

        // Store execution info in JobDataMap
        dataMap.put(JobUtility.CURRENT_EXECUTION, execution.getId());
        dataMap.put(JobUtility.EXECUTION_TYPE, typeId);

        try {
            // Mark execution as IN_PROGRESS
            LocalDateTime startDate = LocalDateTime.now();
            dataMap.put(JobUtility.START_DATE, startDate);
            executionService.update(
                    execution.getId(),
                    JobStatus.IN_PROGRESS,
                    startDate,
                    null
            );
            dataMap.put(JobUtility.JOB_STATUS, JobStatus.IN_PROGRESS.getId());

            // Simulate actual work
            TimeUnit.SECONDS.sleep(5);
            executionService.update(
                    execution.getId(),
                    JobStatus.LOADING_DATA,
                    null,
                    null
            );
            dataMap.put(JobUtility.JOB_STATUS, JobStatus.LOADING_DATA.getId());

            TimeUnit.SECONDS.sleep(5);
            executionService.update(
                    execution.getId(),
                    JobStatus.TRANSFERRING_DATA,
                    null,
                    null
            );
            dataMap.put(JobUtility.JOB_STATUS, JobStatus.TRANSFERRING_DATA.getId());

            TimeUnit.SECONDS.sleep(5);

            // Mark execution as FINISHED
            LocalDateTime endDate = LocalDateTime.now();
            executionService.update(
                    execution.getId(),
                    JobStatus.FINISHED,
                    null,
                    endDate
            );
            dataMap.put(JobUtility.JOB_STATUS, JobStatus.FINISHED.getId());
            dataMap.put(JobUtility.END_DATE, endDate);

        } catch (Exception ex) {

            // Mark execution as FAILED if any error occurs
            executionService.update(
                    execution.getId(),
                    JobStatus.FAILED,
                    null,
                    LocalDateTime.now()
            );
            dataMap.put(JobUtility.JOB_STATUS, JobStatus.FAILED.getId());

            LOGGER.error("Failed to execute HelloWorldJob", ex);
        }
    }
}
