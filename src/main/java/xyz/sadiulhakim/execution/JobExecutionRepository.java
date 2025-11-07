package xyz.sadiulhakim.execution;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import xyz.sadiulhakim.job.JobModel;

import java.util.List;

public interface JobExecutionRepository extends JpaRepository<JobExecution, Long> {
    List<JobExecution> findAllByJob(JobModel job, Pageable page);

    List<JobExecution> findTop300ByEndTimeIsNotNullOrderByEndTimeDesc();
}
