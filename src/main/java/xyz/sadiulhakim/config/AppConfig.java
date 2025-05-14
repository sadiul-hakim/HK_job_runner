package xyz.sadiulhakim.config;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.DirectSchedulerFactory;
import org.quartz.simpl.RAMJobStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.SimpleAsyncTaskScheduler;

@EnableAsync
@Configuration
@EnableScheduling
public class AppConfig {

    @Bean
    TaskScheduler defaultTaskScheduler() {
        SimpleAsyncTaskScheduler taskScheduler = new SimpleAsyncTaskScheduler();
        taskScheduler.setVirtualThreads(true);
        return taskScheduler;
    }

    @Bean
    public Scheduler scheduler() throws SchedulerException {
        QuartzVirtualThreadPool virtualThreadPool = new QuartzVirtualThreadPool();
        RAMJobStore jobStore = new RAMJobStore();

        DirectSchedulerFactory.getInstance().createScheduler(
                "VirtualScheduler",
                "VTGroup",
                virtualThreadPool,
                jobStore
        );

        Scheduler scheduler = DirectSchedulerFactory.getInstance().getScheduler("VirtualScheduler");
        scheduler.start();
        return scheduler;
    }
}
