package com.poll.web.payload.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ChoiceResponse {
    private long id;
    private String text;
    private long voteCount;
}
