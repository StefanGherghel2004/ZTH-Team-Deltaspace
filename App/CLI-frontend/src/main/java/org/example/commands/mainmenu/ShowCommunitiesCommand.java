package org.example.commands.mainmenu;

import org.example.Community;
import org.example.commands.Command;
import org.example.exceptions.BackNavigationException;
import org.example.handlers.AppHandler;
import org.example.loggers.Logger;
import org.example.response.ApiResponse;
import org.example.userinterface.readers.Console;
import org.example.userinterface.textformatters.Color;
import org.example.userinterface.views.UICommunity;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.awt.*;
import java.util.Collections;
import java.util.List;

public class ShowCommunitiesCommand implements Command {
    private final RestClient restClient = RestClient.builder()
            .baseUrl("http://localhost:8080")
            .build();
    AppHandler appHandler = AppHandler.getInstance();
    @Override
    public boolean execute() {

        UICommunity uiCommunity = UICommunity.getInstance();
        Console console = Console.getInstance();
        try{
        ApiResponse<List<Community>> response=restClient.get()
                .uri("/subreddits")
                .headers(headers -> {
                    if (appHandler.getJwtToken() != null) {
                        headers.setBearerAuth(appHandler.getJwtToken());
                    }
                })
                .retrieve()
                .body(new ParameterizedTypeReference<ApiResponse<List<Community>>>() {});
        List<Community> communities = (response != null && response.getData() != null)
                    ? response.getData()
                    : Collections.emptyList();
        uiCommunity.showCommunitiesList(communities, appHandler.getCurrentUser());

            if(communities==null || communities.isEmpty()){
                return true;
        }
            String communityName = console.getStringInput("\nEnter Community Name to open (or press Enter to go back): ", true);

            if (!communityName.isBlank()) {
                Community selectedCommunity = fetchCommunityByName(communityName, appHandler.getJwtToken());

                if (selectedCommunity != null) {
                    if(selectedCommunity.isNSFW()){
                       String warningMessage = Color.textOrange("This community contains NSFW posts! Are you sure you want to proceed?[yes/no]");
                       String confirmation = console.getStringInput(warningMessage,false);
                       if(confirmation.equalsIgnoreCase("no")){
                           appHandler.setCurrentState(AppHandler.State.LOGGED_IN);
                           return true;
                       }
                    }
                    appHandler.setCurrentCommunity(selectedCommunity);
                    appHandler.setCurrentState(AppHandler.State.ON_COMMUNITY);
                    console.success("Entered community r/" + selectedCommunity.getNickname());
                } else {
                    console.error("Community 'r/" + communityName + "' not found!");
                }
            }

        } catch (HttpClientErrorException e) {
            console.error("HTTP Error " + e.getStatusCode() + ": " + e.getResponseBodyAsString());
        } catch (BackNavigationException e) {
            console.info("Returned to main menu.");
        } catch (Exception e) {
            console.error("Failed to load communities: " + e.getMessage());
        }

        return true;
    }

    private Community fetchCommunityByName(String name, String token) {
        try {
            ApiResponse<Community> response = restClient.get()
                    .uri("/subreddits/{name}", name)
                    .headers(headers -> {
                        if (appHandler.getJwtToken() != null) {
                            headers.setBearerAuth(appHandler.getJwtToken());
                        }
                    })
                    .retrieve()
                    .body(new ParameterizedTypeReference<ApiResponse<Community>>() {});

            return (response != null) ? response.getData() : null;
        } catch (Exception e) {
            return null;
        }
    }
}