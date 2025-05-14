package xyz.sadiulhakim.trigger;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TriggerRepository extends JpaRepository<TriggerModel, Long> {
    List<TriggerModel> findAllByIdIn(List<Long> ids);

    List<Long> id(long id);
}
