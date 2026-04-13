package com.skeleton.service;

import com.skeleton.dto.request.CreateUserRequest;
import com.skeleton.dto.response.UserResponse;
import com.skeleton.entity.User;
import com.skeleton.enums.Role;
import com.skeleton.exception.BusinessException;
import com.skeleton.exception.ResourceNotFoundException;
import com.skeleton.mapper.UserMapper;
import com.skeleton.repository.UserRepository;
import com.skeleton.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService")
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private UserMapper userMapper;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("john@example.com")
                .name("John Doe")
                .password("encoded")
                .roles(Set.of(Role.ROLE_USER))
                .active(true)
                .build();

        userResponse = new UserResponse();
        userResponse.setEmail("john@example.com");
        userResponse.setName("John Doe");
    }

    @Nested
    @DisplayName("create()")
    class Create {

        @Test
        @DisplayName("deve criar usuário com sucesso")
        void shouldCreateUser() {
            CreateUserRequest request = new CreateUserRequest();
            request.setEmail("john@example.com");
            request.setPassword("Password@1");
            request.setName("John Doe");
            request.setRoles(Set.of(Role.ROLE_USER));

            when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
            when(userMapper.toEntity(request)).thenReturn(user);
            when(passwordEncoder.encode(any())).thenReturn("encoded");
            when(userRepository.save(any())).thenReturn(user);
            when(userMapper.toResponse(user)).thenReturn(userResponse);

            UserResponse result = userService.create(request);

            assertThat(result).isNotNull();
            assertThat(result.getEmail()).isEqualTo("john@example.com");
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("deve lançar exceção quando email já existe")
        void shouldThrowWhenEmailExists() {
            CreateUserRequest request = new CreateUserRequest();
            request.setEmail("john@example.com");

            when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

            assertThatThrownBy(() -> userService.create(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Email already in use");

            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindById {

        @Test
        @DisplayName("deve retornar usuário quando encontrado")
        void shouldReturnUser() {
            UUID id = UUID.randomUUID();
            when(userRepository.findById(id)).thenReturn(Optional.of(user));
            when(userMapper.toResponse(user)).thenReturn(userResponse);

            UserResponse result = userService.findById(id);

            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("deve lançar exceção quando não encontrado")
        void shouldThrowWhenNotFound() {
            UUID id = UUID.randomUUID();
            when(userRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.findById(id))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("delete()")
    class Delete {

        @Test
        @DisplayName("deve fazer soft delete com sucesso")
        void shouldSoftDelete() {
            UUID id = UUID.randomUUID();
            when(userRepository.findById(id)).thenReturn(Optional.of(user));
            when(userRepository.save(any())).thenReturn(user);

            userService.delete(id);

            assertThat(user.getDeletedAt()).isNotNull();
            verify(userRepository).save(user);
        }
    }
}
