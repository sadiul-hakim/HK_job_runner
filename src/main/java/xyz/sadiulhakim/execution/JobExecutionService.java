package xyz.sadiulhakim.execution;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.sadiulhakim.enumeration.JobStatus;
import xyz.sadiulhakim.exception.OperationNowAllowedException;
import xyz.sadiulhakim.job.JobModel;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class JobExecutionService {

    private final JobExecutionRepository repository;
    private final JobExecutionAsyncService jobExecutionAsyncService;

    private static final Logger LOGGER = LoggerFactory.getLogger(JobExecutionService.class);

    @Value("${app.max.page_size:1000}")
    private int maxPageSize;

    public JobExecutionService(JobExecutionRepository repository, JobExecutionAsyncService jobExecutionAsyncService) {
        this.repository = repository;
        this.jobExecutionAsyncService = jobExecutionAsyncService;
    }

    @Transactional
    public JobExecution create(JobModel job) {
        try {
            JobExecution execution = new JobExecution();
            execution.setStatus(JobStatus.NEW);
            execution.setJob(job);
            JobExecution save = repository.save(execution);
            LOGGER.info("Successfully saved Job Execution for job {}", job.getName());
            return save;
        } catch (Exception ex) {
            LOGGER.error("Failed to save Job Execution, error : {}", ex.getMessage());
            return null;
        }
    }

    public void update(JobExecution execution) {
        jobExecutionAsyncService.updateAsync(execution);
    }

    public void update(long runId, JobStatus status, LocalDateTime start, LocalDateTime end) {
        jobExecutionAsyncService.updateAsync(runId, status, start, end);
    }

    @Transactional(readOnly = true)
    public List<JobExecution> findAllByJob(JobModel job, int pageNumber, int pageSize) {
        if (pageSize > maxPageSize) {
            throw new OperationNowAllowedException("Page Size can not bigger than " + maxPageSize);
        }
        return repository.findAllByJob(job, PageRequest.of(pageNumber, pageSize));
    }
}
