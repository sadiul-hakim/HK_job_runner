package xyz.sadiulhakim.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import xyz.sadiulhakim.execution.JobExecution;
import xyz.sadiulhakim.execution.JobExecutionService;
import xyz.sadiulhakim.job.JobModel;
import xyz.sadiulhakim.job.JobService;
import xyz.sadiulhakim.scheduler.JobSchedulerService;

@Service
public class ManualExecutionService {

    private final JobService jobService;
    private final JobExecutionService jobExecutionService;
    private final JobSchedulerService schedulerService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ManualExecutionService.class);

    public ManualExecutionService(JobService jobService, JobExecutionService jobExecutionService,
                                  JobSchedulerService schedulerService) {
        this.jobService = jobService;
        this.jobExecutionService = jobExecutionService;
        this.schedulerService = schedulerService;
    }

    @Async("defaultExecutor")
    public void execute(long jobId) {

        JobModel job = jobService.findById(jobId);
        JobExecution save = jobExecutionService.create(job);
        schedulerService.scheduleJob(job, save.getId()); // TODO : Fix This
        LOGGER.info("Job {} is triggered", job.getName());
    }
}
