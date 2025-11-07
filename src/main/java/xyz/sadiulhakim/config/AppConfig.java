package xyz.sadiulhakim.config;

import org.quartz.spi.JobFactory;
import org.quartz.utils.ConnectionProvider;
import org.quartz.utils.DBConnectionManager;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
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
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@EnableAsync
@Configuration
@EnableScheduling
public class AppConfig {

    private final AutowireCapableBeanFactory beanFactory;

    public AppConfig(AutowireCapableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Bean
    public JobFactory jobFactory() {
        return new AutowiringSpringBeanJobFactory(beanFactory);
    }

    @Bean
    TaskScheduler defaultTaskScheduler() {
        var scheduler = new SimpleAsyncTaskScheduler();
        scheduler.setVirtualThreads(true);
        return scheduler;
    }

    @Bean
    Executor defaultExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(JobFactory jobFactory, DataSource dataSource) {
        // Step 1: Register DataSource for Quartz
        DBConnectionManager.getInstance().addConnectionProvider("quartzDataSource", new ConnectionProvider() {
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

        // Step 2: Configure Quartz
        Properties props = getProperties();

        // Step 3: Build Factory
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setJobFactory(jobFactory);
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
