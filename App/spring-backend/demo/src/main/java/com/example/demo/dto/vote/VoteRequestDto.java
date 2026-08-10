package com.example.demo.dto.vote;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class VoteRequestDto {
    @NotNull( message = "voteType  is 'up', 'down' or 'none'")
    private VoteAction voteType;

}
