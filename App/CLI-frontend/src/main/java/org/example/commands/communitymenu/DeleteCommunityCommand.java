package org.example.commands.communitymenu;

import org.example.Community;
import org.example.commands.Command;
import org.example.handlers.AppHandler;
import org.example.userinterface.readers.Console;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.HttpClientErrorException;

public class DeleteCommunityCommand implements Command {


    private final RestClient restClient = RestClient.builder()
            .baseUrl("http://localhost:8080")
            .build();

    @Override
    public boolean execute() {
        AppHandler appHandler = AppHandler.getInstance();
        Community currentCommunity = appHandler.getCurrentCommunity();
        Console console = Console.getInstance();

        if (currentCommunity == null) {
            return false;
        }

        boolean confirmation = console.getUserConfirmation("Are you sure you want to delete this community? (yes/no): ");

        if (confirmation) {
            try {
                restClient.delete()
                        .uri("/api/communities/{communityName}", currentCommunity.getNickname())
                        .headers(headers -> {

                            if (appHandler.getCurrentUser().getToken() != null) {
                                headers.setBearerAuth(appHandler.getCurrentUser().getToken());
                            }
                        })
                        .retrieve()
                        .toBodilessEntity();


                appHandler.setCurrentCommunity(null);
                appHandler.setCurrentState(AppHandler.State.LOGGED_IN);
                console.success("Community deleted successfully!");
                return true;

            } catch (HttpClientErrorException.Forbidden e) {

                console.error("You are not allowed to delete this community!");
            } catch (HttpClientErrorException.NotFound e) {

                console.error("Community was not found on the server.");
            } catch (Exception e) {

                console.error("Failed to delete community: " + e.getMessage());
            }
        } else {
            console.info("Community deletion cancelled.");
        }

        appHandler.setCurrentState(AppHandler.State.ON_COMMUNITY);
        return true;
    }
}