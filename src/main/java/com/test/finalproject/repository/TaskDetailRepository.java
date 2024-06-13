package com.test.finalproject.repository;

import com.test.finalproject.entity.TaskDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskDetailRepository extends JpaRepository<TaskDetail, Integer> {
}
