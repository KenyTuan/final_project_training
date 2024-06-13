package com.test.finalproject.repository;

import com.test.finalproject.entity.Task;
import com.test.finalproject.enums.ProgressStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {
    Optional<Task> findByIdAndStatus(Integer id, ProgressStatus status);
}
