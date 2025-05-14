package xyz.sadiulhakim.job.pojo;

import java.util.List;

public record TriggerAssignmentPojo(
        long jobId,
        List<Long> triggers
) {
}
