package xyz.sadiulhakim.jobs;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.sadiulhakim.enumeration.JobStatus;
import xyz.sadiulhakim.execution.JobExecutionService;

import java.time.LocalDateTime;

@Component
public class HelloWorldJob implements Job {

    private static final Logger LOGGER = LoggerFactory.getLogger(HelloWorldJob.class);

    private JobExecutionService executionService;

    @Autowired
    public void setExecutionService(JobExecutionService executionService) {
        this.executionService = executionService;
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        JobDataMap dataMap = jobExecutionContext.getMergedJobDataMap();
        long runId = (long) dataMap.get("runId");
        LOGGER.info("Hello, World!");
        executionService.update(runId, JobStatus.FINISHED, null, LocalDateTime.now());
    }
}
