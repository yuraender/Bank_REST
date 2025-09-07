package com.example.bankcards.service;

import com.example.bankcards.dto.UserDto;
import com.example.bankcards.dto.user.CreateUserRequest;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.entity.UserAlreadyExistsException;
import com.example.bankcards.exception.entity.UserNotFoundException;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Page<UserDto> getAll(Pageable pageable) {
        return userRepository.findAll(pageable).map(User::toDto);
    }

    public UserDto getById(Long id) {
        return userRepository.findById(id)
                .map(User::toDto)
                .orElseThrow(UserNotFoundException::new);
    }

    @Transactional
    public UserDto create(CreateUserRequest createUserRequest) {
        if (userRepository.existsByUsername(createUserRequest.getUsername())) {
            throw new UserAlreadyExistsException();
        }
        User user = new User();
        user.setUsername(createUserRequest.getUsername());
        user.setPassword(passwordEncoder.encode(createUserRequest.getPassword()));
        user.setRole(createUserRequest.getRole());
        user.setEnabled(true);
        User createdUser = userRepository.save(user);
        return createdUser.toDto();
    }

    @Transactional
    public void setEnabled(Long id, boolean enabled) {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
        user.setEnabled(enabled);
        userRepository.save(user);
    }
}
