package xyz.sadiulhakim.execution;

import jakarta.persistence.*;
import xyz.sadiulhakim.enumeration.ExecutionType;
import xyz.sadiulhakim.enumeration.JobStatus;
import xyz.sadiulhakim.job.JobModel;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "job_execution")
public class JobExecution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private JobStatus status;

    @Enumerated(EnumType.STRING)
    private ExecutionType type;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @ManyToOne
    private JobModel job;

    public JobExecution(Long id, JobStatus status, ExecutionType type, LocalDateTime startTime, LocalDateTime endTime,
                        JobModel job) {
        this.id = id;
        this.status = status;
        this.type = type;
        this.startTime = startTime;
        this.endTime = endTime;
        this.job = job;
    }

    public JobExecution() {
    }

    public ExecutionType getType() {
        return type;
    }

    public void setType(ExecutionType type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public JobModel getJob() {
        return job;
    }

    public void setJob(JobModel job) {
        this.job = job;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JobExecution other)) return false;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return 120;
    }
}
