package com.example.taskmanagerment.controller;

import com.example.taskmanagerment.entity.Task;
import com.example.taskmanagerment.enums.Priority;
import com.example.taskmanagerment.enums.TaskStatus;
import com.example.taskmanagerment.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(TaskController.class)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    @Test
    void testCreateTask_withInvalidData_shouldReturn400() throws Exception {
        // Invalid input (short title, empty description, invalid enum)
        String invalidTaskJson = """
            {
              "title": "Hi",
              "description": "",
              "status": "INVALID_STATUS",
              "priority": "HIGH"
            }
            """;

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidTaskJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Invalid value for 'status'")));
    }

    @Test
    void testCreateTask_withValidData_shouldReturn201() throws Exception {
        // Arrange (mock service response)
        Task savedTask = new Task();
        savedTask.setId(1L);
        savedTask.setTitle("Finish Spring Boot module");
        savedTask.setDescription("Complete task management API");
        savedTask.setStatus(TaskStatus.PENDING);
        savedTask.setPriority(Priority.HIGH);

        // When controller calls service.createTask(), return our fake task
        Mockito.when(taskService.createTask(Mockito.any(Task.class))).thenReturn(savedTask);

        // Valid JSON request body
        String validTaskJson = """
        {
          "title": "Finish Spring Boot module",
          "description": "Complete task management API",
          "status": "PENDING",
          "priority": "HIGH"
        }
        """;

        // Act + Assert
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validTaskJson))
                .andExpect(status().isCreated())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Finish Spring Boot module")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Complete task management API")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("PENDING")));
    }

    @Test
    void testGetTaskById_withValidId_shouldReturn200() throws Exception {
        // Arrange
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Learn Spring Boot Testing");
        task.setDescription("Understand MockMvc and Unit Testing");
        task.setStatus(TaskStatus.IN_PROGRESS);
        task.setPriority(Priority.MEDIUM);

        // Mock the service call
        Mockito.when(taskService.getTaskById(1L)).thenReturn(task);

        // Act + Assert
        mockMvc.perform(get("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Learn Spring Boot Testing")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("IN_PROGRESS")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Understand MockMvc and Unit Testing")));
    }

    @Test
    void testGetTaskById_withInvalidId_shouldReturn404() throws Exception {
        // Mock service to throw exception when task not found
        Mockito.when(taskService.getTaskById(99L))
                .thenThrow(new com.example.taskmanagerment.exception.TaskNotFoundException("Task not found with id: 99"));

        // Act + Assert
        mockMvc.perform(get("/api/tasks/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Task not found with id: 99")));
    }

    @Test
    void testUpdateTaskDescription_withValidId_shouldReturn200() throws Exception {
        // Arrange — existing task in DB
        Task existingTask = new Task();
        existingTask.setId(1L);
        existingTask.setTitle("Learn Spring Boot");
        existingTask.setDescription("Old Description");
        existingTask.setStatus(TaskStatus.PENDING);
        existingTask.setPriority(Priority.MEDIUM);

        // Updated task returned by service after update
        Task updatedTask = new Task();
        updatedTask.setId(1L);
        updatedTask.setTitle("Learn Spring Boot");
        updatedTask.setDescription("Updated Description - Added testing section");
        updatedTask.setStatus(TaskStatus.PENDING);
        updatedTask.setPriority(Priority.MEDIUM);

        // Mock service behavior
        Mockito.when(taskService.updateTask(Mockito.eq(1L), Mockito.any(Task.class))).thenReturn(updatedTask);

        // Valid JSON body
        String updatedJson = """
        {
          "title": "Learn Spring Boot",
          "description": "Updated Description - Added testing section",
          "status": "PENDING",
          "priority": "MEDIUM"
        }
        """;

        // Act + Assert
        mockMvc.perform(put("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedJson))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Updated Description - Added testing section")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Learn Spring Boot")));
    }

    @Test
    void testUpdateTaskDescription_withInvalidId_shouldReturn404() throws Exception {
        // Mock service to throw exception
        Mockito.when(taskService.updateTask(Mockito.eq(99L), Mockito.any(Task.class)))
                .thenThrow(new com.example.taskmanagerment.exception.TaskNotFoundException("Task not found with id: 99"));

        // JSON body
        String updatedJson = """
        {
          "title": "Invalid Update",
          "description": "Trying to update non-existing task",
          "status": "PENDING",
          "priority": "LOW"
        }
        """;

        // Act + Assert
        mockMvc.perform(put("/api/tasks/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedJson))
                .andExpect(status().isNotFound())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Task not found with id: 99")));
    }

    @Test
    void testDeleteTask_withValidId_shouldReturn200() throws Exception {
        // Arrange: No need to mock return — service.deleteTask() returns void
        Mockito.doNothing().when(taskService).deleteTask(1L);

        // Act + Assert
        mockMvc.perform(delete("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Task deleted successfully")));
    }

    @Test
    void testDeleteTask_withInvalidId_shouldReturn404() throws Exception {
        // Arrange: mock exception when deleting non-existing task
        Mockito.doThrow(new com.example.taskmanagerment.exception.TaskNotFoundException("Task not found with id: 99"))
                .when(taskService).deleteTask(99L);

        // Act + Assert
        mockMvc.perform(delete("/api/tasks/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Task not found with id: 99")));
    }




}
