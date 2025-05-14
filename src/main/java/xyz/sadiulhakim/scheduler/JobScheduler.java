package xyz.sadiulhakim.scheduler;

import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import xyz.sadiulhakim.job.JobModel;
import xyz.sadiulhakim.job.JobService;
import xyz.sadiulhakim.trigger.TriggerModel;
import xyz.sadiulhakim.util.JobUtility;

import java.util.List;

@Component
public class JobScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobScheduler.class);

    private final JobService jobService;
    private final Scheduler scheduler;

    public JobScheduler(JobService jobService, Scheduler scheduler) {
        this.jobService = jobService;
        this.scheduler = scheduler;
    }

    @Scheduled(initialDelay = 10 * 1000, fixedRate = 1000 * 60 * 20, scheduler = "defaultTaskScheduler")
    void applicationIsReady() {
        LOGGER.info("+-----------------------------Scheduling jobs----------------------------------+");
        List<JobModel> jobs = jobService.findAll();
        for (JobModel job : jobs) {
            if (job.getTriggers().isEmpty()) {
                LOGGER.warn("Skipping job {}", job.getName());
                continue;
            }

            try {
                JobKey jobKey = JobKey.jobKey(job.getName(), job.getGroup());
                if (scheduler.checkExists(jobKey)) {
                    LOGGER.warn("Job {} already scheduled", job.getName());
                    continue;
                }

                JobDetail jobDetail = JobUtility.createJobDetail(job, jobKey);
                scheduler.addJob(jobDetail, false); // false = don’t replace existing

                for (TriggerModel trigger : job.getTriggers()) {
                    String name = "trigger-" + job.getName() + "-" + trigger.getName();
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

        LOGGER.info("+-----------------------------Done Scheduling jobs----------------------------------+");
    }
}
