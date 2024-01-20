package com.webapp.FinTurn.service.impl;

import com.webapp.FinTurn.constant.FileConstant;
import com.webapp.FinTurn.domain.UserPrincipal;
import com.webapp.FinTurn.domain.entity.UserEntity;
import com.webapp.FinTurn.enumeration.UserRole;
import com.webapp.FinTurn.exception.domain.EmailExistException;
import com.webapp.FinTurn.exception.domain.EmailNotFoundException;
import com.webapp.FinTurn.exception.domain.NotAnImageFileException;
import com.webapp.FinTurn.exception.domain.UsernameExistException;
import com.webapp.FinTurn.repository.UserRepository;
import com.webapp.FinTurn.service.EmailService;
import com.webapp.FinTurn.service.LoginAttemptService;
import com.webapp.FinTurn.service.UserService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.mail.MessagingException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.webapp.FinTurn.constant.FileConstant.*;
import static com.webapp.FinTurn.constant.UserServiceImplConstant.*;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.springframework.http.MediaType.*;

@Slf4j
@Transactional
@Service("UserDetailsService") //todo read
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private LoginAttemptService loginAttemptService;
    private EmailService emailService;

    public UserServiceImpl(UserRepository userRepository,
                           BCryptPasswordEncoder passwordEncoder,
                           LoginAttemptService loginAttemptService,
                           EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.loginAttemptService = loginAttemptService;
        this.emailService = emailService;
    }

    @Override
    public UserEntity register(String firstName, String lastName, String username, String email, String password) throws EmailExistException, UsernameExistException {
        validateNewUsernameAndEmail(StringUtils.EMPTY, username, email);
        UserEntity user = new UserEntity();
        user.setUserId(generateUserId());
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(encodePassword(password));
        user.setJoinDate(new Date());
        user.setActive(true);
        user.setNotLocked(true);
        user.setRole(UserRole.ROLE_USER.name());
        user.setAuthorities(UserRole.ROLE_USER.getAuthorities());
        user.setProfileImageUrl(getTemporaryProfileImageUrl(username));
        userRepository.save(user);
        log.info("New user registered. Login: " + user.getUsername() + ", Password: " + password);
        //emailService.sendEmail(firstName, email);
        return user;
    }

    @Override
    public List<UserEntity> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public UserEntity findUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    @Override
    public UserEntity findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    @Override
    public UserEntity addNewUser(String firstName, String lastName, String username, String email,
                                 String role, boolean isNotLocked, boolean isActive, MultipartFile profileImage)
            throws EmailExistException, UsernameExistException, IOException {
        validateNewUsernameAndEmail(EMPTY, username, email);
        UserEntity user = new UserEntity();
        String password = generatePassword();
        user.setUserId(generateUserId());
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setPassword(encodePassword(password));
        user.setEmail(email);
        user.setJoinDate(new Date());
        user.setActive(isActive);
        user.setNotLocked(isNotLocked);
        user.setRole(getRoleEnumName(role).name());
        user.setAuthorities(getRoleEnumName(role).getAuthorities());
        user.setProfileImageUrl(getTemporaryProfileImageUrl(username));
        userRepository.save(user);
        saveProfileImage(user, profileImage);
        log.info("New user added, Username: " + username + ", Password: " + password);
        return user;
    }

    @Override
    public UserEntity updateUser(String currentUsername, String newFirstName, String newLastName, String newUsername,
                                 String newEmail, String role, boolean isNotLocked, boolean isActive, MultipartFile profileImage)
            throws EmailExistException, UsernameExistException, IOException {
        UserEntity currentUser = validateNewUsernameAndEmail(currentUsername, newUsername, newEmail);
        currentUser.setFirstName(newFirstName);
        currentUser.setLastName(newLastName);
        currentUser.setUsername(newUsername);
        currentUser.setEmail(newEmail);
        currentUser.setActive(isActive);
        currentUser.setNotLocked(isNotLocked);
        currentUser.setRole(getRoleEnumName(role).name());
        currentUser.setAuthorities(getRoleEnumName(role).getAuthorities());
        userRepository.save(currentUser);
        saveProfileImage(currentUser, profileImage);
        return currentUser;
    }

    @Override
    public void deleteUser(String username) throws IOException {
        UserEntity user = userRepository.findUserByUsername(username);
        Path userFolder = Paths.get(USER_FOLDER + user.getUsername()).toAbsolutePath().normalize();
        FileUtils.deleteDirectory(new File(userFolder.toString()));
        userRepository.deleteById(user.getId());
    }

    @Override
    public void resetPassword(String email) throws EmailNotFoundException, MessagingException {
        UserEntity user = userRepository.findUserByEmail(email);
        if (user == null) {
            throw new EmailNotFoundException(NO_USER_FOUND_BY_EMAIL + email);
        }
        String password = generatePassword();
        user.setPassword(encodePassword(password));
        userRepository.save(user);
        emailService.sendNewPasswordEmail(user.getFirstName(), password, user.getEmail());
    }

    @Override
    public UserEntity updateProfileImage(String username, MultipartFile newProfileImage) throws IOException, EmailExistException, UsernameExistException {
        UserEntity user = validateNewUsernameAndEmail(username, null, null);
        saveProfileImage(user, newProfileImage);
        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findUserByUsername(username);
        if (user == null) {
            log.error(NO_USER_FOUND_BY_USERNAME + username);
            throw new UsernameNotFoundException(NO_USER_FOUND_BY_USERNAME + username);
        } else {
            validateLoginAttempt(user);
            user.setLastLoginDateDisplay(user.getLastLoginDate());
            user.setLastLoginDate(new Date());
            userRepository.save(user);
            UserPrincipal userPrincipal = new UserPrincipal(user);
            log.info(RETURNING_FOUND_USER_BY_USERNAME + username);
            return userPrincipal;
        }
    }

    private void validateLoginAttempt(UserEntity user) {
        if (user.isNotLocked()) {
            if (loginAttemptService.hasExceededMaxAttempts(user.getUsername())) {
                user.setNotLocked(false);
            } else {
                user.setNotLocked(true);
            }
        } else {
            loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
        }
    }

    private void saveProfileImage(UserEntity user, MultipartFile profileImage) throws IOException {
        if (profileImage != null) {
            /**
             * If profileImage is not an image-type file, throw an exception
             */
            if (!Arrays.asList(IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE, IMAGE_GIF_VALUE).contains(profileImage.getContentType())) {
                throw new NotAnImageFileException(profileImage.getOriginalFilename() + NOT_AN_IMAGE_FILE);
            }
            /**
             * Set folder path on the computer for that specific user
             */
            Path userFolder = Paths.get(USER_FOLDER + user.getUsername()).toAbsolutePath().normalize();
            /**
             * If that folder doesn't exist - create it and set logger
             */
            if (!Files.exists(userFolder)) {
                Files.createDirectories(userFolder);
                log.info(DIRECTORY_CREATED + userFolder);
            }
            /**
             * Delete other files from that directory, if they exist
             */
            Files.deleteIfExists(Paths.get(userFolder + user.getUsername(), DOT + JPG_EXTENSION));
            /**
             * Copy file and replace existing, if any
             */
            Files.copy(profileImage.getInputStream(), userFolder.resolve(
                    user.getUsername() + DOT + JPG_EXTENSION), REPLACE_EXISTING);
            user.setProfileImageUrl(setProfileImageUrl(user.getUsername()));
            userRepository.save(user);
            log.info(FILE_SAVED_IN_FILE_SYSTEM + profileImage.getOriginalFilename());
        }
    }

    private String getTemporaryProfileImageUrl(String username) {
        log.info("get Temp profile image: " + ServletUriComponentsBuilder.fromCurrentContextPath().path(DEFAULT_USER_IMAGE_PATH + username).toUriString());

        return ServletUriComponentsBuilder.fromCurrentContextPath().path(DEFAULT_USER_IMAGE_PATH + username).toUriString();
    }

    private String setProfileImageUrl(String username) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(USER_IMAGE_PATH + username + FORWARD_SLASH + username + DOT + JPG_EXTENSION).toUriString();
    }


    /**
     * extract number 10 to a different const
     * @return
     */
    private String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(10);
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    /**
     * Check if this is already exist in database
     * extract number 10 to a different const
     */
    private String generateUserId() {
        return RandomStringUtils.randomNumeric(10);
    }

    private UserRole getRoleEnumName(String role) {
        return UserRole.valueOf(role.toUpperCase());
    }

    /*public String getTemporaryProfileImageUrl(String username) {
        return temporaryImageProvider.getTemporaryProfileImageUrl(username);
    }*/

    private UserEntity validateNewUsernameAndEmail(String currentUsername, String newUsername, String newEmail) throws UsernameExistException, EmailExistException {
        UserEntity userByNewUsername = findUserByUsername(newUsername);
        UserEntity userByNewEmail = findUserByEmail(newEmail);
        if (StringUtils.isNoneBlank(currentUsername)) {
            UserEntity currentUser = findUserByUsername(currentUsername);
            if (currentUser == null) {
                throw new UsernameNotFoundException(NO_USER_FOUND_BY_USERNAME + currentUsername);
            }
            if (userByNewUsername != null && !currentUser.getId().equals(userByNewUsername.getId())) {
                throw new UsernameExistException(USERNAME_ALREADY_EXIST);
            }
            if (userByNewEmail != null && !currentUser.getId().equals(userByNewEmail.getEmail())) {
                throw new EmailExistException(EMAIL_ALREADY_EXIST);
            }
            return currentUser;
        } else { //todo verify else
            if (userByNewUsername != null) {
                throw new UsernameExistException(USERNAME_ALREADY_EXIST);
            }
            if (userByNewEmail != null) {
                throw new EmailExistException(EMAIL_ALREADY_EXIST);
            }
            return null;
        }

    }
}
