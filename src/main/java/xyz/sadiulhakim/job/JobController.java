package xyz.sadiulhakim.job;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import xyz.sadiulhakim.job.pojo.TriggerAssignmentPojo;

import java.util.Map;

@RestController
@RequestMapping("/job")
public class JobController {

    private final JobService service;

    public JobController(JobService service) {
        this.service = service;
    }

    @PostMapping
    ResponseEntity<JobModel> save(@RequestBody JobModel jobModel) {
        JobModel saved = service.save(jobModel);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{id}")
    ResponseEntity<JobModel> getById(@PathVariable long id) {
        JobModel job = service.findById(id);
        return ResponseEntity.ok(job);
    }

    @GetMapping("/get-all-triggers/{id}")
    ResponseEntity<?> getAllTriggers(@PathVariable long id) {
        var triggers = service.findAllTriggers(id);
        return ResponseEntity.ok(triggers);
    }

    @PostMapping("/assign-triggers")
    ResponseEntity<?> assignTriggers(@RequestBody TriggerAssignmentPojo pojo) {
        boolean assigned = service.assignTrigger(pojo);
        return assigned ?
                ResponseEntity.ok(Map.of("message", "Successfully assigned triggers")) :
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                        Map.of("message", "Could not assign triggers")
                );
    }

    @PostMapping("/remove-triggers")
    ResponseEntity<?> removeTriggers(@RequestBody TriggerAssignmentPojo pojo) {
        boolean assigned = service.removeTrigger(pojo);
        return assigned ?
                ResponseEntity.ok(Map.of("message", "Successfully assigned triggers")) :
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                        Map.of("message", "Could not assign triggers")
                );
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> delete(@PathVariable long id) {
        service.delete(id);
        return ResponseEntity.ok(Map.of("message", "Successfully deleted job " + id));
    }
}
