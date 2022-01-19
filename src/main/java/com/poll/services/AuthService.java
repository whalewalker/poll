package com.poll.services;


import com.poll.data.model.Token;
import com.poll.data.model.User;
import com.poll.web.exception.AppException;
import com.poll.web.exception.BadRequestException;
import com.poll.web.payload.request.LoginRequest;
import com.poll.web.payload.request.PasswordRequest;
import com.poll.web.payload.request.ResetPasswordRequest;
import com.poll.web.payload.request.SignUpRequest;
import com.poll.web.payload.response.JwtResponse;

public interface AuthService {
    String login (LoginRequest request);
    User register(SignUpRequest signUpRequest) throws BadRequestException;
    void updatePassword(PasswordRequest passwordRequest) throws BadRequestException;
    void  resetPassword(ResetPasswordRequest resetPasswordRequest, String passwordResetToken) throws BadRequestException;
    Token generatePasswordResetToken(String email) throws BadRequestException;
}
