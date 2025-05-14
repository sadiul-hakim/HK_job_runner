package xyz.sadiulhakim.trigger;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/trigger")
public class TriggerController {
    private final TriggerService service;

    public TriggerController(TriggerService service) {
        this.service = service;
    }

    @PostMapping
    ResponseEntity<TriggerModel> save(@RequestBody TriggerModel model){
        TriggerModel saved = service.save(model);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{id}")
    ResponseEntity<TriggerModel> findById(@PathVariable long id){
        TriggerModel trigger = service.findById(id);
        return ResponseEntity.ok(trigger);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> delete(@PathVariable long id){
        service.delete(id);
        return ResponseEntity.ok(Map.of("message", "Successfully deleted trigger " + id));
    }
}
