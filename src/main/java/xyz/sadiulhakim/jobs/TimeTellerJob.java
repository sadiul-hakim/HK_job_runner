package xyz.sadiulhakim.jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TimeTellerJob implements Job {
    private static final Logger LOGGER = LoggerFactory.getLogger(TimeTellerJob.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        ZonedDateTime dateTime = ZonedDateTime.now();
        LOGGER.info("{}",dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss a z")));
    }
}
