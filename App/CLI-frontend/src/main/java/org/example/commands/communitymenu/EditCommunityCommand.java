package org.example.commands.communitymenu;

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

public class EditCommunityCommand implements Command {

    private String editType;

    private final RestClient restClient = RestClient.builder()
            .baseUrl("http://localhost:8080")

            .build();

    public EditCommunityCommand(String editType) {
        this.editType = editType;
    }


    @Override
    public boolean execute() {
        AppHandler appHandler = AppHandler.getInstance();
        Community currentCommunity = appHandler.getCurrentCommunity();
        Console console = Console.getInstance();

        if(currentCommunity==null){
            return false;
        }

        try{
            Map<String,Object> updateDto=new HashMap<>();
            updateDto.put("name",currentCommunity.getNickname());
            updateDto.put("description",currentCommunity.getDescription());
            updateDto.put("topic",currentCommunity.getTopic());
            switch(editType){
                case "name" -> {
                    String newCommunityName = console.getStringInput("Please enter a new community name",false);
                    updateDto.put("name",newCommunityName);
                }
                case "description"->{
                    String newDescription = console.getStringInput("Please enter new description",false);
                    updateDto.put("description",newDescription);
                }
                case "topic"->{
                    List<String> topics = List.of("Food",
                            "Gaming",
                            "Science",
                            "Art",
                            "Tech");
                    console.printIndexList("Topics", topics);

                    int choice = console.getIntInRangeInput(1, topics.size());
                    String newTopic = topics.get(choice - 1);
                    updateDto.put("topic",newTopic);
                }
            }
            Community updatedCommunity=restClient.put()
                    .uri("/api/communities/{communityname}",currentCommunity.getNickname())
                    .contentType(MediaType.APPLICATION_JSON)
                    .headers(headers -> {
                     if (appHandler.getJwtToken() != null) {
                    headers.setBearerAuth(appHandler.getJwtToken());
                     }
                      })
                    .body(updateDto)
                    .retrieve()
                    .body(Community.class);
            appHandler.setCurrentCommunity(currentCommunity);
            Logger.info("Community edited!");
            console.success("Community updated successfully!");
        }catch (HttpClientErrorException.Forbidden e) {
            console.error("You cannot edit this community as you are not the owner.");
        } catch (HttpClientErrorException.BadRequest e) {
            console.error("Failed to update: " + e.getResponseBodyAsString());
        } catch (HttpClientErrorException.NotFound e) {
            console.error("Community was not found on the server.");
        } catch (BackNavigationException backNavigationException) {
            console.info(backNavigationException.getMessage());
        } catch (Exception e) {
            console.error("Community failed to update: " + e.getMessage());
            Logger.severe("Community failed to update: " + e.getMessage());
        }
        return true;
    }
}
