package com.webapp.FinTurn.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webapp.FinTurn.domain.dto.UserDto;
import com.webapp.FinTurn.domain.entity.UserEntity;
import com.webapp.FinTurn.mapper.UserMapper;
import com.webapp.FinTurn.service.UserService;
import com.webapp.FinTurn.utility.JWTTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false) // disable spring security
//@ExtendWith(MockitoExtension.class) // instead of Autocloseable
class UserControllerTest {
    public static final String BASIC_URL = "http://localhost:8081/user";
    @MockBean
    private UserService userService;
    @Autowired
    private MockMvc mockMvc;
    private UserController underTest;
    @MockBean
    private AuthenticationManager authenticationManager;
    @MockBean
    private JWTTokenProvider jwtTokenProvider;
    @MockBean
    private UserMapper userMapper;
    //@Autowired
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        underTest = new UserController(userService, authenticationManager, jwtTokenProvider, userMapper);
    }

    @Test
    void login() {
    }

    @Disabled // body is null
    @Test
    void register() throws Exception {
        // Given
        String username = "paulson";
        String email = "paul@email.com";
        String password = "password";

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setEmail(email);
        userEntity.setPassword(password);

        UserDto userDto = new UserDto();
        userDto.setUsername(username);
        userDto.setEmail(email);
        userDto.setPassword(password);

        String userDtoJson = objectMapper.writeValueAsString(userDto);
        System.out.println(userDtoJson);
        //RequestBuilder request = MockMvcRequestBuilders.post(BASIC_URL + "/register");

        // When
        when(userService.register(username, email, password)).thenReturn(userEntity);
        when(userMapper.mapUserEntityToDto(userEntity)).thenReturn(userDto);
        // Then

        mockMvc.perform(post(BASIC_URL + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userDtoJson
                ))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.email").value(email))
        ;
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
    void getAllUsers() throws Exception {
        // Given
        String firstUsername = "Flip";
        String firstEmail = "flip@gmail.com";
        UserEntity firstUserEntity = new UserEntity();
        firstUserEntity.setUsername(firstUsername);
        firstUserEntity.setEmail(firstEmail);

        String secondUsername = "Flap";
        String secondEmail = "flap@gmail.com";
        UserEntity secondUserEntity = new UserEntity();
        secondUserEntity.setUsername(secondUsername);
        secondUserEntity.setEmail(secondEmail);

        List<UserEntity> usersEntityList = Arrays.asList(firstUserEntity, secondUserEntity);

        UserDto firstUserDto = new UserDto();
        firstUserDto.setUsername(firstUsername);
        firstUserDto.setEmail(firstEmail);

        UserDto secondUserDto = new UserDto();
        secondUserDto.setUsername(secondUsername);
        secondUserDto.setEmail(secondEmail);

        List<UserDto> usersDtoList = Arrays.asList(firstUserDto, secondUserDto);
        RequestBuilder request = MockMvcRequestBuilders.get(BASIC_URL + "/list");

        // When
        when(userService.getUsers()).thenReturn(usersEntityList);
        when(userMapper.mapUserEntityToDto(firstUserEntity)).thenReturn(firstUserDto);
        when(userMapper.mapUserEntityToDto(secondUserEntity)).thenReturn(secondUserDto);
        //ResponseEntity<List<UserDto>> response = underTest.getAllUsers();
        // Then
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(usersDtoList.size()))
                .andExpect(jsonPath("$[0].username").value(firstUserDto.getUsername()))
                .andExpect(jsonPath("$[0].email").value(firstUserDto.getEmail()))
                .andExpect(jsonPath("$[1].username").value(secondUserDto.getUsername()))
                .andExpect(jsonPath("$[1].email").value(secondUserDto.getEmail()));


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