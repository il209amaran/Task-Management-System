package com.example.taskmanagerment.integration;

import com.example.taskmanagerment.entity.Task;
import com.example.taskmanagerment.enums.Priority;
import com.example.taskmanagerment.enums.TaskStatus;
import com.example.taskmanagerment.repository.TaskRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest  // Loads full application context
@AutoConfigureMockMvc  // Enables MockMvc for integration testing
@Transactional  // Rollbacks DB after each test for isolation
class TaskIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskRepository taskRepository;

    private Task task;

    @BeforeEach
    void setUp() {
        task = new Task();
        task.setTitle("Integration Testing");
        task.setDescription("End-to-end test for Task APIs");
        task.setStatus(TaskStatus.PENDING);
        task.setPriority(Priority.HIGH);
        task.setDueDate(LocalDate.now().plusDays(3));
    }

    // ✅ Test Create Task (POST)
    @Test
    void testCreateTaskIntegration_shouldReturn201AndPersistInDB() throws Exception {
        String json = objectMapper.writeValueAsString(task);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("Integration Testing")));

        // Verify it was persisted
        assertEquals(1, taskRepository.findAll().size());
    }

    // ✅ Test Get Task By ID (GET)
    @Test
    void testGetTaskByIdIntegration_shouldReturn200() throws Exception {
        Task saved = taskRepository.save(task);

        mockMvc.perform(get("/api/tasks/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Integration Testing")));
    }

    // ✅ Test Update Task (PUT)
    @Test
    void testUpdateTaskIntegration_shouldUpdateDescription() throws Exception {
        Task saved = taskRepository.save(task);
        saved.setDescription("Updated via Integration Test");

        String updatedJson = objectMapper.writeValueAsString(saved);

        mockMvc.perform(put("/api/tasks/" + saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedJson))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Updated via Integration Test")));

        // Verify updated in DB
        Task updated = taskRepository.findById(saved.getId()).orElseThrow();
        assertEquals("Updated via Integration Test", updated.getDescription());
    }

    // ✅ Test Delete Task (DELETE)
    @Test
    void testDeleteTaskIntegration_shouldDeleteFromDB() throws Exception {
        Task saved = taskRepository.save(task);

        mockMvc.perform(delete("/api/tasks/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Task deleted successfully")));

        assertEquals(0, taskRepository.findAll().size());
    }
}
