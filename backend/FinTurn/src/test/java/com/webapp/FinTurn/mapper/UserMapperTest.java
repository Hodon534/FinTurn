package com.webapp.FinTurn.mapper;

import com.webapp.FinTurn.domain.dto.UserDto;
import com.webapp.FinTurn.domain.entity.UserEntity;
import com.webapp.FinTurn.enumeration.UserRole;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * OK
 */
class UserMapperTest {
    private static UserMapper underTest;

    @BeforeAll
    static void beforeAll() {
        underTest = new UserMapper(new ModelMapper());
    }

    @Test
    void mapUserDtoToEntity() {
        // Given
        UserDto dto = new UserDto();
        dto.setId(1234);
        dto.setUserId("1234567890");
        dto.setFirstName("John");
        dto.setLastName("Paul");
        dto.setUsername("paulson");
        dto.setEmail("paul@email.com");
        dto.setPassword("password");
        dto.setNotLocked(true);
        dto.setActive(true);
        dto.setProfileImageUrl("tempImage");
        dto.setLastLoginDate(new Date());
        dto.setJoinDate(new Date());
        dto.setLastLoginDateDisplay(new Date());
        dto.setRole(UserRole.ROLE_USER.name());
        dto.setAuthorities(UserRole.ROLE_USER.getAuthorities());
        // When
        UserEntity entity = underTest.mapUserDtoToEntity(dto);
        // Then
        assertAll(
                () -> assertEquals(entity.getId(), dto.getId()),
                () -> assertEquals(entity.getUserId(), dto.getUserId()),
                () -> assertEquals(entity.getFirstName(), dto.getFirstName()),
                () -> assertEquals(entity.getLastName(), dto.getLastName()),
                () -> assertEquals(entity.getUsername(), dto.getUsername()),
                () -> assertEquals(entity.getEmail(), dto.getEmail()),
                () -> assertEquals(entity.getPassword(), dto.getPassword()),
                () -> assertEquals(entity.getProfileImageUrl(), dto.getProfileImageUrl()),
                () -> assertEquals(entity.getLastLoginDate(), dto.getLastLoginDate()),
                () -> assertEquals(entity.getJoinDate(), dto.getJoinDate()),
                () -> assertEquals(entity.getLastLoginDateDisplay(), dto.getLastLoginDateDisplay()),
                () -> assertEquals(entity.getRole(), dto.getRole()),
                () -> assertArrayEquals(entity.getAuthorities(), dto.getAuthorities()),
                () -> assertEquals(entity.isNotLocked(), dto.isNotLocked()),
                () -> assertEquals(entity.isActive(), dto.isActive())
        );
    }

    @Test
    void mapUserEntityToDto() {
            // Given
            UserEntity entity = new UserEntity();
            entity.setId(12444L);
            entity.setUserId("1234567890");
            entity.setFirstName("John");
            entity.setLastName("Paul");
            entity.setUsername("paulson");
            entity.setEmail("paul@email.com");
            entity.setPassword("password");
            entity.setNotLocked(true);
            entity.setActive(true);
            entity.setProfileImageUrl("tempImage");
            entity.setLastLoginDate(new Date());
            entity.setJoinDate(new Date());
            entity.setLastLoginDateDisplay(new Date());
            entity.setRole(UserRole.ROLE_USER.name());
            entity.setAuthorities(UserRole.ROLE_USER.getAuthorities());
            // When
            UserDto dto = underTest.mapUserEntityToDto(entity);
            // Then
            assertAll(
                    () -> assertEquals(dto.getId(), entity.getId()),
                    () -> assertEquals(dto.getUserId(), entity.getUserId()),
                    () -> assertEquals(dto.getFirstName(), entity.getFirstName()),
                    () -> assertEquals(dto.getLastName(), entity.getLastName()),
                    () -> assertEquals(dto.getUsername(), entity.getUsername()),
                    () -> assertEquals(dto.getEmail(), entity.getEmail()),
                    () -> assertEquals(dto.getPassword(), entity.getPassword()),
                    () -> assertEquals(dto.getProfileImageUrl(), entity.getProfileImageUrl()),
                    () -> assertEquals(dto.getLastLoginDate(), entity.getLastLoginDate()),
                    () -> assertEquals(dto.getJoinDate(), entity.getJoinDate()),
                    () -> assertEquals(dto.getLastLoginDateDisplay(), entity.getLastLoginDateDisplay()),
                    () -> assertEquals(dto.getRole(), entity.getRole()),
                    () -> assertArrayEquals(dto.getAuthorities(), entity.getAuthorities()),
                    () -> assertEquals(dto.isNotLocked(), entity.isNotLocked()),
                    () -> assertEquals(dto.isActive(), entity.isActive())
            );
    }
}