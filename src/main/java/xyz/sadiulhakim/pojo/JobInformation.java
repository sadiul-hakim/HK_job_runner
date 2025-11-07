package xyz.sadiulhakim.pojo;

public record JobInformation(
        long jobId,
        long executionId,
        String name,
        String startTime,
        String endTime,
        String timeTaken,
        String executionType,
        String status
) {
}
