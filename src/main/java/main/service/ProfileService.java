package main.service;

import lombok.AllArgsConstructor;
import main.dto.enums.ProfileErrors;
import main.dto.request.ProfileRequest;
import main.dto.responses.CreateResponse;
import main.dto.responses.ResultResponse;
import main.model.User;
import main.repositories.UserRepository;
import main.security.UserService;
import org.imgscalr.Scalr;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class ProfileService {

    private final UserService userService;
    private final UserRepository userRepository;


    public ResponseEntity<?> editProfile(ProfileRequest profileRequest, Principal principal) throws Exception {
        System.out.println(profileRequest.toString());

        Map<ProfileErrors, String> list = new HashMap<>();
        checks(null, profileRequest.getName(), profileRequest.getEmail(), profileRequest.getPassword(), list);

        if (!list.isEmpty()) {
            return new ResponseEntity<>(new CreateResponse(false, list), HttpStatus.OK);
        }


        if (profileRequest.getPassword() == null
                && profileRequest.getName() != null
                && profileRequest.getEmail() != null
                && profileRequest.getRemovePhoto() == 0) {
            userRepository.editNameAndEmail(
                    profileRequest.getName(),
                    profileRequest.getEmail(),
                    userService.getCurrentUser().getId());
            return new ResponseEntity<>(new ResultResponse(true), HttpStatus.OK);
        }

        if (profileRequest.getPassword() == null
                && profileRequest.getName() != null
                && profileRequest.getEmail() != null
                && profileRequest.getRemovePhoto() == 1) {
            userRepository.editNameEmailPhoto(
                    profileRequest.getName(),
                    profileRequest.getEmail(),
                    null,
                    userService.getCurrentUser().getId());
            return new ResponseEntity<>(new ResultResponse(true), HttpStatus.OK);
        }
        if (profileRequest.getPassword() != null
                && profileRequest.getName() != null
                && profileRequest.getEmail() != null
                && profileRequest.getRemovePhoto() == 1) {
            userRepository.editPasswordPhoto(
                    profileRequest.getName(),
                    profileRequest.getEmail(),
                    new BCryptPasswordEncoder(12).encode(profileRequest.getPassword()),
                    null,
                    userService.getCurrentUser().getId());
            return new ResponseEntity<>(new ResultResponse(true), HttpStatus.OK);
        } else {
            userRepository.editNameEmailPassword(
                    profileRequest.getName(),
                    profileRequest.getEmail(),
                    new BCryptPasswordEncoder(12).encode(profileRequest.getPassword()),
                    userService.getCurrentUser().getId());
        }

        return new ResponseEntity<>(new ResultResponse(true), HttpStatus.OK);
    }

    public ResponseEntity<?> editProfileMultipart(MultipartFile file, String name, String email, String password, String removePhoto, Principal principal) throws IOException {


        String profileImage = null;
        String resultPath = null;
        if (file != null) {
            profileImage = String.valueOf(userService.getCurrentUser().getId());
            resultPath = "profile_avatars/" + userService.getCurrentUser().getId();
            Path uploadDir = Paths.get(resultPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            try {
                BufferedImage resized = resizeImage(file.getInputStream(), 36, 36);
                File newFileScalr = new File(resultPath + "/" + profileImage + ".png");
                ImageIO.write(resized, "png", newFileScalr);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }

        Map<ProfileErrors, String> list = new HashMap<>();
        checks(file, name, email, password, list);

        if (!list.isEmpty()) {
            return new ResponseEntity<>(new CreateResponse(false, list), HttpStatus.OK);
        }

        if (password != null && file != null) {
            userRepository.editPasswordPhoto(
                    name,
                    email,
                    new BCryptPasswordEncoder(12).encode(password),
                    "/" + resultPath + "/" + profileImage + ".png",
                    userService.getCurrentUser().getId());

        } else if (password == null && file != null) {
            userRepository.editPhoto(
                    name,
                    email,
                    "/" + resultPath + "/" + profileImage + ".png",
                    userService.getCurrentUser().getId());
        } else {
            userRepository.editPhoto(
                    name,
                    email,
                    "/" + resultPath + profileImage + ".png",
                    userService.getCurrentUser().getId());
        }

        return new ResponseEntity<>(new ResultResponse(true), HttpStatus.OK);

    }

    private BufferedImage resizeImage(InputStream inputStream, int newWidth, int newHeight) throws
            IOException {
        BufferedImage image = ImageIO.read(inputStream);
        return Scalr.resize(image, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.FIT_TO_WIDTH, newWidth, newHeight);
    }


    private Map<ProfileErrors, String> checks(MultipartFile file, String name, String email, String password, Map<ProfileErrors, String> list) {

        if (!name.matches("[A-Za-zА-Яа-я0-9]+")) {
            list.put(ProfileErrors.NAME, ProfileErrors.NAME.getErrors());
        }

        if (file != null && file.getSize() > 5242880) {
            list.put(ProfileErrors.PHOTO, ProfileErrors.PHOTO.getErrors());
        }

        User userEmail = userRepository.findByEmailForProfile(email);
        if (userEmail != null && userEmail.getId() != userService.getCurrentUser().getId()) {
            list.put(ProfileErrors.EMAIL, ProfileErrors.EMAIL.getErrors());
        }

        if (password != null && password.length() < 6) {
            list.put(ProfileErrors.PASSWORD, ProfileErrors.PASSWORD.getErrors());
        }
        return list;
    }

}
