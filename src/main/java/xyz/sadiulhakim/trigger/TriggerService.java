package xyz.sadiulhakim.trigger;

import org.springframework.stereotype.Service;
import xyz.sadiulhakim.exception.EntityNotFoundException;

import java.util.List;

@Service
public class TriggerService {

    private final TriggerRepository repository;

    public TriggerService(TriggerRepository repository) {
        this.repository = repository;
    }

    public TriggerModel save(TriggerModel model) {
        return repository.save(model);
    }

    public TriggerModel findById(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Could not find trigger with id " + id));
    }

    public List<TriggerModel> findAll(List<Long> ids) {
        return repository.findAllByIdIn(ids);
    }

    public void delete(long id) {
        repository.deleteById(id);
    }
}
