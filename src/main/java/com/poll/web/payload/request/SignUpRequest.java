package com.poll.web.payload.request;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class SignUpRequest {
    @NotBlank(message = "name cannot be blank")
    @Size(min = 4, max = 40)
    private String name;

    @NotBlank(message = "username cannot be blank")
    @Size(min = 3, max = 15)
    private String username;

    @NotBlank(message = "message cannot be blank")
    @Size(max = 40)
    @Email
    private String email;

    @NotBlank(message = "password cannot be blank")
    @Size(min = 6, max = 20)
    private String password;
}
