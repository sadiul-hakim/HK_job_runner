package xyz.sadiulhakim.pojo;

import java.util.ArrayList;
import java.util.List;

public record JobExecutionDisplay(
        List<JobInformation> runningJobs,
        List<JobInformation> history
) {

    public JobExecutionDisplay() {
        this(new ArrayList<>(), new ArrayList<>());
    }

    public void addRunningJobs(JobInformation information) {
        runningJobs.add(information);
    }

    public void addHistory(JobInformation information) {
        history.add(information);
    }
}
