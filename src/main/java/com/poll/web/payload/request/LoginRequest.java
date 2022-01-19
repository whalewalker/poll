package com.poll.web.payload.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LoginRequest {
    @NotBlank(message = "username of email cannot be blank")
    private String usernameOrEmail;
    @NotBlank(message = "password cannot be blank")
    private String password;
}
