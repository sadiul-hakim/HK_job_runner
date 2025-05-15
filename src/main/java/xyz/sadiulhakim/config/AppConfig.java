package xyz.sadiulhakim.config;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.DirectSchedulerFactory;
import org.quartz.impl.jdbcjobstore.InvalidConfigurationException;
import org.quartz.impl.jdbcjobstore.JobStoreTX;
import org.quartz.simpl.RAMJobStore;
import org.quartz.utils.ConnectionProvider;
import org.quartz.utils.DBConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.SimpleAsyncTaskScheduler;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

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
    public Scheduler scheduler(DataSource dataSource) throws SchedulerException, InvalidConfigurationException {
        QuartzVirtualThreadPool virtualThreadPool = new QuartzVirtualThreadPool();
        DBConnectionManager dbConnectionManager = DBConnectionManager.getInstance();
        dbConnectionManager.addConnectionProvider("myDS", new ConnectionProvider() {
            @Override
            public Connection getConnection() throws SQLException {
                return dataSource.getConnection();
            }

            @Override
            public void shutdown() {
            }

            @Override
            public void initialize() {
            }
        });

        JobStoreTX jobStore = new JobStoreTX();
        jobStore.setDataSource("myDS");
        jobStore.setDriverDelegateClass("org.quartz.impl.jdbcjobstore.PostgreSQLDelegate");
        jobStore.setTablePrefix("QRTZ_");

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
