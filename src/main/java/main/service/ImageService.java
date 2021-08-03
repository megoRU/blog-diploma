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
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
@AllArgsConstructor
public class ImageService {

    private final UserService userService;
    private final UserRepository userRepository;


    public ResponseEntity<?> editProfile(ProfileRequest profileRequest, Principal principal) throws Exception {

        Map<ProfileErrors, String> list = new HashMap<>();
        profileChecks(null, profileRequest.getName(), profileRequest.getEmail(), profileRequest.getPassword(), list);

        if (!list.isEmpty()) {
            return new ResponseEntity<>(new CreateResponse(false, list), HttpStatus.OK);
        }


        if (profileRequest.getPassword() == null
                && profileRequest.getName() != null
                && profileRequest.getEmail() != null
                && profileRequest.getRemovePhoto() == 0) {
            userRepository.updateNameAndEmail(
                    profileRequest.getName(),
                    profileRequest.getEmail(),
                    userService.getCurrentUser().getId());
            return new ResponseEntity<>(new ResultResponse(true), HttpStatus.OK);
        }

        if (profileRequest.getPassword() == null
                && profileRequest.getName() != null
                && profileRequest.getEmail() != null
                && profileRequest.getRemovePhoto() == 1) {
            userRepository.updateNameEmailPhoto(
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
            userRepository.updatePasswordPhoto(
                    profileRequest.getName(),
                    profileRequest.getEmail(),
                    new BCryptPasswordEncoder(12).encode(profileRequest.getPassword()),
                    null,
                    userService.getCurrentUser().getId());
            return new ResponseEntity<>(new ResultResponse(true), HttpStatus.OK);
        } else {
            userRepository.updateNameEmailPassword(
                    profileRequest.getName(),
                    profileRequest.getEmail(),
                    new BCryptPasswordEncoder(12).encode(profileRequest.getPassword()),
                    userService.getCurrentUser().getId());
        }

        return new ResponseEntity<>(new ResultResponse(true), HttpStatus.OK);
    }

    public ResponseEntity<?> editProfileImage(MultipartFile photo, String name, String email, String password, String removePhoto, Principal principal) throws IOException {


        String profileImage = null;
        String resultPath = null;
        if (photo != null) {
            profileImage = String.valueOf(userService.getCurrentUser().getId());
            resultPath = "profile_avatars/" + userService.getCurrentUser().getId();
            Path uploadDir = Paths.get(resultPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            try {
                BufferedImage resized = resizeImage(photo.getInputStream(), 36, 36);
                File newFileScalr = new File(resultPath + "/" + profileImage + ".png");
                ImageIO.write(resized, "png", newFileScalr);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }

        Map<ProfileErrors, String> list = new HashMap<>();
        profileChecks(photo, name, email, password, list);

        if (!list.isEmpty()) {
            return new ResponseEntity<>(new CreateResponse(false, list), HttpStatus.OK);
        }

        if (password != null && photo != null) {
            userRepository.updatePasswordPhoto(
                    name,
                    email,
                    new BCryptPasswordEncoder(12).encode(password),
                    "/" + resultPath + "/" + profileImage + ".png",
                    userService.getCurrentUser().getId());

        } else {
            userRepository.updateNameEmailPhoto(
                    name,
                    email,
                    "/" + resultPath + "/" + profileImage + ".png",
                    userService.getCurrentUser().getId());
        }

        return new ResponseEntity<>(new ResultResponse(true), HttpStatus.OK);

    }

    public ResponseEntity<?> uploadImage(MultipartFile photo, Principal principal) throws IOException {
        Map<ProfileErrors, String> list = new HashMap<>();

        if (photo == null) {
            list.put(ProfileErrors.IMAGE_NULL, ProfileErrors.IMAGE_NULL.getErrors());
            return new ResponseEntity<>(new CreateResponse(false, list), HttpStatus.BAD_REQUEST);
        }

        if (photo.getSize() > 5242880) {
            list.put(ProfileErrors.IMAGE, ProfileErrors.IMAGE.getErrors());
            return new ResponseEntity<>(new CreateResponse(false, list), HttpStatus.BAD_REQUEST);
        }

        String fileName = photo.getOriginalFilename().toLowerCase(Locale.ROOT)
                .substring(photo.getOriginalFilename().lastIndexOf('.') + 1, photo.getOriginalFilename().length());

        if (!fileName.equals("png") && !fileName.equals("jpg")) {
            list.put(ProfileErrors.IMAGE_BAD_FORMAT, ProfileErrors.IMAGE_BAD_FORMAT.getErrors());
            return new ResponseEntity<>(new CreateResponse(false, list), HttpStatus.BAD_REQUEST);
        } else {
            StringBuilder hashPath = new StringBuilder();
            String hash = toHexString(photo.getBytes());

            for (int i = 0; i < 3; i++) {
                hashPath.append(hash, 20 + i, 22 + i).append("/");
            }

            String resultPath = "upload/" + hashPath;
            Path uploadDir = Paths.get(resultPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            try {
                File path = new File(resultPath + "/" + photo.getOriginalFilename()
                        .substring(0, photo.getOriginalFilename().lastIndexOf('.')) + ".png");

                BufferedImage bufferedImage = ImageIO.read(photo.getInputStream());
                ImageIO.write(bufferedImage, "png", path);
                return new ResponseEntity<>("\\" + path.getPath(), HttpStatus.OK);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    private BufferedImage resizeImage(InputStream inputStream, int newWidth, int newHeight) throws
            IOException {
        BufferedImage image = ImageIO.read(inputStream);
        return Scalr.resize(image, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.FIT_TO_WIDTH, newWidth, newHeight);
    }

    private String toHexString(byte[] hash) {
        BigInteger number = new BigInteger(1, hash);
        StringBuilder hexString = new StringBuilder(number.toString(16));
        while (hexString.length() < 32) {
            hexString.append('0');
        }
        return hexString.toString().replaceAll("[0-9]+", "");
    }

    private Map<ProfileErrors, String> profileChecks(MultipartFile file, String name, String email, String password, Map<ProfileErrors, String> list) {

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