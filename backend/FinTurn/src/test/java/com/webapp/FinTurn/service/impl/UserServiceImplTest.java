package com.webapp.FinTurn.service.impl;

import com.webapp.FinTurn.domain.entity.UserEntity;
import com.webapp.FinTurn.enumeration.UserRole;
import com.webapp.FinTurn.exception.domain.EmailExistException;
import com.webapp.FinTurn.exception.domain.UsernameExistException;
import com.webapp.FinTurn.repository.UserRepository;
import com.webapp.FinTurn.service.EmailService;
import com.webapp.FinTurn.service.UserService;
import com.webapp.FinTurn.utility.ImageProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class) // instead of Autocloseable
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    private UserService underTest;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Mock
    private EmailService emailService;
    @Mock
    private ImageProvider imageProvider;

    @BeforeEach
    void setUp() {
        underTest = new UserServiceImpl(userRepository, passwordEncoder, emailService, imageProvider);
    }

    @Test
    void shouldRegisterSuccessfully() throws EmailExistException, UsernameExistException, MessagingException {
        // Given
        String username = "paulson";
        String email = "paul@email.com";
        String password = "password";
        // When
        when(imageProvider.getTemporaryProfileImageUrl(username)).thenReturn("tempImage");
        underTest.register(username, email, password);
        // Then
        ArgumentCaptor<UserEntity> userEntityArgumentCaptor =
                ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(userEntityArgumentCaptor.capture());
    }

    @Test
    void shouldGetUsers() {
        // When
        underTest.getUsers();
        // Then
        verify(userRepository).findAll();
    }

    @Test
    void shouldFindUserByUsername() {
        // Given
        String username = "nickname";
        UserEntity user = new UserEntity();
        user.setUsername(username);
        // When
        UserEntity expectedUser = underTest.findUserByUsername(username);
        // Then
        verify(userRepository).findUserByUsername(username);
    }

    @Test
    void shouldFindUserByEmail() {
        // Given
        String email = "nickname@email.com";
        UserEntity expectedUser = new UserEntity();
        expectedUser.setEmail(email);
        // When
        underTest.findUserByEmail(email);
        // Then
        verify(userRepository).findUserByEmail(email);
    }

    @Test
    void canAddNewUserWithFileImage() throws EmailExistException, IOException, UsernameExistException, MessagingException {
        // Given
        String username = "paulson";
        String email = "paul@email.com";
        boolean isActive = true;
        boolean isNotLocked = true;
        String role = UserRole.ROLE_USER.name();
        MultipartFile profileImage = new MockMultipartFile(
                "image", "image", MediaType.IMAGE_PNG_VALUE, new byte[] {1});
        // When
        //when(imageProvider.getTemporaryProfileImageUrl(username)).thenReturn("tempImage");
        underTest.addNewUser(username, email, role, isNotLocked, isActive, profileImage);
        // Then
        ArgumentCaptor<UserEntity> userEntityArgumentCaptor =
                ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(userEntityArgumentCaptor.capture());
    }

    @Test
    void canAddNewUserWithTempImage() throws EmailExistException, IOException, UsernameExistException, MessagingException {
        // Given
        String username = "paulson";
        String email = "paul@email.com";
        boolean isActive = true;
        boolean isNotLocked = true;
        String role = UserRole.ROLE_USER.name();
        MultipartFile profileImage = new MockMultipartFile(
                "noImage", "emptyFile", MediaType.IMAGE_PNG_VALUE, new byte[0]);
        // When
        when(imageProvider.getTemporaryProfileImageUrl(username)).thenReturn("tempImage");
        underTest.addNewUser(username, email, role, isNotLocked, isActive, profileImage);
        // Then
        ArgumentCaptor<UserEntity> userEntityArgumentCaptor =
                ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(userEntityArgumentCaptor.capture());
    }

    @Test
    void updateUser() {

    }

    @Test
    void deleteUser() {

    }

    @Test
    void resetPassword() {
    }

    @Test
    void updateProfileImage() {
    }

    @Test
    void loadUserByUsername() {
    }
}