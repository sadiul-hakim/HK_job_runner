# Quartz Scheduler with Spring Boot - Tutorial

This guide explains how to use Quartz Scheduler to build a dynamic job scheduling system in a Spring Boot application.

---

## 📘 1. Required Quartz Classes and Their Roles

| Quartz Class/Interface | Role                                                                       |
|------------------------|----------------------------------------------------------------------------|
| `Scheduler`            | Main API for scheduling jobs. Used to schedule and control job execution.  |
| `Job`                  | Interface to define actual job logic. Each job must implement `execute()`. |
| `JobDetail`            | Defines a job instance: job class, identity, and data.                     |
| `Trigger`              | Defines the schedule for job execution (e.g., using a cron expression).    |
| `JobBuilder`           | Used to create `JobDetail` instances.                                      |
| `TriggerBuilder`       | Used to create `Trigger` instances.                                        |
| `CronScheduleBuilder`  | Used to build a cron-based trigger.                                        |
| `JobDataMap`           | A map to pass parameters to the job instance.                              |

---

## 🔁 2. Flow of Quartz Job Execution

1. **Create a Job class** implementing `org.quartz.Job`.
2. **Create a JobDetail** object using `JobBuilder`, which includes job metadata.
3. **Create a Trigger** using `TriggerBuilder`, typically with a cron schedule.
4. **Schedule the Job** using a `Scheduler` instance.
5. Quartz **executes the job** based on the trigger.

---

## ✅ 3. Example: Spring Boot + Quartz

### 📦 Maven Dependency

```xml

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-quartz</artifactId>
</dependency>
```

### 🧑‍💻 Create a Job Class

```java
package com.example.jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.time.LocalDateTime;

public class HelloJob implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println("Hello from Quartz Job! Time: " + LocalDateTime.now());
    }
}
```

### ⚙️ Configure Quartz Job and Trigger

```java
package com.example.config;

import com.example.jobs.HelloJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail helloJobDetail() {
        return JobBuilder.newJob(HelloJob.class)
                .withIdentity("helloJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger helloJobTrigger(JobDetail helloJobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(helloJobDetail)
                .withIdentity("helloTrigger")
                .withSchedule(CronScheduleBuilder.cronSchedule("0/10 * * * * ?")) // every 10 seconds
                .build();
    }
}
```

### 🚀 Output

When the application runs, it will print:

```
Hello from Quartz Job! Time: 2025-05-14T10:00:00
Hello from Quartz Job! Time: 2025-05-14T10:00:10
Hello from Quartz Job! Time: 2025-05-14T10:00:20
        ...
```

### 💡 Tips

- Use JobDataMap to pass parameters to jobs dynamically.
- To persist jobs/triggers, configure Quartz with JDBC JobStore.
- Use SchedulerFactoryBean to expose and customize the Scheduler.

## 4. Running Jobs in Virtual Thread and Safe Job and Trigger information in database instead of RAM

> Make sure you have database configured

### Properties
```
spring.quartz.jdbc.initialize-schema=always
spring.quartz.job-store-type=jdbc
spring.quartz.jdbc.platform=postgres
spring.quartz.wait-for-jobs-to-complete-on-shutdown=true
```

### Code Configuration

```java
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
```
