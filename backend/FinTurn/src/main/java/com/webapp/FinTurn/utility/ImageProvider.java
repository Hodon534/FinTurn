package com.webapp.FinTurn.utility;

import com.webapp.FinTurn.constant.FileConstant;
import com.webapp.FinTurn.constant.UserServiceImplConstant;
import com.webapp.FinTurn.exception.domain.NotAnImageFileException;
import com.webapp.FinTurn.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Slf4j
@Component
public class ImageProvider {

    public String setProfileImage(String username, MultipartFile profileImage) throws IOException {
        if (profileImage != null) {
            /**
             * If profileImage is not an image-type file, throw an exception
             */
            if (!Arrays.asList(MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_GIF_VALUE).contains(profileImage.getContentType())) {
                throw new NotAnImageFileException(profileImage.getOriginalFilename() + UserServiceImplConstant.NOT_AN_IMAGE_FILE);
            }
            /**
             * Set folder path on the computer for that specific user
             */
            Path userFolder = Paths.get(FileConstant.USER_FOLDER + username).toAbsolutePath().normalize();
            /**
             * If that folder doesn't exist - create it and set logger
             */
            if (!Files.exists(userFolder)) {
                Files.createDirectories(userFolder);
                log.info(FileConstant.DIRECTORY_CREATED + userFolder);
            }
            /**
             * Delete other files from that directory, if they exist
             */
            Files.deleteIfExists(Paths.get(userFolder + username, FileConstant.DOT + FileConstant.JPG_EXTENSION));
            /**
             * Copy file and replace existing, if any
             */
            Files.copy(profileImage.getInputStream(), userFolder.resolve(
                    username + FileConstant.DOT + FileConstant.JPG_EXTENSION), REPLACE_EXISTING);
            log.info(FileConstant.FILE_SAVED_IN_FILE_SYSTEM + profileImage.getOriginalFilename());
            return getProfileImageUrl(username);
        }
        return null;
    }

    public String getTemporaryProfileImageUrl(String username) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(FileConstant.DEFAULT_USER_IMAGE_PATH + username).toUriString();
    }

    public String getProfileImageUrl(String username) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(FileConstant.USER_IMAGE_PATH + username + FileConstant.FORWARD_SLASH + username + FileConstant.DOT + FileConstant.JPG_EXTENSION).toUriString();
    }
}