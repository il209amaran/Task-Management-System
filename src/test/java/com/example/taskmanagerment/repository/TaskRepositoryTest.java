package com.example.taskmanagerment.repository;

import com.example.taskmanagerment.entity.Task;
import com.example.taskmanagerment.enums.Priority;
import com.example.taskmanagerment.enums.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest  // Loads only JPA components and configures an H2 in-memory DB
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    private Task task;

    @BeforeEach
    void setUp() {
        task = new Task();
        task.setTitle("Write Repository Tests");
        task.setDescription("Learn how to test JPA repositories");
        task.setStatus(TaskStatus.PENDING);
        task.setPriority(Priority.HIGH);
        task.setDueDate(LocalDate.now().plusDays(5));
    }

    // ✅ Test Create and Save
    @Test
    void testSaveTask_shouldReturnSavedTask() {
        Task savedTask = taskRepository.save(task);

        assertNotNull(savedTask.getId());
        assertEquals("Write Repository Tests", savedTask.getTitle());
        assertEquals(TaskStatus.PENDING, savedTask.getStatus());
    }

    // ✅ Test Find By Id
    @Test
    void testFindById_shouldReturnTask() {
        Task savedTask = taskRepository.save(task);

        Optional<Task> foundTask = taskRepository.findById(savedTask.getId());

        assertTrue(foundTask.isPresent());
        assertEquals("Write Repository Tests", foundTask.get().getTitle());
    }

    // ✅ Test Find All
    @Test
    void testFindAll_shouldReturnAllTasks() {
        Task task2 = new Task();
        task2.setTitle("Review Repository Layer");
        task2.setDescription("Check CRUD operations");
        task2.setStatus(TaskStatus.IN_PROGRESS);
        task2.setPriority(Priority.MEDIUM);
        task2.setDueDate(LocalDate.now().plusDays(3));

        taskRepository.save(task);
        taskRepository.save(task2);

        List<Task> tasks = taskRepository.findAll();

        assertEquals(2, tasks.size());
        assertTrue(tasks.stream().anyMatch(t -> t.getTitle().equals("Review Repository Layer")));
    }

    // ✅ Test Delete
    @Test
    void testDeleteTask_shouldRemoveTask() {
        Task savedTask = taskRepository.save(task);

        taskRepository.deleteById(savedTask.getId());

        Optional<Task> deletedTask = taskRepository.findById(savedTask.getId());
        assertTrue(deletedTask.isEmpty());
    }

    // ✅ Test Update
    @Test
    void testUpdateTask_shouldChangeDescription() {
        Task savedTask = taskRepository.save(task);

        savedTask.setDescription("Updated description - Verified repository update");
        Task updatedTask = taskRepository.save(savedTask);

        assertEquals("Updated description - Verified repository update", updatedTask.getDescription());
    }
}
