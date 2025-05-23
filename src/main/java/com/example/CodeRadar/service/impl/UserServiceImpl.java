package com.example.CodeRadar.service.impl;

import com.example.CodeRadar.dto.LoginRequestDto;
import com.example.CodeRadar.dto.LoginResponseDto;
import com.example.CodeRadar.dto.UserDto;
import com.example.CodeRadar.entity.User;
import com.example.CodeRadar.exception.BadRequestException;
import com.example.CodeRadar.exception.ResourceNotFoundException;
import com.example.CodeRadar.mapper.UserMapper;
import com.example.CodeRadar.repository.UserRepository;
import com.example.CodeRadar.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = userMapper.dtoToEntity(userDto);
        // In future: user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        return userMapper.entityToDto(userRepository.saveAndFlush(user));
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> allUsers = userRepository.findAll();
        return userMapper.entitiesToDto(allUsers);
    }

    @Override
    public UserDto getUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        if(user.isEmpty()){
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        return userMapper.entityToDto(user.get());
    }

    @Override
    public LoginResponseDto loginUser(LoginRequestDto loginRequestDto) {
        Optional<User> user = userRepository.findByEmail(loginRequestDto.getEmail());

        if (user.isPresent()) {
            if (user.get().getPassword().equals(loginRequestDto.getPassword())) {
                return new LoginResponseDto("Login successful for " + user.get().getGithubUsername());
            } else {
                throw new BadRequestException("Incorrect password.");
            }
        } else {
            throw new BadRequestException("Invalid email or user not found.");
        }
    }
}
