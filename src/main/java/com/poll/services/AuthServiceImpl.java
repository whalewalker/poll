package com.poll.services;

import com.poll.data.model.Role;
import com.poll.data.model.Token;
import com.poll.data.model.User;
import com.poll.data.repository.RoleRepository;
import com.poll.data.repository.TokenRepository;
import com.poll.data.repository.UserRepository;
import com.poll.security.JwTokenProvider;
import com.poll.web.exception.BadRequestException;
import com.poll.web.payload.request.LoginRequest;
import com.poll.web.payload.request.PasswordRequest;
import com.poll.web.payload.request.ResetPasswordRequest;
import com.poll.web.payload.request.SignUpRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import static com.poll.data.model.RoleName.ROLE_USER;
import static com.poll.data.model.TokenType.PASSWORD_RESET;
import static java.lang.String.format;

@Component
@Slf4j
public class AuthServiceImpl implements AuthService{

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwTokenProvider tokenProvider;

    @Autowired
    private TokenRepository tokenRepository;

    @Override
    public String login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsernameOrEmail(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return tokenProvider.generateToken(authentication);
    }

    @Override
    public User register(SignUpRequest signUpRequest) throws BadRequestException {
        User user = new User(signUpRequest.getName(), signUpRequest.getUsername(),
                signUpRequest.getEmail(), signUpRequest.getPassword());

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Role userRole = roleRepository.findByRoleName(ROLE_USER)
                .orElseThrow(() -> new BadRequestException("User Role not set."));

        user.setRoles(Collections.singleton(userRole));

        log.info("User ==> {}", user);
        return saveAUser(user);
    }

    private User saveAUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public void updatePassword(PasswordRequest request) throws BadRequestException {
        String email = request.getEmail();
        String oldPassword = request.getOldPassword();
        String newPassword = request.getPassword();
        User userToChangePassword = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("No user found with email" + email));

        boolean passwordMatch = passwordEncoder.matches(oldPassword, userToChangePassword.getPassword());
        if (!passwordMatch) {
            throw new BadRequestException("Passwords do not match");
        }
        userToChangePassword.setPassword(passwordEncoder.encode(newPassword));
        saveAUser(userToChangePassword);
    }

    @Override
    public void resetPassword(ResetPasswordRequest request, String passwordResetToken) throws BadRequestException{
        String email = request.getEmail();
        String newPassword = request.getPassword();
        User userToResetPassword = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("No user found with user name " + email));
        Token token = tokenRepository.findByToken(passwordResetToken)
                .orElseThrow(() -> new BadRequestException(format("No token with value %s found", passwordResetToken)));
        if (token.getExpiry().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("This password reset token has expired ");
        }
        if (!token.getUserId().equals(userToResetPassword.getId())) {
            throw new BadRequestException("This password rest token does not belong to this user");
        }
        userToResetPassword.setPassword(passwordEncoder.encode(newPassword));
        saveAUser(userToResetPassword);
        tokenRepository.delete(token);
    }

    @Override
    public Token generatePasswordResetToken(String email) throws BadRequestException {
        User userToResetPassword = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("No user found with user name " + email));
        Token token = new Token();
        token.setType(PASSWORD_RESET);
        token.setUserId(userToResetPassword.getId());
        token.setToken(UUID.randomUUID().toString());
        token.setExpiry(LocalDateTime.now().plusMinutes(30));
        return tokenRepository.save(token);
    }
}
