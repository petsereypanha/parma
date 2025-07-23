package com.parma.user.service.handler;

import com.parma.common.dto.EmptyObject;
import com.parma.common.dto.UserRequest;
import com.parma.common.dto.UserResponse;
import com.parma.common.exception.ResponseErrorTemplate;
import com.parma.user.model.Role;
import com.parma.user.model.User;
import com.parma.user.repository.RoleRepository;
import com.parma.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class UserHandlerService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public ResponseErrorTemplate userRequestValidation(UserRequest request){
        if(ObjectUtils.isEmpty(request.password())) {
            return new ResponseErrorTemplate(
                    "Password can't be blank or null.",
                    String.valueOf(HttpStatus.BAD_REQUEST),
                    new EmptyObject(),
                    true);
        }
        Optional<User> user = userRepository.findByUsernameOrEmail(request.username(), request.email());
        if(user.isPresent()){
            return new ResponseErrorTemplate(
                    "Username or Email already exists.",
                    String.valueOf(HttpStatus.BAD_REQUEST),
                    new EmptyObject(),
                    true);
        }
        List<String> roles = roleRepository.findAll().stream().map(Role::getName).toList();
        for(var role : request.roles()){
            if(!roles.contains(role)) {
                return new ResponseErrorTemplate(
                        "Role is invalid request.",
                        String.valueOf(HttpStatus.BAD_REQUEST),
                        new EmptyObject(),
                        true);
            }
        }
        return new ResponseErrorTemplate(
                "Success",
                String.valueOf(HttpStatus.OK),
                new EmptyObject(),
                false);
    }

    public User convertUserRequestToUser(final UserRequest request, User user) {
        user.setUsername(request.username());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setUserImage(request.userImg());
        user.setUserType(request.userType());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setEmail(request.email());
        user.setGender(request.gender());
        user.setDateOfBirth(request.dateOfBirth());
        user.setLoginAttempt(Optional.ofNullable(request.loginAttempt()).orElse(0));
        return user;
    }

    public UserResponse convertUserToUserResponse(final User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getUserImage(),
                user.getGender(),
                user.getDateOfBirth(),
                user.getPassword(),
                user.getEmail(),
                user.getFirstName() + " " + user.getLastName(),
                user.getCreatedAt(),
                user.getLastLogin(),
                user.getLoginAttempt(),
                user.getMaxAttempt(),
                user.getStatus(),
                user.getRoles().stream().map(Role::getName).toList()
        );
    }

}
