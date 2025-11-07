package xyz.sadiulhakim.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ManualExecutionController {

    private final ManualExecutionService service;

    public ManualExecutionController(ManualExecutionService service) {
        this.service = service;
    }

    @GetMapping("/execute")
    ResponseEntity<?> executeJob(@RequestParam long jobId) {

        service.execute(jobId);
        return ResponseEntity.ok(
                Map.of(
                        "message", "Job is successfully triggered. Please monitor the status."
                )
        );
    }
}
