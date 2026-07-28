package com.example.demo.dto.vote;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class VoteRequestDto {
    @NotBlank
    @Pattern(regexp = "^(up|down|none)$", message = "voteType  is 'up', 'down' or 'none'")
    private String voteType;
}
