package com.poll.web.payload.request;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

@Data
public class PollLength {
    @NotNull(message = "days cannot be null")
    @Max(7)
    private Integer days;

    @NotNull(message = "hour cannot be null")
    @Max(23)
    private Integer hours;
}
