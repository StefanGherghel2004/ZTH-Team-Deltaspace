package com.example.demo.dto.post;

import com.example.demo.model.Post;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PostFeedDto {
    private String seed;
    private List<Post> posts= new ArrayList<>();

}
