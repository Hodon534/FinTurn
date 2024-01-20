package com.webapp.FinTurn.controller.rest;

import com.webapp.FinTurn.mapper.UserMapper;
import com.webapp.FinTurn.service.UserService;
import com.webapp.FinTurn.service.impl.UserServiceImpl;
import com.webapp.FinTurn.utility.JWTTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

//@RunWith(SpringRunner.class)
@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false) // disable spring security
@ExtendWith(MockitoExtension.class) // instead of Autocloseable
class UserControllerTest {
    @MockBean
    private UserService userService;
    @Autowired
    private MockMvc mockMvc;
    private UserController underTest;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JWTTokenProvider jwtTokenProvider;
    @Mock
    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        underTest = new UserController(userService, authenticationManager, jwtTokenProvider, userMapper);
    }

    @Test
    void login() {
    }

    @Test
    void register() {
        // Given

        // When

        // Then
    }

    @Test
    void addNewUser() {
    }

    @Test
    void updateUser() {
    }

    @Test
    void getUser() {
    }

    @Test
    void getAllUsers() {
    }

    @Test
    void resetPassword() {
    }

    @Test
    void deleteUser() {
    }

    @Test
    void updateProfileImage() {
    }

    @Test
    void getProfileImage() {
    }

    @Test
    void getTempProfileImage() {
    }
}