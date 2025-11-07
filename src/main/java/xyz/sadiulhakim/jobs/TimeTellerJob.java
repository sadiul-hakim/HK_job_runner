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
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class TimeTellerJob implements Job {
    private static final Logger LOGGER = LoggerFactory.getLogger(TimeTellerJob.class);

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

        // Extract JobDataMap (contains job + trigger parameters)
        JobDataMap dataMap = context.getMergedJobDataMap();

        // Read values passed during job scheduling
        long jobId = (long) dataMap.get(JobUtility.JOB_ID);
        int typeId = (int) dataMap.get(JobUtility.EXECUTION_TYPE);

        // Convert type ID to enum
        ExecutionType type = ExecutionType.getById(typeId);

        // Load job from database
        JobModel job = jobService.findById(jobId);

        // Create a new execution record
        JobExecution execution = executionService.create(job, type);

        // Store execution info in JobDataMap
        dataMap.put(JobUtility.CURRENT_EXECUTION, execution.getId());
        dataMap.put(JobUtility.EXECUTION_TYPE, typeId);

        try {
            // Mark execution as IN_PROGRESS
            LocalDateTime startDate = LocalDateTime.now();
            executionService.update(
                    execution.getId(),
                    JobStatus.IN_PROGRESS,
                    startDate,
                    null
            );

            // Save start date and status into JobDataMap
            dataMap.put(JobUtility.START_DATE, startDate);
            dataMap.put(JobUtility.JOB_STATUS, JobStatus.IN_PROGRESS.getId());

            // Job logic (your actual work)
            ZonedDateTime dateTime = ZonedDateTime.now();
            LOGGER.info("{}", dateTime.format(
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss a z")
            ));

            // Mark execution as FINISHED
            LocalDateTime endDate = LocalDateTime.now();
            executionService.update(
                    execution.getId(),
                    JobStatus.FINISHED,
                    null,
                    endDate
            );

            // Save end date and status into JobDataMap
            dataMap.put(JobUtility.JOB_STATUS, JobStatus.FINISHED.getId());
            dataMap.put(JobUtility.END_DATE, endDate);

        } catch (Exception ex) {

            // Mark execution as FAILED
            executionService.update(
                    execution.getId(),
                    JobStatus.FAILED,
                    null,
                    LocalDateTime.now()
            );

            // Save failed status into JobDataMap
            dataMap.put(JobUtility.JOB_STATUS, JobStatus.FAILED.getId());

            LOGGER.error("Failed to execute HelloWorldJob", ex);
        }
    }
}
