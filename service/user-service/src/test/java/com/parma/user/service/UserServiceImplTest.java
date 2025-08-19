package com.parma.user.service;

import com.parma.common.constant.ApiConstant;
import com.parma.common.dto.UserRequest;
import com.parma.common.dto.UserResponse;
import com.parma.common.exception.ResponseErrorTemplate;
import com.parma.user.model.Group;
import com.parma.user.model.Role;
import com.parma.user.model.User;
import com.parma.user.repository.GroupRepository;
import com.parma.user.repository.RoleRepository;
import com.parma.user.repository.UserRepository;
import com.parma.user.service.handler.UserHandlerService;
import com.parma.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Test")
public class UserServiceImplTest {

    @Mock
    private RoleRepository roleRepository;
    @Mock
    private GroupRepository groupRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserHandlerService userHandlerService;
    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserRequest UserRequest;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        UserRequest = new UserRequest(
                1L,
                "testuser",
                "Test",
                "User",
                "profile.jpg",
                "password123",
                "test@example.com",
                "EMPLOYEE",
                "MALE",
                "1990-01-01",
                null,
                0,
                3,
                true,
                "ACTIVE",
                Set.of("USER", "ADMIN"),
                Set.of("DEVELOPERS", "QA")
        );
        userResponse = new UserResponse(
                1L,
                "testuser",
                "Test",
                "User",
                "profile_images/john_doe.jpg",
                "Male",
                "1990-05-15",
                null,
                "test@example.com",
                "Test User",
                LocalDateTime.of(2024, 1, 15, 10, 30),
                LocalDateTime.of(2024, 12, 20, 14, 45),
                0,
                5,
                "ACTIVE",
                List.of("USER", "ADMIN")
        );
    }
    @Test
    @DisplayName("Should create user successfully when valid request")
    void createShouldReturnSuccessResponseWhenValidRequest() {
        // Given

        Role adminRole = new Role();
        adminRole.setName("ADMIN");
        Group developersGroup = new Group();
        developersGroup.setName("DEVELOPERS");

        when(userHandlerService.userRequestValidation(UserRequest)).thenReturn(null);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(userHandlerService.convertUserRequestToUser(any(UserRequest.class), any(User.class))).thenReturn(testUser);
        when(roleRepository.findByNameIn(UserRequest.roles())).thenReturn(List.of(adminRole));
        when(groupRepository.findAllByNameIn(UserRequest.groups())).thenReturn(List.of(developersGroup));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userHandlerService.convertUserToUserResponse(testUser)).thenReturn(userResponse);

        // When
        ResponseErrorTemplate result = userService.create(UserRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isError()).isFalse();
        assertThat(result.message()).isEqualTo(ApiConstant.SUCCESS.getDescription());
        assertThat(result.data()).isEqualTo(userResponse);

        verify(userHandlerService).userRequestValidation(UserRequest);
        verify(userRepository).findByUsername("testuser");
        verify(userRepository).save(any(User.class));
    }
}
