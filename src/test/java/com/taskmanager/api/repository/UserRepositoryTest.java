package com.taskmanager.api.repository;

import com.taskmanager.api.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        entityManager.persist(testUser);
        entityManager.flush();
    }

    @Test
    void findByEmail_Success() {
        Optional<User> user = userRepository.findByEmail("test@example.com");

        assertTrue(user.isPresent());
        assertEquals("Test User", user.get().getName());
        assertEquals("test@example.com", user.get().getEmail());
    }

    @Test
    void findByEmail_NotFound() {
        Optional<User> user = userRepository.findByEmail("notfound@example.com");

        assertFalse(user.isPresent());
    }

    @Test
    void existsByEmail_True() {
        boolean exists = userRepository.existsByEmail("test@example.com");

        assertTrue(exists);
    }

    @Test
    void existsByEmail_False() {
        boolean exists = userRepository.existsByEmail("notfound@example.com");

        assertFalse(exists);
    }
}
