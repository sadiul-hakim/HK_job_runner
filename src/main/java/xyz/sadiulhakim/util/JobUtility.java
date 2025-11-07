package xyz.sadiulhakim.util;

import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import xyz.sadiulhakim.enumeration.ExecutionType;
import xyz.sadiulhakim.enumeration.JobStatus;
import xyz.sadiulhakim.execution.JobExecution;
import xyz.sadiulhakim.job.JobModel;
import xyz.sadiulhakim.pojo.JobInformation;
import xyz.sadiulhakim.trigger.TriggerModel;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

public class JobUtility {
    public static final String JOB_ID = "jobId";
    public static final String JOB_NAME = "jobName";
    public static final String EXECUTION_TYPE = "executionType";
    public static final String JOB_STATUS = "jobStatus";
    public static final String START_DATE = "startDate";
    public static final String END_DATE = "endDate";
    public static final String JOBS_PACKAGE_PATH = "xyz.sadiulhakim.jobs.";
    public static final String CURRENT_EXECUTION = "currentExecution";
    public static final Logger LOGGER = LoggerFactory.getLogger(JobUtility.class);

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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
        JobDataMap map = new JobDataMap();
        map.put(JOB_ID, model.getId());
        map.put(JOB_NAME, model.getName());
        return JobBuilder.newJob(jobClass)
                .withIdentity(key)
                .usingJobData(map)
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
                .usingJobData(JobUtility.EXECUTION_TYPE, ExecutionType.SCHEDULED.getId())
                .forJob(key)
                .build();
    }

    public static JobInformation extractInformation(JobExecutionContext context) {

        JobDataMap dataMap = context.getMergedJobDataMap();

        long executionId = getLong(dataMap, JobUtility.CURRENT_EXECUTION, -1L);
        long jobId = getLong(dataMap, JobUtility.JOB_ID, -1L);
        String jobName = getString(dataMap, JobUtility.JOB_NAME, "UNKNOWN");

        int statusId = getInt(dataMap, JobUtility.JOB_STATUS, JobStatus.UNKNOWN.getId());
        JobStatus status = JobStatus.getById(statusId);

        int typeId = getInt(dataMap, JobUtility.EXECUTION_TYPE, ExecutionType.UNKNOWN.getId());
        ExecutionType type = ExecutionType.getById(typeId);

        LocalDateTime startDate = getLocalDateTime(dataMap, JobUtility.START_DATE, LocalDateTime.now());
        LocalDateTime endDate = getLocalDateTime(dataMap, JobUtility.END_DATE, LocalDateTime.now());
        String runningTime = getRunTime(startDate, endDate);

        return new JobInformation(
                jobId,
                executionId,
                jobName,
                formatter.format(startDate),
                formatter.format(endDate),
                runningTime,
                type.getName(),
                status.getName()
        );
    }

    public static JobInformation extractInformation(JobExecution execution) {

        long jobId = execution.getJob().getId();
        String jobName = execution.getJob().getName();
        long executionId = execution.getId();
        LocalDateTime startDate = execution.getStartTime();
        LocalDateTime endDate = execution.getEndTime();

        String runningTime = getRunTime(startDate, endDate);
        ExecutionType type = execution.getType();
        JobStatus status = execution.getStatus();

        String startDateText = startDate == null ? "" : formatter.format(startDate);
        String endDateText = endDate == null ? "" : formatter.format(endDate);

        return new JobInformation(
                jobId,
                executionId,
                jobName,
                startDateText,
                endDateText,
                runningTime,
                type.getName(),
                status.getName()
        );
    }

    private static String getRunTime(LocalDateTime startTime, LocalDateTime endTime) {

        if (startTime == null) {
            return "";
        }

        LocalDateTime jobEnd = (endTime != null) ? endTime : LocalDateTime.now();

        Duration duration = Duration.between(startTime, jobEnd);

        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;

        return (hours > 0 ? hours + "h " : "") +
                (minutes > 0 ? minutes + "m " : "") +
                seconds + "s";
    }

    private static long getLong(JobDataMap map, String key, long defaultValue) {
        Object value = map.get(key);

        switch (value) {
            case null -> {
                return defaultValue;
            }
            case Long l -> {
                return l;
            }
            case Integer i -> {
                return i.longValue();
            }
            case String s -> {
                try {
                    return Long.parseLong(s);
                } catch (NumberFormatException ignored) {
                }
            }
            default -> {
            }
        }

        return defaultValue;
    }

    private static int getInt(JobDataMap map, String key, int defaultValue) {
        Object value = map.get(key);

        switch (value) {
            case null -> {
                return defaultValue;
            }
            case Integer i -> {
                return i;
            }
            case Long l -> {
                return l.intValue();
            }
            case String s -> {
                try {
                    return Integer.parseInt(s);
                } catch (NumberFormatException ignored) {
                }
            }
            default -> {
            }
        }

        return defaultValue;
    }

    private static LocalDateTime getLocalDateTime(JobDataMap map, String key, LocalDateTime defaultValue) {
        Object value = map.get(key);

        switch (value) {
            case null -> {
                return defaultValue;
            }

            // Already LocalDateTime
            case LocalDateTime ldt -> {
                return ldt;
            }


            // java.util.Date → convert
            case Date date -> {
                return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
            }


            // Long epoch millis
            case Long l -> {
                return LocalDateTime.ofInstant(Instant.ofEpochMilli(l), ZoneId.systemDefault());
            }


            // Integer epoch millis (rare but safe)
            case Integer i -> {
                return LocalDateTime.ofInstant(Instant.ofEpochMilli(i.longValue()), ZoneId.systemDefault());
            }


            // String parsing
            case String s -> {
                try {
                    return LocalDateTime.parse(s);
                } catch (Exception ignored) {
                }
            }
            default -> {
            }
        }

        return defaultValue;
    }

    private static String getString(JobDataMap map, String key, String defaultValue) {
        Object value = map.get(key);
        return (value == null) ? defaultValue : value.toString();
    }
}
