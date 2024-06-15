package com.test.finalproject.service;

import com.test.finalproject.constants.MessageException;
import com.test.finalproject.entity.Task;
import com.test.finalproject.entity.TaskDetail;
import com.test.finalproject.enums.ProgressStatus;
import com.test.finalproject.exception.NotFoundException;
import com.test.finalproject.model.dtos.taskDetail.TaskDetailReq;
import com.test.finalproject.model.dtos.taskDetail.TaskDetailRes;
import com.test.finalproject.repository.TaskDetailRepository;
import com.test.finalproject.repository.TaskRepository;
import com.test.finalproject.service.impl.TaskDetailServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskDetailServiceTest {

    @Mock
    private TaskDetailRepository taskDetailRepository;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskDetailServiceImpl taskDetailService;

    private TaskDetail taskDetail;

    private Task task;

    private TaskDetailReq taskDetailReq;

    @BeforeEach
    void setUp() {
        task = Task.builder()
                .id(1)
                .status(ProgressStatus.TODO)
                .completeDate(null)
                .name("Feature Manager User")
                .build();

        taskDetail = TaskDetail.builder()
                .name("Feature Register User")
                .id(1)
                .task(task)
                .build();

        taskDetailReq = TaskDetailReq.builder()
                .taskId(1)
                .name("Feature Login User")
                .build();
    }


    @Test
    public void testAddTaskDetail_WhenSuccess() throws Exception {
        when(taskRepository.findById(anyInt())).thenReturn(Optional.of(task));
        when(taskDetailRepository.save(any(TaskDetail.class))).thenReturn(taskDetail);

        TaskDetailRes taskDetailRes = taskDetailService.addTaskDetail(taskDetailReq);

        verify(taskRepository,times(1)).findById(anyInt());
        verify(taskDetailRepository,times(1)).save(any(TaskDetail.class));
        assertThat(taskDetailRes).isNotNull();
        assertThat(taskDetailRes.name()).isEqualTo("Feature Login User");
    }

    @Test
    public void testAddTaskDetail_WhenNotFoundTask() throws Exception {
        when(taskRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskDetailService.addTaskDetail(taskDetailReq))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(MessageException.NOT_FOUND_TASK);

        verify(taskRepository,times(1)).findById(anyInt());
        verify(taskDetailRepository,never()).save(any(TaskDetail.class));
    }

    //=====================Test_UpdateTaskDetail====================================
    @Test
    public void testUpdateTaskDetail_WhenSuccess() throws Exception {
        when(taskRepository.findById(anyInt())).thenReturn(Optional.of(task));
        when(taskDetailRepository.findById(anyInt())).thenReturn(Optional.of(taskDetail));
        when(taskDetailRepository.save(any(TaskDetail.class))).thenReturn(taskDetail);

        TaskDetailRes taskDetailRes = taskDetailService.updateTaskDetail(taskDetailReq,anyInt());

        verify(taskRepository,times(1)).findById(anyInt());
        verify(taskDetailRepository,times(1)).findById(anyInt());
        verify(taskDetailRepository,times(1)).save(any(TaskDetail.class));
        assertThat(taskDetailRes).isNotNull();
        assertThat(taskDetailRes.name()).isEqualTo("Feature Login User");
    }

    @Test
    public void testUpdateTaskDetail_WhenNotFoundTask() throws Exception {
        when(taskDetailRepository.findById(anyInt())).thenReturn(Optional.of(taskDetail));
        when(taskRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskDetailService.updateTaskDetail(taskDetailReq,anyInt()))
                .isInstanceOf(NotFoundException.class)
                        .hasMessageContaining(MessageException.NOT_FOUND_TASK);

        verify(taskDetailRepository,times(1)).findById(anyInt());
        verify(taskRepository,times(1)).findById(anyInt());
        verify(taskDetailRepository,never()).save(any(TaskDetail.class));
    }

    @Test
    public void testUpdateTaskDetail_WhenNotFoundTaskDetail() throws Exception {
        when(taskDetailRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskDetailService.updateTaskDetail(taskDetailReq,anyInt()))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(MessageException.NOT_FOUND_TASK_DETAIL);

        verify(taskDetailRepository,times(1)).findById(anyInt());
        verify(taskRepository,never()).findById(anyInt());
        verify(taskDetailRepository,never()).save(any(TaskDetail.class));
    }

    //=======================Test_DeleteTaskDetail========================================
    @Test
    public void testDeleteTaskDetail_WhenSuccess() throws Exception {
        when(taskDetailRepository.findById(anyInt())).thenReturn(Optional.of(taskDetail));

        taskDetailService.deleteTaskDetail(taskDetail.getId());

        verify(taskDetailRepository,times(1)).findById(anyInt());
        verify(taskDetailRepository,times(1)).delete(any(TaskDetail.class));
    }

    @Test
    public void testDeleteTaskDetail_WhenNotFoundTaskDetail() throws Exception {
        when(taskDetailRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskDetailService.deleteTaskDetail(taskDetail.getId()))
                .isInstanceOf(NotFoundException.class)
                        .hasMessageContaining(MessageException.NOT_FOUND_TASK_DETAIL);

        verify(taskDetailRepository,times(1)).findById(anyInt());
        verify(taskDetailRepository,never()).delete(any(TaskDetail.class));
    }

    @Test
    public void testDeleteTaskDetail_WhenTaskComplete() throws Exception {
        task.setStatus(ProgressStatus.COMPLETE);
        when(taskDetailRepository.findById(anyInt())).thenReturn(Optional.of(taskDetail));

        assertThatThrownBy(() -> taskDetailService.deleteTaskDetail(taskDetail.getId()))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(MessageException.TASK_IS_COMPLETED);

        verify(taskDetailRepository,times(1)).findById(anyInt());
        verify(taskDetailRepository,never()).delete(any(TaskDetail.class));
    }

}
