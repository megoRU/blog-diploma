package main.service;

import main.api.responses.UserLoginResponseList;
import main.model.User;
import main.model.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CheckService {

    @Autowired
    private final UserRepository userRepository;

    public CheckService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserLoginResponseList getCheck() {

        User user = userRepository.findByEmail("user@gmail.com").get();


        return new UserLoginResponseList(user);
    }

}
