package xyz.sadiulhakim.scheduler;

import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import xyz.sadiulhakim.enumeration.JobStatus;
import xyz.sadiulhakim.execution.JobExecution;
import xyz.sadiulhakim.execution.JobExecutionService;
import xyz.sadiulhakim.job.JobModel;
import xyz.sadiulhakim.job.JobService;
import xyz.sadiulhakim.trigger.TriggerModel;
import xyz.sadiulhakim.util.JobUtility;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class JobSchedulerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobSchedulerService.class);

    private final JobService jobService;
    private final Scheduler scheduler;
    private final JobExecutionService executionService;

    public JobSchedulerService(JobService jobService, Scheduler scheduler, JobExecutionService executionService) {
        this.jobService = jobService;
        this.scheduler = scheduler;
        this.executionService = executionService;
    }

    @Scheduled(initialDelay = 10 * 1000, fixedRate = 1000 * 60 * 2, scheduler = "defaultTaskScheduler")
    void applicationIsReady() {
        LOGGER.info("+-----------------------------Scheduling jobs----------------------------------+");
        List<JobModel> jobs = jobService.findAll();
        for (JobModel job : jobs) {
            JobExecution execution = executionService.create(job);
            scheduleJob(job, execution.getId());
        }

        LOGGER.info("+-----------------------------Done Scheduling jobs----------------------------------+");
    }

    public void scheduleJob(JobModel job, long runId) {
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
            jobDetail.getJobDataMap().put("jobId", job.getId());
            jobDetail.getJobDataMap().put("runId", runId);
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
            executionService.update(runId, JobStatus.IN_PROGRESS, LocalDateTime.now(), null);
        } catch (Exception ex) {
            LOGGER.error("Failed to schedule job - {}", job.getName());
        }
    }
}
