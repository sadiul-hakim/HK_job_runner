package xyz.sadiulhakim.util;

import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.sadiulhakim.job.JobModel;
import xyz.sadiulhakim.trigger.TriggerModel;

public class JobUtility {
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
        return TriggerBuilder.newTrigger()
                .withIdentity(name, group)
                .withSchedule(CronScheduleBuilder.cronSchedule(model.getCronExpression()))
                .forJob(key)
                .build();
    }
}
