package xyz.sadiulhakim.scheduler;

import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import xyz.sadiulhakim.enumeration.ExecutionType;
import xyz.sadiulhakim.job.JobModel;
import xyz.sadiulhakim.job.JobService;
import xyz.sadiulhakim.trigger.TriggerModel;
import xyz.sadiulhakim.util.JobUtility;

import java.util.List;

@Service
public class JobSchedulerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobSchedulerService.class);

    private final JobService jobService;
    private final Scheduler scheduler;

    public JobSchedulerService(JobService jobService, Scheduler scheduler) {
        this.jobService = jobService;
        this.scheduler = scheduler;
    }

    @Scheduled(initialDelay = 5 * 1000, fixedRate = 1000 * 60 * 10, scheduler = "defaultTaskScheduler")
    void applicationIsReady() {
        LOGGER.info("+-----------------------------Scheduling jobs----------------------------------+");
        List<JobModel> jobs = jobService.findAll();
        for (JobModel job : jobs) {
            scheduleJob(job);
        }

        LOGGER.info("+-----------------------------Done Scheduling jobs----------------------------------+");
    }

    public void scheduleJob(JobModel job) {
        if (job.getTriggers().isEmpty()) {
            LOGGER.warn("Skipping job {}", job.getName());
            return;
        }

        try {
            JobKey jobKey = JobKey.jobKey(job.getName().replace(" ", "_"),
                    job.getGroup().replace(" ", "_"));
            if (scheduler.checkExists(jobKey)) {
                LOGGER.warn("Job {} already scheduled", job.getName());
                return;
            }

            JobDetail jobDetail = JobUtility.createJobDetail(job, jobKey);
            JobDataMap dataMap = jobDetail.getJobDataMap();
            dataMap.put(JobUtility.JOB_ID, job.getId());
            scheduler.addJob(jobDetail, false); // false = don’t replace existing

            for (TriggerModel trigger : job.getTriggers()) {
                String name = "trigger-" + job.getName().replace(" ", "_") +
                        "-" + trigger.getName().replace(" ", "_");
                Trigger t = JobUtility.createTrigger(
                        trigger, jobKey, name, job.getGroup()
                );
                scheduler.scheduleJob(t);
            }
            LOGGER.info("Successfully scheduled job - {}", job.getName());
        } catch (Exception ex) {
            LOGGER.error("Failed to schedule job - {}", job.getName());
        }
    }

    public void executeJob(JobModel job) {

        JobKey jobKey = JobKey.jobKey(job.getName().replace(" ", "_"),
                job.getGroup().replace(" ", "_"));
        JobDetail jobDetail = JobUtility.createJobDetail(job, jobKey);
        JobDataMap dataMap = new JobDataMap();
        dataMap.put(JobUtility.JOB_ID, job.getId());
        dataMap.put(JobUtility.EXECUTION_TYPE, ExecutionType.MANUAL.getId());

        // If the Job Key is not yet exist in the Scheduler, Add it now.
        try {
            if (!scheduler.checkExists(jobDetail.getKey())) {
                scheduler.addJob(jobDetail, true);
            }

            scheduler.triggerJob(jobDetail.getKey(), dataMap);
            LOGGER.info("Job {} is triggered", job.getId());
        } catch (SchedulerException e) {
            LOGGER.error("Failed to check job existence, error {}", e.getMessage());
        }
    }
}
