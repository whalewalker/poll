package com.poll.web.controller;

import com.poll.data.model.Token;
import com.poll.data.model.User;
import com.poll.data.repository.UserRepository;
import com.poll.services.AuthService;
import com.poll.web.exception.AppException;
import com.poll.web.exception.BadRequestException;
import com.poll.web.payload.request.LoginRequest;
import com.poll.web.payload.request.PasswordRequest;
import com.poll.web.payload.request.ResetPasswordRequest;
import com.poll.web.payload.request.SignUpRequest;
import com.poll.web.payload.response.ApiResponse;
import com.poll.web.payload.response.JwtResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@Slf4j
@RequestMapping("api/v1/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private AuthService authService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest request){
        String jwt = authService.login(request);
        return ResponseEntity.ok(new JwtResponse(jwt));
    }

    @PostMapping("signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest request){
        if (userRepository.existsByUsername(request.getUsername())){
            return new ResponseEntity<>(new ApiResponse(false, "username is already in taken!"), HttpStatus.BAD_REQUEST);
        }

        if(userRepository.existsByEmail(request.getEmail())) {
            return new ResponseEntity<>(new ApiResponse(false, "Email Address already in use!"),
                    HttpStatus.BAD_REQUEST);
        }

        try {
            User user = authService.register(request);
            URI location = ServletUriComponentsBuilder
                    .fromCurrentContextPath().path("/api/v1/users/{username}")
                    .buildAndExpand(user.getUsername()).toUri();

            return ResponseEntity.created(location).body(new ApiResponse(true, "User registered successfully"));
        } catch (BadRequestException e) {
            return new ResponseEntity<>(new ApiResponse(false, "failed"), HttpStatus.BAD_REQUEST);
        }


    }


    @PostMapping("/password/update")
    public ResponseEntity<?> updatePassword(@Valid @RequestBody PasswordRequest passwordRequest){
        try {
            authService.updatePassword(passwordRequest);
            return new ResponseEntity<>(new ApiResponse(true, "User password is successfully updated"), HttpStatus.OK);
        }catch (BadRequestException e){
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/password/reset/{username}")
    public ResponseEntity<?> forgotPassword(@Valid @PathVariable String username){
        try {
            Token passwordResetToken = authService.generatePasswordResetToken(username);
            return new ResponseEntity<>(passwordResetToken, HttpStatus.CREATED  );
        }catch (BadRequestException exception){
            return new ResponseEntity<>(new ApiResponse(false, exception.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/password/reset/{token}")
    public ResponseEntity<?> resetPassword( @Valid  @PathVariable String token, @RequestBody ResetPasswordRequest request){
        try{
            authService.resetPassword(request, token);
            return new ResponseEntity<>(new ApiResponse(true, "Password reset is successful"), HttpStatus.OK);
        }catch (BadRequestException exception){
            return new ResponseEntity<>(new ApiResponse(false, exception.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
