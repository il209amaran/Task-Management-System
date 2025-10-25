package com.example.taskmanagerment.controller;

import com.example.taskmanagerment.entity.Task;
import com.example.taskmanagerment.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    //Create new task
    @PostMapping
    public ResponseEntity<Task> createTask(@Validated @RequestBody Task task){
        Task createdTask = taskService.createTask(task);
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }

    //Get task by Id
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id){
        Task task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    //Get all tasks
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks(){
        List<Task> tasks= taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }

    //Update a task
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task task){
        Task updatedTask = taskService.updateTask(id,task);
        return ResponseEntity.ok(updatedTask);
    }

    //Delete task
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTask(@PathVariable Long id){
        taskService.deleteTask(id);
        return ResponseEntity.ok("Task deleted successfully");
    }
}


/*
$body = @{
    title = "Complete Spring Boot Assignment"
    description = "Build a task management API"
    status = "PENDING"
    priority = "HIGH"
    dueDate = "2024-02-15"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/tasks" -Method Post -ContentType "application/json" -Body $body

$body = @{
    title = "Learn Spring Boot"
    description = "Complete the library management API project"
    status = "PENDING"
    priority = "HIGH"
    dueDate = "2024-12-31"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/tasks" -Method Post -ContentType "application/json" -Body $body


*/
