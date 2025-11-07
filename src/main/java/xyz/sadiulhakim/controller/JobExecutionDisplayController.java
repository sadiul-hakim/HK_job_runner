package xyz.sadiulhakim.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import xyz.sadiulhakim.service.JobExecutionSseService;

@RestController
public class JobExecutionDisplayController {

    private final JobExecutionSseService sseService;

    public JobExecutionDisplayController(JobExecutionSseService sseService) {
        this.sseService = sseService;
    }

    @GetMapping(path = "/execution-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamExecutions() {
        return sseService.createEmitter();
    }
}
