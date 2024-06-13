package com.test.finalproject.service.impl;

import com.test.finalproject.entity.Task;
import com.test.finalproject.entity.TaskDetail;
import com.test.finalproject.exception.NotFoundException;
import com.test.finalproject.model.converter.TaskDetailDtoConverter;
import com.test.finalproject.model.dtos.taskDetail.TaskDetailReq;
import com.test.finalproject.model.dtos.taskDetail.TaskDetailRes;
import com.test.finalproject.repository.TaskDetailRepository;
import com.test.finalproject.repository.TaskRepository;
import com.test.finalproject.service.TaskDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaskDetailServiceImpl implements TaskDetailService {

    private final TaskDetailRepository repo;
    private final TaskRepository taskRepository;

    @Override
    @Transactional
    public TaskDetailRes addTaskDetail(TaskDetailReq req) {
        final Task task = taskRepository.findById(req.getTaskId())
                .orElseThrow(() -> new NotFoundException("404", "Task not found"));

        final TaskDetail entity = TaskDetailDtoConverter.toEntity(req);

        entity.setTask(task);

        repo.save(entity);
        return TaskDetailDtoConverter.toResponse(entity);
    }

    @Override
    @Transactional
    public TaskDetailRes updateTaskDetail(TaskDetailReq req, int id) {
        final TaskDetail entity = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("404", "Task Detail not found"));

        final Task task = taskRepository.findById(req.getTaskId())
                .orElseThrow(() -> new NotFoundException("404", "Task not found"));

        entity.setName(req.getName());
        entity.setTask(task);

        repo.save(entity);
        return TaskDetailDtoConverter.toResponse(entity);
    }

    @Override
    @Transactional
    public void deleteTaskDetail(int id) {
        final TaskDetail taskDetail = repo.findById(id)
                        .orElseThrow(() -> new NotFoundException("404", "Task Detail not found"));
        repo.delete(taskDetail);
    }
}
