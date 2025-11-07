package xyz.sadiulhakim.execution;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.sadiulhakim.enumeration.JobStatus;
import xyz.sadiulhakim.job.JobModel;

import java.time.LocalDateTime;

@Service
public class JobExecutionAsyncService {

    private final JobExecutionRepository repository;
    private static final Logger LOGGER = LoggerFactory.getLogger(JobExecutionAsyncService.class);

    public JobExecutionAsyncService(JobExecutionRepository repository) {
        this.repository = repository;
    }

    @Transactional
    @Async("defaultExecutor")
    public void updateAsync(JobExecution execution) {
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
    @Async("defaultExecutor")
    public void updateAsync(long runId, JobStatus status, LocalDateTime start, LocalDateTime end) {
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
}
