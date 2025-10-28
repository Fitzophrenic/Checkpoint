package edu.csc3380.checkpoint.controller;

import org.springframework.web.bind.annotation.*;
import edu.csc3380.checkpoint.model.User;
import edu.csc3380.checkpoint.repository.UserRepository;

import java.util.List;

@RestController
@RequestMapping("/test")
public class TestController {

    private final UserRepository userRepository;

    public TestController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/add")
    public User addUser(@RequestBody User user) {
        return userRepository.save(user);
    }

    @GetMapping("/all")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
