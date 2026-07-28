package org.example.commands.mainmenu;

import org.example.Community;
import org.example.commands.Command;
import org.example.exceptions.BackNavigationException;
import org.example.handlers.AppHandler;
import org.example.loggers.Logger;
import org.example.userinterface.readers.Console;
import org.example.userinterface.views.UICommunity;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

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
        List<Community> communities=restClient.get()
                .uri("/api/communities")
                .headers(headers -> {
                    if (appHandler.getJwtToken() != null) {
                        headers.setBearerAuth(appHandler.getJwtToken());
                    }
                })
                .retrieve()
                .body(new ParameterizedTypeReference<List<Community>>() {});

        uiCommunity.showCommunitiesList(communities, appHandler.getCurrentUser());

            if(communities==null || communities.isEmpty()){
                return true;
        }
            String communityName = console.getStringInput("\nEnter Community Name to open (or press Enter to go back): ", true);

            if (!communityName.isBlank()) {
                Community selectedCommunity = fetchCommunityByName(communityName, appHandler.getJwtToken());

                if (selectedCommunity != null) {
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
            return restClient.get()
                    .uri("/api/communities/{name}", name)
                    .headers(headers -> {
                        if (appHandler.getJwtToken() != null) {
                            headers.setBearerAuth(appHandler.getJwtToken());
                        }
                    })
                    .retrieve()
                    .body(Community.class);
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}