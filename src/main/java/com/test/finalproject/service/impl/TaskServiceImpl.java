package com.test.finalproject.service.impl;

import com.test.finalproject.entity.Task;
import com.test.finalproject.entity.User;
import com.test.finalproject.enums.ProgressStatus;
import com.test.finalproject.exception.NotFoundException;
import com.test.finalproject.model.converter.TaskDtoConverter;
import com.test.finalproject.model.dtos.task.TaskReq;
import com.test.finalproject.model.dtos.task.TaskRes;
import com.test.finalproject.repository.TaskRepository;
import com.test.finalproject.repository.UserRepository;
import com.test.finalproject.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    private final UserRepository userRepository;

    @Override
    public List<TaskRes> getTasks() {
        return TaskDtoConverter.toModelList(taskRepository.findAll());
    }

    @Override
    public TaskRes getTask(int id) {
        final Task task = taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("404", "Task not found"));
        return TaskDtoConverter.toResponse(task);
    }

    @Override
    @Transactional
    public TaskRes addTask(TaskReq req) {
        final User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new NotFoundException("404", "User not found"));

        final Task task = TaskDtoConverter.toEntity(req);

        task.setStatus(ProgressStatus.TODO);
        task.setUser(user);

        taskRepository.save(task);

        return TaskDtoConverter.toResponse(task);
    }

    @Override
    @Transactional
    public TaskRes updateTask(TaskReq req, int id) {

        final Task task = taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("404", "Task not found"));

        final User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new NotFoundException("404", "User not found"));

        task.setName(req.getName());
        task.setUser(user);

        taskRepository.save(task);
        return TaskDtoConverter.toResponse(task);
    }

    @Override
    @Transactional
    public TaskRes updateTaskCompleted(int id) {
        final Task task = taskRepository.findByIdAndStatus(id,ProgressStatus.TODO)
                .orElseThrow(() -> new NotFoundException("404", "Task not found or completed"));

        task.setStatus(ProgressStatus.COMPLETE);
        task.setCompleteDate(new Date(System.currentTimeMillis()));

        taskRepository.save(task);
        return TaskDtoConverter.toResponse(task);
    }

    @Override
    @Transactional
    public void deleteTask(int id) {
        final Task task = taskRepository.findByIdAndStatus(id,ProgressStatus.TODO)
                        .orElseThrow(() -> new NotFoundException("404", "Task not found or completed"));
        taskRepository.delete(task);
    }


}
