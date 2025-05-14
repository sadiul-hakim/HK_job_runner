package xyz.sadiulhakim.util;

import org.quartz.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobUtility {
    public static final String JOBS_PACKAGE_PATH = "xyz.sadiulhakim.jobs.";
    public static final Logger LOGGER = LoggerFactory.getLogger(JobUtility.class);

    private JobUtility() {
    }

    public Class<? extends Job> getJobClass(String className) {
        try {
            return (Class<? extends Job>) Class.forName(JOBS_PACKAGE_PATH + className);
        } catch (ClassNotFoundException ex) {
            LOGGER.error("getJobClass() could not get class {}", className);
        }

        return null;
    }
}
