package com.webapp.FinTurn.service;

import com.webapp.FinTurn.domain.entity.User;
import com.webapp.FinTurn.exception.domain.EmailExistException;
import com.webapp.FinTurn.exception.domain.EmailNotFoundException;
import com.webapp.FinTurn.exception.domain.UsernameExistException;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

public interface UserService {

    User register(String firstName, String lastName, String username, String email, String password) throws EmailExistException, UsernameExistException;

    List<User> getUsers();

    User findUserByUsername(String username);

    User findUserByEmail(String email);

    User addNewUser(String firstName, String lastName, String username, String email,
                    String role, boolean isNotLocked, boolean isActive, MultipartFile profileImage)
            throws EmailExistException, UsernameExistException, IOException;

    User updateUser(String currentUsername, String newFirstName, String newLastName, String newUsername,
                    String newEmail, String role, boolean isNotLocked, boolean isActive, MultipartFile profileImage)
            throws EmailExistException, UsernameExistException, IOException;

    void deleteUser(String username) throws IOException;

    void resetPassword(String email) throws EmailNotFoundException, MessagingException;

    User updateProfileImage(String username, MultipartFile newProfileImage) throws IOException, EmailExistException, UsernameExistException;
}
