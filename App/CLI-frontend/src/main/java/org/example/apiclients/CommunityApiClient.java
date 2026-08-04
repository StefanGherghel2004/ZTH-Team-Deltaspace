package org.example.apiclients;

import org.example.Community;
import org.example.response.ApiResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;

public class CommunityApiClient{

    private static final String DEFAULT_BASE_URL = "http://localhost:8080";
    private static CommunityApiClient instance;
    private final RestClient restClient;

    private CommunityApiClient(){
        this.restClient = RestClient.builder()
                .baseUrl(DEFAULT_BASE_URL)
                .build();
    }
    public static CommunityApiClient getInstance(){
        if(instance==null) {
            instance= new CommunityApiClient();
        }
        return instance;
    }


    public boolean joinCommunity(Community community){
        try{
             restClient.post()
                    .uri("/subreddits/{name}/join", community.getNickname())
                    .retrieve()
                    .toBodilessEntity();
            System.out.println("You have successfully joined our community");
            return true;
            }catch (Exception e){
                    System.err.println("An error occured");
                    e.printStackTrace();
                }
        return false;
    }

    public boolean leaveCommunity(Community community){
        try{
            restClient.post()
                    .uri("/subreddits/{name}/leave", community.getNickname())
                    .retrieve()
                    .toBodilessEntity();

            System.out.println("You have successfully left the community...");
            return true;
        }catch (Exception e){
            System.err.println("Error occurred when leaving the subreddit ");
            e.printStackTrace();
        }
        return false;
    }
}
