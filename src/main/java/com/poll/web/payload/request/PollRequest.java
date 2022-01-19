package com.poll.web.payload.request;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class PollRequest {
    @NotBlank(message = "question cannot be blank")
    @Size(max = 140)
    private String question;

    @NotNull(message = "choice cannot be null")
    @Size(min = 2, max = 6)
    @Valid
    private List<ChoiceRequest> choices;

    @NotNull
    @Valid
    private PollLength pollLength;
}
