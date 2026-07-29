package org.example.commands.mainmenu;


import org.example.Community;
import org.example.commands.Command;
import org.example.exceptions.BackNavigationException;
import org.example.handlers.AppHandler;
import org.example.loggers.Logger;
import org.example.userinterface.readers.Console;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateCommunityCommand implements Command {

    private final RestClient restClient = RestClient.builder()
            .baseUrl("http://localhost:8080")
            .build();

    @Override
    public boolean execute() {
        AppHandler appHandler = AppHandler.getInstance();
        Console console = Console.getInstance();

        try{
            String nickname = console.getStringInput("Enter Community Name:",false);
            String description = console.getMultiLineInput("Enter Description:");

            List<String> topics = List.of("Food",
                    "Gaming",
                    "Science",
                    "Art",
                    "Tech");
            console.info("Please choose a topic from the list below:");
            console.printIndexList("Topics", topics);

            int choice = console.getIntInRangeInput(1, topics.size());
            String selectedTopic = topics.get(choice - 1);

            Map<String, Object> createDto = new HashMap<>();
            createDto.put("title", nickname);
            createDto.put("description", description);
            createDto.put("topic", selectedTopic);

            Community createdCommunity = restClient.post()
                    .uri("/api/communities")
                    .contentType(MediaType.APPLICATION_JSON)
                    .headers(headers -> {
                        if (appHandler.getJwtToken() != null) {
                            headers.setBearerAuth(appHandler.getJwtToken());
                        }
                    })
                    .body(createDto)
                    .retrieve()
                    .body(Community.class);
            appHandler.setCurrentCommunity(createdCommunity);
            appHandler.setCurrentState(AppHandler.State.ON_COMMUNITY);

            console.success("Community created successfully!");
            Logger.info("Community '" + nickname + "' created successfully!");


        } catch (HttpClientErrorException.Conflict e) {
            console.error("Community with this nickname already exists!");
        } catch (HttpClientErrorException.BadRequest e) {
            console.error("Invalid community data: " + e.getResponseBodyAsString());
        } catch (HttpClientErrorException.Unauthorized e) {
            console.error("You must be logged in to create a community.");
        } catch (BackNavigationException e) {
            console.info("Community creation cancelled.");
        } catch (Exception e) {
            console.error("Failed to create community: " + e.getMessage());
            Logger.severe("Failed to create community: " + e.getMessage());
        }

        return true;
    }
}