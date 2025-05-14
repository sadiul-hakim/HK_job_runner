package xyz.sadiulhakim.job;

import org.springframework.stereotype.Service;
import xyz.sadiulhakim.exception.EntityNotFoundException;
import xyz.sadiulhakim.job.pojo.TriggerAssignmentPojo;
import xyz.sadiulhakim.trigger.TriggerModel;
import xyz.sadiulhakim.trigger.TriggerService;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class JobService {

    private final JobRepository repository;
    private final TriggerService triggerService;

    public JobService(JobRepository repository, TriggerService triggerService) {
        this.repository = repository;
        this.triggerService = triggerService;
    }

    public JobModel save(JobModel model) {
        model.setCreatedAt(LocalDateTime.now());
        return repository.save(model);
    }

    public JobModel findById(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Could not find job with id " + id));
    }

    public List<JobModel> findAll(){
        return repository.findAll();
    }

    public List<TriggerModel> findAllTriggers(long jobId) {
        JobModel job = findById(jobId);
        return job.getTriggers();
    }

    public boolean assignTrigger(TriggerAssignmentPojo pojo) {

        try {
            if (pojo.triggers().isEmpty())
                return false;

            JobModel job = findById(pojo.jobId());
            List<TriggerModel> triggerModels = triggerService.findAll(pojo.triggers());
            for (TriggerModel triggerModel : triggerModels) {
                if (!job.getTriggers().contains(triggerModel)) {
                    job.getTriggers().add(triggerModel);
                }
            }
            save(job);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public boolean removeTrigger(TriggerAssignmentPojo pojo) {

        try {
            if (pojo.triggers().isEmpty())
                return false;

            JobModel job = findById(pojo.jobId());
            List<TriggerModel> triggerModels = triggerService.findAll(pojo.triggers());
            for (TriggerModel triggerModel : triggerModels) {
                job.getTriggers().remove(triggerModel);
            }
            save(job);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public void delete(long id) {
        repository.deleteById(id);
    }
}
