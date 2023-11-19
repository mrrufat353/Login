package com.service;

import com.dto.UserDto;
import com.entity.Role;
import com.entity.User;
import com.repository.RoleRepository;
import com.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    // UserRepository for database operations related to users
    private final UserRepository userRepository;

    // RoleRepository for database operations related to roles
    private final RoleRepository roleRepository;

    // PasswordEncoder for encrypting passwords
    private final PasswordEncoder passwordEncoder;

    // Save a user based on the provided UserDto
    @Override
    public void saveUser(UserDto userDto) {
        // Create a new User object and populate its fields from the UserDto
        User user = new User();
        user.setName(userDto.getFirstName() + " " + userDto.getLastName());
        user.setEmail(userDto.getEmail());

        // Encrypt the password using Spring Security's PasswordEncoder
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        // Retrieve or create the ROLE_ADMIN role and assign it to the user
        Role role = roleRepository.findByName("ROLE_ADMIN");
        if (role == null) {
            role = checkRoleExist();
        }
        user.setRoles(Arrays.asList(role));

        // Save the user to the database
        userRepository.save(user);
    }

    // Find a user by their email
    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Retrieve a list of UserDto objects representing all users
    @Override
    public List<UserDto> findAllUsers() {
        // Retrieve a list of User entities from the database
        List<User> users = userRepository.findAll();

        // Map each User entity to a UserDto and collect them into a list
        return users.stream()
                .map((user) -> mapToUserDto(user))
                .collect(Collectors.toList());
    }

    // Map a User entity to a UserDto
    private UserDto mapToUserDto(User user) {
        UserDto userDto = new UserDto();
        String[] str = user.getName().split(" ");
        userDto.setFirstName(str[0]);
        userDto.setLastName(str[1]);
        userDto.setEmail(user.getEmail());
        return userDto;
    }

    // Check if the ROLE_ADMIN role exists; if not, create and save it
    private Role checkRoleExist() {
        Role role = new Role();
        role.setName("ROLE_ADMIN");
        return roleRepository.save(role);
    }
}