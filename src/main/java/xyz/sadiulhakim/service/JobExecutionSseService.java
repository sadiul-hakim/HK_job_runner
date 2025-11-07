package xyz.sadiulhakim.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import xyz.sadiulhakim.pojo.JobExecutionDisplay;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class JobExecutionSseService {

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public SseEmitter createEmitter() {
        SseEmitter emitter = new SseEmitter(0L); // no timeout

        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError((e) -> emitters.remove(emitter));

        return emitter;
    }

    public void broadcast(JobExecutionDisplay display) {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("executions-update")
                        .data(display)
                );
            } catch (Exception ex) {
                emitters.remove(emitter);
            }
        }
    }
}
