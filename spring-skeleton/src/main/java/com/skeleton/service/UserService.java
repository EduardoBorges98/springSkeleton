package com.skeleton.service;

import com.skeleton.dto.request.CreateUserRequest;
import com.skeleton.dto.request.UpdateUserRequest;
import com.skeleton.dto.response.PageResponse;
import com.skeleton.dto.response.UserResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {

    UserResponse create(CreateUserRequest request);

    UserResponse findById(UUID id);

    PageResponse<UserResponse> findAll(String search, Pageable pageable);

    UserResponse update(UUID id, UpdateUserRequest request);

    void delete(UUID id);
}
