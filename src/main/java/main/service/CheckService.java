package main.service;

import main.dto.responses.UserLoginResponseList;
import main.model.User;
import main.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CheckService {

    @Autowired
    private final UserRepository userRepository;

    public CheckService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserLoginResponseList getCheck() {

        Optional<User> user = userRepository.findByEmail("user@gmail.com");

        System.out.println(user.get().getEmail());
        System.out.println(user.get().getId());

        return new UserLoginResponseList(user.get());
    }

}
