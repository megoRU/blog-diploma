package main.service;

import lombok.AllArgsConstructor;
import main.dto.enums.EnumResponse;
import main.dto.enums.RegistrationErrors;
import main.dto.request.PasswordRestoreRequest;
import main.dto.request.RestoreRequest;
import main.dto.responses.CreateResponse;
import main.dto.responses.ResultResponse;
import main.model.User;
import main.repositories.CaptchaRepository;
import main.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Service
@AllArgsConstructor
public class RestoreService {

    private final UserRepository userRepository;
    private final CaptchaRepository captchaRepository;
    private final MailSender mailSender;
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d MMMM yyyy, HH:mm");
    private final Calendar calendar = Calendar.getInstance();

    public ResponseEntity<?> restore(RestoreRequest restoreRequest) {
        Optional<User> email = userRepository.findByEmail(restoreRequest.getEmail());

        if (email.isPresent()) {
            UUID uuid = UUID.randomUUID();

            calendar.setTime(new Date());
            calendar.add(Calendar.HOUR_OF_DAY, 1);
            calendar.getTime();

            userRepository.updateUserCode(uuid.toString().replaceAll("-", ""), email.get().getId(), LocalDateTime.now());
            mailSender.sendMessage(
                    email.get().getEmail(),
                    "DevPub: Запрос на восстановление пароля",
                    "\nВы отправили запрос на восстановление пароля на сайте: " +
                            "https://" + InetAddress.getLoopbackAddress().getHostName() +
                            "\nДля того чтобы задать новый пароль, перейдите по ссылке: \n" +
                            "https://" + InetAddress.getLoopbackAddress().getHostName() +
                            "/login/change-password/" + uuid.toString().replaceAll("-", "")

                            + "\n\nСсылка действительна до " + simpleDateFormat.format(calendar.getTime())
                            + "\nПожалуйста, проигнорируйте данное письмо, если оно попало к Вам по ошибке." +
                            "\nС уважением, DevPub");

            return new ResponseEntity<>(new ResultResponse(true), HttpStatus.OK);
        }

        return new ResponseEntity<>(new ResultResponse(false), HttpStatus.OK);
    }

    public ResponseEntity<?> passwordRestore(PasswordRestoreRequest passwordRestoreRequest) {
        User userCode = userRepository.findUserCode(passwordRestoreRequest.getCode());
        Map<String, String> list = new HashMap<>();

        extracted(passwordRestoreRequest, userCode, list);

        if (list.isEmpty()) {

            userRepository.updatePasswordAndDeleteCode(
                    new BCryptPasswordEncoder(12).encode(passwordRestoreRequest.getPassword()),
                    userCode.getId());

            return new ResponseEntity<>(new ResultResponse(true), HttpStatus.OK);
        }

        return new ResponseEntity<>(new CreateResponse(false, list), HttpStatus.OK);
    }

    private void extracted(PasswordRestoreRequest passwordRestoreRequest, User userCode, Map<String, String> list) {
        if (passwordRestoreRequest.getPassword().length() < 6) {
            list.put(EnumResponse.password.name(), RegistrationErrors.PASSWORD.getErrors());
        }

        if (userCode == null) {
            list.put(EnumResponse.code.name(), RegistrationErrors.CODE.getErrors());
        }

        if (userCode != null && userCode.getHashTime().plusHours(1).isBefore(LocalDateTime.now())) {
            list.put(EnumResponse.code.name(), RegistrationErrors.CODE.getErrors());
        }

        if (!passwordRestoreRequest.getCaptcha().equals(captchaRepository.checkCaptcha(passwordRestoreRequest.getCaptcha_secret()))) {
            list.put(EnumResponse.captcha.name(), RegistrationErrors.CAPTCHA.getErrors());
        }
    }
}
