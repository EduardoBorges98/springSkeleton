package com.skeleton.dto.response;

import com.skeleton.enums.Role;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
public class UserResponse {

    private UUID id;
    private String name;
    private String email;
    private String phoneNumber;
    private boolean active;
    private Set<Role> roles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
