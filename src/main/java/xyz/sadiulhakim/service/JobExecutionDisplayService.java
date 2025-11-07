package xyz.sadiulhakim.service;

import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import xyz.sadiulhakim.execution.JobExecution;
import xyz.sadiulhakim.execution.JobExecutionService;
import xyz.sadiulhakim.pojo.JobExecutionDisplay;
import xyz.sadiulhakim.pojo.JobInformation;
import xyz.sadiulhakim.util.JobUtility;

import java.util.List;

@Service
public class JobExecutionDisplayService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobExecutionDisplayService.class);

    private final Scheduler scheduler;
    private final JobExecutionSseService sseService;
    private final JobExecutionService executionService;

    public JobExecutionDisplayService(Scheduler scheduler, JobExecutionSseService sseService,
                                      JobExecutionService executionService) {
        this.scheduler = scheduler;
        this.sseService = sseService;
        this.executionService = executionService;
    }

    @Scheduled(cron = "0/5 * * * * ?", scheduler = "defaultTaskScheduler")
    public void getExecutionsInformation() {
        JobExecutionDisplay display = new JobExecutionDisplay();

        try {
            List<JobExecutionContext> contexts = scheduler.getCurrentlyExecutingJobs();
            for (JobExecutionContext context : contexts) {
                JobInformation information = JobUtility.extractInformation(context);
                display.addRunningJobs(information);
            }

            List<JobExecution> history = executionService.findHistory();
            for (JobExecution execution : history) {
                display.addHistory(JobUtility.extractInformation(execution));
            }

            sseService.broadcast(display);
        } catch (SchedulerException e) {
            LOGGER.error("getExecutionsInformation() Error, {}", e.getMessage());
        }
    }
}
