package com.poll.web.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChoiceRequest {

    @NotBlank(message = "text cannot be blank")
    @Size(max = 40)
    private String text;

}
