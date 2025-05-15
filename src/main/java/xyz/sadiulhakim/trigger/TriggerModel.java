package xyz.sadiulhakim.trigger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import xyz.sadiulhakim.job.JobModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class TriggerModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 55)
    private String cronExpression;

    @Column(length = 55)
    private String name;

    @Column(length = 55)
    private String description;

    @Column(length = 55)
    private String timeZone;

    @JsonIgnore
    @ManyToMany(mappedBy = "triggers", fetch = FetchType.LAZY)
    private List<JobModel> jobs = new ArrayList<>();

    public TriggerModel() {
    }

    public TriggerModel(long id, String cronExpression, String name, String description, String timeZone,
                        List<JobModel> jobs) {
        this.id = id;
        this.cronExpression = cronExpression;
        this.name = name;
        this.description = description;
        this.timeZone = timeZone;
        this.jobs = jobs;
    }

    public List<JobModel> getJobs() {
        return jobs;
    }

    public void setJobs(List<JobModel> jobs) {
        this.jobs = jobs;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TriggerModel that = (TriggerModel) o;
        return id == that.id && Objects.equals(cronExpression, that.cronExpression) && Objects.equals(name, that.name)
                && Objects.equals(description, that.description) && Objects.equals(timeZone, that.timeZone) &&
                Objects.equals(jobs, that.jobs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, cronExpression, name, description, timeZone, jobs);
    }
}
