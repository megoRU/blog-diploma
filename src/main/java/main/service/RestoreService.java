package main.service;

import lombok.AllArgsConstructor;
import main.config.MailConfig;
import main.dto.request.RestoreRequest;
import main.dto.responses.ResultResponse;
import main.model.User;
import main.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class RestoreService {

    private final UserRepository userRepository;
    private final MailSender mailSender;
    private final MailConfig mailConfig;

    public ResponseEntity<?> restore(RestoreRequest restoreRequest) {
        Optional<User> email = userRepository.findByEmail(restoreRequest.getEmail());
        System.out.println(restoreRequest.getEmail());
        if (email.isPresent()) {
            UUID uuid = UUID.randomUUID();
            System.out.println(InetAddress.getLoopbackAddress().getHostAddress());
            System.out.println();

            userRepository.updateUserCode(uuid.toString().replaceAll("-", ""), email.get().getId());
            mailSender.sendMessage(
                    email.get().getEmail(),
                    "DevPub: Запрос на восстановление пароля",
                    "Чтобы изменить пароль перейдите по ссылке ниже: \n" +
                    "http://" + InetAddress.getLoopbackAddress().getHostAddress() + ":" + mailConfig.getSpringPort() +
                            "/login/change-password/" + uuid.toString().replaceAll("-", ""));

            return new ResponseEntity<>(new ResultResponse(true), HttpStatus.OK);

        }

        return new ResponseEntity<>(new ResultResponse(false), HttpStatus.OK);

    }

}
