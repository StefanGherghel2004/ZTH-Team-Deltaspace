package com.example.demo.dto.vote;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoteResponseDto {
    private int upvotes;
    private int downvotes;
    private int score;
    private String userVote;
}
