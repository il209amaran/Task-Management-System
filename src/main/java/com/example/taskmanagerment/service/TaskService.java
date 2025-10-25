package com.example.taskmanagerment.service;

import com.example.taskmanagerment.entity.Task;
import com.example.taskmanagerment.exception.TaskNotFoundException;
import com.example.taskmanagerment.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // Create a new Task
    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    // Get Task by ID
    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: "+id));
    }

    //List all tasks
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    //Update task
    public Task updateTask(Long id, Task updateTask){
        Task existingTask = getTaskById(id);

        existingTask.setTitle(updateTask.getTitle());
        existingTask.setDescription(updateTask.getDescription());
        existingTask.setPriority(updateTask.getPriority());
        existingTask.setStatus(updateTask.getStatus());
        existingTask.setDueDate(updateTask.getDueDate());

        return taskRepository.save(existingTask);
    }

    //Delete task
    public void deleteTask(Long id){
        Task existingTask = getTaskById(id);
        taskRepository.delete(existingTask);
    }

}
