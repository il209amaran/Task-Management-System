package com.example.taskmanagerment.service;

import com.example.taskmanagerment.entity.Task;
import com.example.taskmanagerment.enums.Priority;
import com.example.taskmanagerment.enums.TaskStatus;
import com.example.taskmanagerment.exception.TaskNotFoundException;
import com.example.taskmanagerment.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task task;

    @BeforeEach
    void setUp() {
        task = new Task();
        task.setId(1L);
        task.setTitle("Learn Mockito");
        task.setDescription("Write service-layer tests");
        task.setPriority(Priority.HIGH);
        task.setStatus(TaskStatus.PENDING);
        task.setDueDate(LocalDate.now().plusDays(3));
    }

    // ✅ Test createTask
    @Test
    void testCreateTask_shouldSaveAndReturnTask() {
        when(taskRepository.save(task)).thenReturn(task);

        Task savedTask = taskService.createTask(task);

        assertNotNull(savedTask);
        assertEquals("Learn Mockito", savedTask.getTitle());
        verify(taskRepository, times(1)).save(task);
    }

    // ✅ Test getTaskById (found)
    @Test
    void testGetTaskById_shouldReturnTaskWhenExists() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        Task found = taskService.getTaskById(1L);

        assertEquals("Learn Mockito", found.getTitle());
        verify(taskRepository, times(1)).findById(1L);
    }

    // ❌ Test getTaskById (not found)
    @Test
    void testGetTaskById_shouldThrowWhenNotFound() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        TaskNotFoundException exception = assertThrows(
                TaskNotFoundException.class,
                () -> taskService.getTaskById(99L)
        );

        assertEquals("Task not found with id: 99", exception.getMessage());
        verify(taskRepository, times(1)).findById(99L);
    }

    // ✅ Test getAllTasks
    @Test
    void testGetAllTasks_shouldReturnListOfTasks() {
        Task task2 = new Task();
        task2.setId(2L);
        task2.setTitle("Learn JUnit");
        task2.setDescription("Unit testing basics");
        task2.setPriority(Priority.MEDIUM);
        task2.setStatus(TaskStatus.IN_PROGRESS);

        when(taskRepository.findAll()).thenReturn(List.of(task, task2));

        List<Task> tasks = taskService.getAllTasks();

        assertEquals(2, tasks.size());
        verify(taskRepository, times(1)).findAll();
    }

    // ✅ Test updateTask
    @Test
    void testUpdateTask_shouldUpdateDescription() {
        Task updatedTask = new Task();
        updatedTask.setTitle("Learn Mockito");
        updatedTask.setDescription("Updated: Master Mockito testing");
        updatedTask.setPriority(Priority.HIGH);
        updatedTask.setStatus(TaskStatus.PENDING);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task result = taskService.updateTask(1L, updatedTask);

        assertEquals("Updated: Master Mockito testing", result.getDescription());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    // ✅ Test deleteTask
    @Test
    void testDeleteTask_shouldDeleteWhenExists() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        doNothing().when(taskRepository).delete(task);

        taskService.deleteTask(1L);

        verify(taskRepository, times(1)).delete(task);
    }

    // ❌ Test deleteTask (not found)
    @Test
    void testDeleteTask_shouldThrowWhenNotFound() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.deleteTask(99L));

        verify(taskRepository, times(1)).findById(99L);
    }
}
