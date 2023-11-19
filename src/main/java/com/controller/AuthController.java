package com.controller;

import ch.qos.logback.core.model.Model;
import com.dto.UserDto;
import com.entity.User;
import com.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AuthController {

    // Constructor-based dependency injection
    private final UserService userService;

    // Handler method to handle home page request
    @GetMapping("/index")
    public String home() {
        return "index";
    }

    // Handler method to handle login request
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // Handler method to handle user registration form request
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        // Create model object to store form data
        UserDto user = new UserDto();
        model.addText("user"); // Add user object to the model
        return "register";
    }

    // Handler method to handle user registration form submit request
    @PostMapping("/register/save")
    public String registration(@ModelAttribute("user") UserDto userDto,
                               BindingResult result,
                               Model model) {
        // Check if the email is already registered
        User existingUser = userService.findUserByEmail(userDto.getEmail());

        if (existingUser != null && existingUser.getEmail() != null && !existingUser.getEmail().isEmpty()) {
            result.rejectValue("email", null, "There is already an account registered with the same email");
        }

        // If there are validation errors, return to the registration form
        if (result.hasErrors()) {
            model.addText("user"); // Add userDto back to the model
            return "/register";
        }

        // Save the user and redirect to the registration success page
        userService.saveUser(userDto);
        return "redirect:/register?success";
    }

    // Handler method to handle the list of users
    @GetMapping("/users")
    public String users(Model model) {
        // Get a list of user data transfer objects
        List<UserDto> users = userService.findAllUsers();
        model.addText("user"); // Add the list of users to the model
        return "users";
    }
}