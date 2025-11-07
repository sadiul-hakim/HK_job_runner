package xyz.sadiulhakim.execution;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.sadiulhakim.enumeration.ExecutionType;
import xyz.sadiulhakim.enumeration.JobStatus;
import xyz.sadiulhakim.exception.OperationNowAllowedException;
import xyz.sadiulhakim.job.JobModel;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class JobExecutionService {

    private final JobExecutionRepository repository;

    private static final Logger LOGGER = LoggerFactory.getLogger(JobExecutionService.class);

    @Value("${app.max.page_size:1000}")
    private int maxPageSize;

    public JobExecutionService(JobExecutionRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public JobExecution create(JobModel job, ExecutionType type) {
        try {
            JobExecution execution = new JobExecution();
            execution.setStatus(JobStatus.NEW);
            execution.setJob(job);
            execution.setType(type);
            JobExecution save = repository.save(execution);
            LOGGER.info("Successfully saved Job Execution for job {}", job.getName());
            return save;
        } catch (Exception ex) {
            LOGGER.error("Failed to save Job Execution, error : {}", ex.getMessage());
            return null;
        }
    }

    @Transactional
    public void update(JobExecution execution) {
        try {
            JobExecution oldExecution = repository.findById(execution.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Job Execution is not found with id " + execution.getId()));
            oldExecution.setStatus(execution.getStatus());
            oldExecution.setStartTime(execution.getStartTime());
            oldExecution.setEndTime(execution.getEndTime());
        } catch (Exception ex) {
            LOGGER.error("Failed to update Job Execution, error : {}", ex.getMessage());
        }
    }

    @Transactional
    public void update(long runId, JobStatus status, LocalDateTime start, LocalDateTime end) {
        JobExecution oldExecution = repository.findById(runId)
                .orElseThrow(() -> new EntityNotFoundException("Job Execution is not found with id " + runId));

        if (status != null) {
            oldExecution.setStatus(status);
        }

        if (start != null) {
            oldExecution.setStartTime(start);
        }

        if (end != null) {
            oldExecution.setEndTime(end);
        }
    }

    @Transactional(readOnly = true)
    public List<JobExecution> findAllByJob(JobModel job, int pageNumber, int pageSize) {
        if (pageSize > maxPageSize) {
            throw new OperationNowAllowedException("Page Size can not bigger than " + maxPageSize);
        }
        return repository.findAllByJob(job, PageRequest.of(pageNumber, pageSize));
    }

    @Transactional(readOnly = true)
    public List<JobExecution> findHistory() {
        return repository.findTop300ByEndTimeIsNotNullOrderByEndTimeDesc();
    }
}
