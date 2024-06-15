package com.test.finalproject.service;

import com.test.finalproject.constants.MessageException;
import com.test.finalproject.entity.Task;
import com.test.finalproject.entity.User;
import com.test.finalproject.enums.ProgressStatus;
import com.test.finalproject.exception.BadRequestException;
import com.test.finalproject.exception.NotFoundException;
import com.test.finalproject.model.dtos.task.TaskReq;
import com.test.finalproject.model.dtos.task.TaskRes;
import com.test.finalproject.repository.TaskRepository;
import com.test.finalproject.repository.UserRepository;
import com.test.finalproject.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    private Task task;
    private TaskReq taskReq;
    @BeforeEach
    public void setUp() {
        task = Task.builder()
                .id(1)
                .status(ProgressStatus.TODO)
                .completeDate(null)
                .name("Feature Manager User")
                .build();

        taskReq = TaskReq.builder()
                .name("Feature Manager Task")
                .userId(1)
                .build();
    }

    //====================Test_List=============================
    @Test
    public void testGetTasks() {
        List<Task> tasks = new ArrayList<>();
        tasks.add(task);

        when(taskRepository.findAll()).thenReturn(tasks);

        List<TaskRes> taskResList = taskService.getTasks();

        assertThat(taskResList).isNotNull();
        assertThat(taskResList.size()).isEqualTo(1);
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    public void testGetTasks_ReturnEmptyList() {

        lenient().when(taskRepository.findAll()).thenReturn(new ArrayList<>());

        List<TaskRes> taskResList = taskService.getTasks();

        verify(taskRepository,times(1)).findAll();
        assertThat(taskResList).isEmpty();
    }

    //===================Test_Get_Id============================
    @Test
    public void testGetTasksByID_WhenSuccess() {

        when(taskRepository.findById(anyInt())).thenReturn(Optional.of(task));
        TaskRes taskRes = taskService.getTask(1);

        assertNotNull(taskRes);
        assertEquals(taskRes.name(),task.getName());
        verify(taskRepository, times(1)).findById(anyInt());
    }

    @Test
    public void testGetTask_WhenNotFoundTask() {
        when(taskRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.getTask(2))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(MessageException.NOT_FOUND_TASK);

        verify(taskRepository,times(1)).findById(2);
        assertThat(taskRepository.findById(2)).isEmpty();
    }

    //====================Test_Add=============================

    @Test
    public void testAddTask_WhenSuccess() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(new User()));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskRes savedTask = taskService.addTask(taskReq);

        assertNotNull(savedTask);
        assertEquals(savedTask.name(),"Feature Manager Task");
        verify(userRepository,times(1)).findById(anyInt());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    public void testAddTask_WhenNotFoundUser() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.addTask(taskReq))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(MessageException.NOT_FOUND_USER);

        verify(userRepository, times(1)).findById(anyInt());
        verify(taskRepository,never()).save(any(Task.class));
        assertThat(userRepository.findById(1)).isEmpty();
    }

    //====================Test_Update==========================
    @Test
    public void testUpdateTask_WhenSuccess() {
        when(taskRepository.findById(anyInt())).thenReturn(Optional.of(task));
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(new User()));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskRes updatedTask = taskService.updateTask(taskReq, 1);

        assertNotNull(updatedTask);
        assertEquals(updatedTask.name(),"Feature Manager Task");
        verify(taskRepository, times(1)).findById(anyInt());
        verify(userRepository,times(1)).findById(anyInt());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    public void testUpdateTask_WhenNotFoundTask() {
        when(taskRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.updateTask(taskReq,1))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(MessageException.NOT_FOUND_TASK);

        verify(taskRepository, times(1)).findById(anyInt());
        verify(taskRepository,never()).save(any(Task.class));

        assertThat(taskRepository.findById(anyInt())).isEmpty();
    }

    @Test
    public void testUpdateTask_WhenNotFoundUser() {
        when(taskRepository.findById(anyInt())).thenReturn(Optional.of(task));
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.updateTask(taskReq,1))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(MessageException.NOT_FOUND_USER);

        verify(taskRepository, times(1)).findById(anyInt());
        verify(userRepository,times(1)).findById(anyInt());
        verify(taskRepository,never()).save(any(Task.class));
        assertThat(taskRepository.findById(anyInt())).isNotNull();
        assertThat(userRepository.findById(anyInt())).isEmpty();
    }

    //=====================Test_Update_Complete================
    @Test
    public void testUpdateTaskCompleted_WhenSuccess() {
        when(taskRepository.findById(anyInt())).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskRes completedTask = taskService.updateTaskCompleted(1);

        assertThat(completedTask).isNotNull();
        assertThat(completedTask.status()).isEqualTo(ProgressStatus.COMPLETE);
        assertThat(completedTask.completeDate()).isNotNull();
        verify(taskRepository, times(1)).findById(anyInt());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    public void testUpdateTaskCompleted_WhenNotFoundTask() {
        when(taskRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.updateTaskCompleted(1))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(MessageException.NOT_FOUND_TASK);

        verify(taskRepository, times(1)).findById(anyInt());
        verify(taskRepository,never()).save(any(Task.class));
        assertThat(taskRepository.findById(anyInt())).isEmpty();
    }

    //====================Test_Delete==========================
    @Test
    public void testDeleteTask_WhenSuccess() {
        when(taskRepository.findById(anyInt())).thenReturn(Optional.of(task));

        taskService.deleteTask(1);

        verify(taskRepository, times(1)).findById(anyInt());
        verify(taskRepository, times(1)).delete(any(Task.class));
    }

    @Test
    public void testDeleteTask_WhenNotFoundTask() {
        when(taskRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.deleteTask(anyInt()))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(MessageException.NOT_FOUND_TASK);
        verify(taskRepository, times(1)).findById(anyInt());
        verify(taskRepository,never()).delete(any(Task.class));
        assertThat(taskRepository.findById(anyInt())).isEmpty();
    }

    @Test
    public void testDeleteTask_WhenTaskStatusCompleted() {
        task.setStatus(ProgressStatus.COMPLETE);

        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));

        assertThatThrownBy(() -> taskService.deleteTask(task.getId()))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining(MessageException.TASK_IS_COMPLETED);

        verify(taskRepository,times(1)).findById(anyInt());
        verify(taskRepository, never()).delete(any(Task.class));
    }

}
