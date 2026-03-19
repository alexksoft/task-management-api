package com.taskmanager.api.repository;

import com.taskmanager.api.entity.Task;
import com.taskmanager.api.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TaskRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TaskRepository taskRepository;

    private User testUser;
    private Task testTask;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        entityManager.persist(testUser);

        testTask = new Task();
        testTask.setTitle("Test Task");
        testTask.setDescription("Test Description");
        testTask.setDueDate(LocalDate.now().plusDays(7));
        testTask.setStatus(Task.TaskStatus.PENDING);
        testTask.setUser(testUser);
        entityManager.persist(testTask);
        entityManager.flush();
    }

    @Test
    void findByUserId_Success() {
        Page<Task> tasks = taskRepository.findByUserId(testUser.getId(), PageRequest.of(0, 10));

        assertNotNull(tasks);
        assertEquals(1, tasks.getTotalElements());
        assertEquals("Test Task", tasks.getContent().get(0).getTitle());
    }

    @Test
    void findByIdAndUserId_Success() {
        Optional<Task> task = taskRepository.findByIdAndUserId(testTask.getId(), testUser.getId());

        assertTrue(task.isPresent());
        assertEquals("Test Task", task.get().getTitle());
    }

    @Test
    void findByIdAndUserId_NotFound() {
        Optional<Task> task = taskRepository.findByIdAndUserId(999L, testUser.getId());

        assertFalse(task.isPresent());
    }
}
