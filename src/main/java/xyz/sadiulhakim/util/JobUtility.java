package xyz.sadiulhakim.util;

import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import xyz.sadiulhakim.job.JobModel;
import xyz.sadiulhakim.trigger.TriggerModel;

import java.time.ZoneId;
import java.util.TimeZone;

public class JobUtility {
    public static final String JOB_ID = "jobId";
    public static final String RUN_ID = "runId";
    public static final String JOBS_PACKAGE_PATH = "xyz.sadiulhakim.jobs.";
    public static final Logger LOGGER = LoggerFactory.getLogger(JobUtility.class);

    private JobUtility() {
    }

    public static Class<? extends Job> getJobClass(String className) {
        try {
            return (Class<? extends Job>) Class.forName(JOBS_PACKAGE_PATH + className);
        } catch (ClassNotFoundException ex) {
            LOGGER.error("getJobClass() could not get class {}", className);
        }

        return null;
    }

    public static JobDetail createJobDetail(JobModel model, JobKey key) {
        Class<? extends Job> jobClass = getJobClass(model.getClassName());
        return JobBuilder.newJob(jobClass)
                .withIdentity(key)
                .usingJobData("className", model.getClassName()) // optional custom data
                .storeDurably()
                .build();
    }

    public static Trigger createTrigger(TriggerModel model, JobKey key, String name, String group) {

        ZoneId zone;
        try {
            zone = StringUtils.hasText(model.getTimeZone()) ? ZoneId.of(model.getTimeZone()) : ZoneId.systemDefault();
        } catch (Exception ex) {
            LOGGER.error("Failed to create trigger {} for zone {}", model.getName(), model.getTimeZone());
            zone = ZoneId.systemDefault();
        }

        return TriggerBuilder.newTrigger()
                .withIdentity(name, group)
                .withSchedule(
                        CronScheduleBuilder.cronSchedule(model.getCronExpression())
                                .inTimeZone(TimeZone.getTimeZone(zone))
                )
                .forJob(key)
                .build();
    }
}
