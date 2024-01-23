package com.webapp.FinTurn.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webapp.FinTurn.domain.dto.UserDto;
import com.webapp.FinTurn.domain.entity.UserEntity;
import com.webapp.FinTurn.mapper.UserMapper;
import com.webapp.FinTurn.service.UserService;
import com.webapp.FinTurn.utility.JWTTokenProvider;
import jakarta.servlet.ServletContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.util.AssertionErrors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.web.servlet.function.RequestPredicates.contentType;


//@ExtendWith(SpringExtension.class)
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
        String firstName = "John";
        String lastName = "Paul";
        String username = "paulson";
        String email = "paul@email.com";
        String password = "password";

        UserEntity userEntity = new UserEntity();
        userEntity.setFirstName(firstName);
        userEntity.setLastName(lastName);
        userEntity.setUsername(username);
        userEntity.setEmail(email);
        userEntity.setPassword(password);

        UserDto userDto = new UserDto();
        userDto.setFirstName(firstName);
        userDto.setLastName(lastName);
        userDto.setUsername(username);
        userDto.setEmail(email);
        userDto.setPassword(password);

        String userDtoJson = objectMapper.writeValueAsString(userDto);
        System.out.println(userDtoJson);
        //RequestBuilder request = MockMvcRequestBuilders.post(BASIC_URL + "/register");

        // When
        when(userService.register(firstName, lastName, username, email, password)).thenReturn(userEntity);
        when(userMapper.mapUserEntityToDto(userEntity)).thenReturn(userDto);
        // Then

        mockMvc.perform(post(BASIC_URL + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userDtoJson
                ))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.firstName").value(firstName))
                .andExpect(jsonPath("$.lastName").value(lastName))
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.email").value(email))
        ;
/*        mockMvc.perform(post(BASIC_URL + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userDtoJson)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.firstName").value(firstName))
                .andExpect(jsonPath("$.lastName").value(lastName))
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.email").value(email))
        ;*/
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

/*private final UserEntity firstUserEntity = new UserEntity();
    private final UserEntity secondUserEntity = new UserEntity();
    private final UserDto firstUserDto = new UserDto();
    private final UserDto secondUserDto = new UserDto();

    private void setUpEntities() {
        // First User Entity
        firstUserEntity.setId(12444L);
        firstUserEntity.setUserId("1234567890");
        firstUserEntity.setFirstName("John");
        firstUserEntity.setLastName("Paul");
        firstUserEntity.setUsername("paulson");
        firstUserEntity.setEmail("paul@email.com");
        firstUserEntity.setPassword("password");
        firstUserEntity.setNotLocked(true);
        firstUserEntity.setActive(true);
        firstUserEntity.setProfileImageUrl("tempImage");
        firstUserEntity.setLastLoginDate(new Date());
        firstUserEntity.setJoinDate(new Date());
        firstUserEntity.setLastLoginDateDisplay(new Date());
        firstUserEntity.setRole(UserRole.ROLE_USER.name());
        firstUserEntity.setAuthorities(UserRole.ROLE_USER.getAuthorities());

        // Second User Entity
        secondUserEntity.setId(241L);
        secondUserEntity.setUserId("0987654321");
        secondUserEntity.setFirstName("Michael");
        secondUserEntity.setLastName("Jackson");
        secondUserEntity.setUsername("jacksonM");
        secondUserEntity.setEmail("jackson@email.com");
        secondUserEntity.setPassword("password1");
        secondUserEntity.setNotLocked(true);
        secondUserEntity.setActive(true);
        secondUserEntity.setProfileImageUrl("anotherTempImage");
        secondUserEntity.setLastLoginDate(new Date());
        secondUserEntity.setJoinDate(new Date());
        secondUserEntity.setLastLoginDateDisplay(new Date());
        secondUserEntity.setRole(UserRole.ROLE_ADMIN.name());
        secondUserEntity.setAuthorities(UserRole.ROLE_ADMIN.getAuthorities());

    }

    private void setUpDtos() {
        // First User Entity
        firstUserDto.setId(12444L);
        firstUserDto.setUserId("1234567890");
        firstUserDto.setFirstName("John");
        firstUserDto.setLastName("Paul");
        firstUserDto.setUsername("paulson");
        firstUserDto.setEmail("paul@email.com");
        firstUserDto.setPassword("password");
        firstUserDto.setNotLocked(true);
        firstUserDto.setActive(true);
        firstUserDto.setProfileImageUrl("tempImage");
        firstUserDto.setLastLoginDate(new Date());
        firstUserDto.setJoinDate(new Date());
        firstUserDto.setLastLoginDateDisplay(new Date());
        firstUserDto.setRole(UserRole.ROLE_USER.name());
        firstUserDto.setAuthorities(UserRole.ROLE_USER.getAuthorities());

        // Second User Entity
        secondUserDto.setId(241L);
        secondUserDto.setUserId("0987654321");
        secondUserDto.setFirstName("Michael");
        secondUserDto.setLastName("Jackson");
        secondUserDto.setUsername("jacksonM");
        secondUserDto.setEmail("jackson@email.com");
        secondUserDto.setPassword("password1");
        secondUserDto.setNotLocked(true);
        secondUserDto.setActive(true);
        secondUserDto.setProfileImageUrl("anotherTempImage");
        secondUserDto.setLastLoginDate(new Date());
        secondUserDto.setJoinDate(new Date());
        secondUserDto.setLastLoginDateDisplay(new Date());
        secondUserDto.setRole(UserRole.ROLE_ADMIN.name());
        secondUserDto.setAuthorities(UserRole.ROLE_ADMIN.getAuthorities());

    }*/