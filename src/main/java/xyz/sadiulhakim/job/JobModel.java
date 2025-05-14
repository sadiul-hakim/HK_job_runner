package xyz.sadiulhakim.job;

import jakarta.persistence.*;
import xyz.sadiulhakim.trigger.TriggerModel;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class JobModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 55, nullable = false)
    private String name;

    @Column(length = 55, nullable = false, name = "job_group")
    private String group;

    @Column(length = 55, nullable = false)
    private String className;

    private LocalDateTime createdAt;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<TriggerModel> triggers = new ArrayList<>();

    public JobModel() {
    }

    public JobModel(long id, String name, String group, String className, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.group = group;
        this.className = className;
        this.createdAt = createdAt;
    }

    public List<TriggerModel> getTriggers() {
        return triggers;
    }

    public void setTriggers(List<TriggerModel> triggers) {
        this.triggers = triggers;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        JobModel jobModel = (JobModel) o;
        return id == jobModel.id && Objects.equals(name, jobModel.name) && Objects.equals(group, jobModel.group) &&
                Objects.equals(className, jobModel.className) && Objects.equals(createdAt, jobModel.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, group, className, createdAt);
    }
}
