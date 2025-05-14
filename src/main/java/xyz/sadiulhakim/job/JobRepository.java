package xyz.sadiulhakim.job;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<JobModel,Long> {

}
