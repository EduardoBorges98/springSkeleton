package com.skeleton.service.impl;

import com.skeleton.dto.request.CreateUserRequest;
import com.skeleton.dto.request.UpdateUserRequest;
import com.skeleton.dto.response.PageResponse;
import com.skeleton.dto.response.UserResponse;
import com.skeleton.entity.User;
import com.skeleton.exception.BusinessException;
import com.skeleton.exception.ResourceNotFoundException;
import com.skeleton.mapper.UserMapper;
import com.skeleton.repository.UserRepository;
import com.skeleton.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponse create(CreateUserRequest request) {
        log.info("Creating user with email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already in use: " + request.getEmail());
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User saved = userRepository.save(user);
        log.info("User created with id: {}", saved.getId());

        return userMapper.toResponse(saved);
    }

    @Override
    @Cacheable(value = "users", key = "#id")
    public UserResponse findById(UUID id) {
        return userMapper.toResponse(findUserById(id));
    }

    @Override
    public PageResponse<UserResponse> findAll(String search, Pageable pageable) {
        return PageResponse.of(
                userRepository.findAllActive(search, pageable)
                        .map(userMapper::toResponse)
        );
    }

    @Override
    @Transactional
    @CachePut(value = "users", key = "#id")
    public UserResponse update(UUID id, UpdateUserRequest request) {
        log.info("Updating user: {}", id);

        User user = findUserById(id);
        userMapper.updateEntity(user, request);

        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    @CacheEvict(value = "users", key = "#id")
    public void delete(UUID id) {
        log.info("Soft-deleting user: {}", id);

        User user = findUserById(id);
        user.softDelete();
        userRepository.save(user);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private User findUserById(UUID id) {
        return userRepository.findById(id)
                .filter(u -> !u.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }
}
