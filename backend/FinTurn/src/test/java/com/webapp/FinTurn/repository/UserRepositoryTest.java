package com.webapp.FinTurn.repository;

import com.webapp.FinTurn.domain.entity.UserEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * OK
 */
@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private UserRepository underTest;

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void shouldFindUserByUsername() {
        // Given
        String username = "nickname";
        UserEntity user = new UserEntity();
        user.setUsername(username);
        underTest.save(user);
        // When
        UserEntity expectedUser = underTest.findUserByUsername(username);
        // Then
        assertAll(
                () -> assertNotNull(expectedUser),
                () -> assertEquals(username, expectedUser.getUsername())
        );
    }

    @Test
    void shouldNotFindUserByUsername() {
        // Given
        String username = "nickname";
        UserEntity user = new UserEntity();
        underTest.save(user);
        // When
        UserEntity expectedUser = underTest.findUserByUsername(username);
        // Then
        assertNull(expectedUser);
    }

    @Test
    void shouldFindUserByEmail() {
        // Given
        String email = "email@email.com";
        UserEntity user = new UserEntity();
        user.setEmail(email);
        underTest.save(user);
        // When
        UserEntity expectedUser = underTest.findUserByEmail(email);
        // Then
        assertAll(
                () -> assertNotNull(expectedUser),
                () -> assertEquals(email, expectedUser.getEmail())
        );
    }

    @Test
    void shouldNotFindUserByEmail() {
        // Given
        String email = "email@email.com";
        UserEntity user = new UserEntity();
        underTest.save(user);
        // When
        UserEntity expectedUser = underTest.findUserByEmail(email);
        // Then
        assertNull(expectedUser);

    }
}