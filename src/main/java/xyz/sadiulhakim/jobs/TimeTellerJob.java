package xyz.sadiulhakim.jobs;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
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
    public void execute(JobExecutionContext jobExecutionContext) {
        JobDataMap dataMap = jobExecutionContext.getMergedJobDataMap();
        long jobId = (long) dataMap.get(JobUtility.JOB_ID);
        JobModel job = jobService.findById(jobId);
        JobExecution execution = executionService.create(job);
        try {
            executionService.update(execution.getId(), JobStatus.IN_PROGRESS, LocalDateTime.now(), null);
            ZonedDateTime dateTime = ZonedDateTime.now();
            LOGGER.info("{}", dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss a z")));
            executionService.update(execution.getId(), JobStatus.FINISHED, null, LocalDateTime.now());
        } catch (Exception ex) {
            executionService.update(execution.getId(), JobStatus.FAILED, null, LocalDateTime.now());
            LOGGER.error("Failed to execute HelloWorldJob");
        }
    }
}
