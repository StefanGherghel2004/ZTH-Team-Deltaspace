package com.example.demo.dto.vote;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VoteResponseDto {
    private int upvotes;
    private int downvotes;
    private int score;
    private String userVote;
}
