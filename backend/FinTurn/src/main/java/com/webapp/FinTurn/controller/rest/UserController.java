package com.webapp.FinTurn.controller.rest;

import com.webapp.FinTurn.domain.HttpResponse;
import com.webapp.FinTurn.domain.UserPrincipal;
import com.webapp.FinTurn.domain.dto.UserDto;
import com.webapp.FinTurn.domain.entity.UserEntity;
import com.webapp.FinTurn.exception.ExceptionHandling;
import com.webapp.FinTurn.exception.domain.EmailExistException;
import com.webapp.FinTurn.exception.domain.EmailNotFoundException;
import com.webapp.FinTurn.exception.domain.UsernameExistException;
import com.webapp.FinTurn.mapper.UserMapper;
import com.webapp.FinTurn.service.UserService;
import com.webapp.FinTurn.utility.JWTTokenProvider;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static com.webapp.FinTurn.constant.FileConstant.*;
import static com.webapp.FinTurn.constant.SecurityConstant.JWT_TOKEN_HEADER;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

//todo entity to dto

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping(path = "/user")
public class UserController extends ExceptionHandling {
    public static final String EMAIL_SENT = "An email with a new password was sent to: ";
    public static final String USER_DELETED_SUCCESSFULLY = "User deleted successfully";
    private UserService userService;
    private AuthenticationManager authenticationManager;
    private JWTTokenProvider jwtTokenProvider;
    private UserMapper userMapper;

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody UserDto userDto) {
        authenticate(userDto.getUsername(), userDto.getPassword());
        UserEntity loginUser = userService.findUserByUsername(userDto.getUsername());
        UserPrincipal userPrincipal = new UserPrincipal(loginUser);
        HttpHeaders jwtHeader = getJwtHeader(userPrincipal);
        UserDto mappedUserDto = userMapper.mapUserEntityToDto(loginUser);
        return new ResponseEntity<>(mappedUserDto, jwtHeader, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody UserDto userDto) throws EmailExistException, UsernameExistException {
        UserEntity newUser = userService.register(userDto.getFirstName(), userDto.getLastName(), userDto.getUsername(),
                userDto.getEmail(), userDto.getPassword());
        UserDto mappedUserDto = userMapper.mapUserEntityToDto(newUser);
        return new ResponseEntity<>(mappedUserDto, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<UserDto> addNewUser(@RequestParam("firstName") String firstName,
                                           @RequestParam("lastName") String lastName,
                                           @RequestParam("username") String username,
                                           @RequestParam("email") String email,
                                           @RequestParam("role") String role,
                                           @RequestParam("isNotLocked") String isNotLocked,
                                           @RequestParam("isActive") String isActive,
                                           @RequestParam(value = "profileImage", required = false) MultipartFile profileImage)
            throws EmailExistException, IOException, UsernameExistException {
        UserEntity newUser = userService.addNewUser(firstName, lastName, username, email, role,
                Boolean.parseBoolean(isActive), Boolean.parseBoolean(isNotLocked), profileImage);
        UserDto mappedUserDto = userMapper.mapUserEntityToDto(newUser);
        return new ResponseEntity<>(mappedUserDto, HttpStatus.OK);
    }

    @PostMapping("/update")
    public ResponseEntity<UserDto> updateUser(@RequestParam("currentUsername") String currentUsername,
                                           @RequestParam("firstName") String firstName,
                                           @RequestParam("lastName") String lastName,
                                           @RequestParam("username") String username,
                                           @RequestParam("email") String email,
                                           @RequestParam("role") String role,
                                           @RequestParam("isNotLocked") String isNotLocked,
                                           @RequestParam("isActive") String isActive,
                                           @RequestParam(value = "profileImage", required = false) MultipartFile profileImage)
            throws EmailExistException, IOException, UsernameExistException {
        UserEntity updatedUser = userService.updateUser(currentUsername, firstName, lastName, username, email, role,
                Boolean.parseBoolean(isActive), Boolean.parseBoolean(isNotLocked), profileImage);
        UserDto mappedUserDto = userMapper.mapUserEntityToDto(updatedUser);
        return new ResponseEntity<>(mappedUserDto, HttpStatus.OK);
    }

    @GetMapping("/find/{username}")
    public ResponseEntity<UserDto> getUser(@PathVariable("username") String username) {
        UserEntity user = userService.findUserByUsername(username);
        UserDto mappedUserDto = userMapper.mapUserEntityToDto(user);
        return new ResponseEntity<>(mappedUserDto, HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserEntity> users = userService.getUsers();
        List<UserDto> mappedDtoUsers = users.stream().map(user -> userMapper.mapUserEntityToDto(user)).toList();
        return new ResponseEntity<>(mappedDtoUsers, HttpStatus.OK);
    }

    @GetMapping("/resetPassword/{email}")
    public ResponseEntity<HttpResponse> resetPassword(@PathVariable("email") String email) throws EmailNotFoundException, MessagingException {
        userService.resetPassword(email);
        return response(HttpStatus.OK, EMAIL_SENT + email);
    }

    @DeleteMapping("/delete/{username}")
    @PreAuthorize("hasAnyAuthority('user:delete')")
    public ResponseEntity<HttpResponse> deleteUser(@PathVariable("username") String username) throws IOException {
        userService.deleteUser(username);
        return response(HttpStatus.OK, USER_DELETED_SUCCESSFULLY);
    }

    @PostMapping("/updateProfileImage")
    public ResponseEntity<UserDto> updateProfileImage(
            @RequestParam("username") String username, @RequestParam(value = "profileImage") MultipartFile profileImage)
            throws EmailExistException, IOException, UsernameExistException {
        UserEntity user = userService.updateProfileImage(username, profileImage);
        UserDto mappedUserDto = userMapper.mapUserEntityToDto(user);
        return new ResponseEntity<>(mappedUserDto, HttpStatus.OK);
    }

    @GetMapping(path = "/image/{username}/{fileName}", produces = IMAGE_JPEG_VALUE)
    public byte[] getProfileImage(@PathVariable("username") String username,
                                  @PathVariable("fileName") String fileName) throws IOException {
        return Files.readAllBytes(Paths.get(USER_FOLDER + username + FORWARD_SLASH + fileName));
    }

    @GetMapping(path = "/image/profile/{username}", produces = IMAGE_JPEG_VALUE)
    public byte[] getTempProfileImage(@PathVariable("username") String username) throws IOException {
        URL url = new URL(TEMP_PROFILE_IMAGE_BASE_URL + username);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (InputStream inputStream = url.openStream()) {
            int bytesRead;
            byte[] chunk = new byte[1024];
            while((bytesRead = inputStream.read(chunk)) > 0) {
                byteArrayOutputStream.write(chunk, 0, bytesRead);
            }
        }
        return byteArrayOutputStream.toByteArray();
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(
                new HttpResponse(
                        httpStatus.value(),
                        httpStatus,
                        httpStatus.getReasonPhrase(),
                        message), httpStatus);
    }

    private HttpHeaders getJwtHeader(UserPrincipal userPrincipal) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(JWT_TOKEN_HEADER, jwtTokenProvider.generateJwtToken(userPrincipal));
        return headers;
    }

    private void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }
}
