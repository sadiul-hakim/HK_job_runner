package xyz.sadiulhakim.config;

import org.quartz.utils.ConnectionProvider;
import org.quartz.utils.DBConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.SimpleAsyncTaskScheduler;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

@EnableAsync
@Configuration
@EnableScheduling
public class AppConfig {

    @Bean
    TaskScheduler defaultTaskScheduler() {
        var scheduler = new SimpleAsyncTaskScheduler();
        scheduler.setVirtualThreads(true);
        return scheduler;
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(DataSource dataSource) {
        // Step 1: Register DataSource for Quartz
        DBConnectionManager.getInstance().addConnectionProvider("quartzDataSource", new ConnectionProvider() {
            @Override
            public Connection getConnection() throws SQLException {
                return dataSource.getConnection();
            }

            @Override
            public void shutdown() {}

            @Override
            public void initialize() {}
        });

        // Step 2: Configure Quartz
        Properties props = getProperties();

        // Step 3: Build Factory
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setDataSource(dataSource);
        factory.setQuartzProperties(props);
        factory.setWaitForJobsToCompleteOnShutdown(true);
        factory.setOverwriteExistingJobs(true);
        return factory;
    }

    private static Properties getProperties() {
        Properties props = new Properties();
        props.setProperty("org.quartz.scheduler.instanceName", "VirtualScheduler");
        props.setProperty("org.quartz.jobStore.class", "org.quartz.impl.jdbcjobstore.JobStoreTX");
        props.setProperty("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.PostgreSQLDelegate");
        props.setProperty("org.quartz.jobStore.dataSource", "quartzDataSource");
        props.setProperty("org.quartz.jobStore.tablePrefix", "QRTZ_");

        // Use Virtual Thread Pool
        props.setProperty("org.quartz.threadPool.class", "xyz.sadiulhakim.config.QuartzVirtualThreadPool");
        return props;
    }
}
