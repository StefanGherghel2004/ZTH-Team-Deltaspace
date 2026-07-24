package cli.backend.commands.communitymenu;

import cli.backend.Community;
import cli.backend.commands.Command;
import cli.backend.duplicates.CheckDuplicate;
import cli.backend.duplicates.CommunityDuplicate;
import cli.backend.handlers.AppHandler;
import cli.backend.loggers.Logger;
import cli.backend.repositories.CommentRepository;
import cli.backend.repositories.CommunityRepository;
import cli.backend.services.CommunityService;
import cli.backend.userinterface.readers.Console;

import java.util.List;

public class EditCommunityCommand implements Command {
    String editType;
    public EditCommunityCommand(String editType){
        this.editType=editType;
    }
    @Override
    public boolean execute() {
        AppHandler appHandler = AppHandler.getInstance();
        Community currentCommunity = appHandler.getCurrentCommunity();
        CommunityRepository communityRepository = CommunityRepository.getInstance();
        Console console = Console.getInstance();
        CommunityService communityService = CommunityService.getInstance();
        CheckDuplicate communityCheck = new CommunityDuplicate();


        if(!communityService.canUserEditCommunity(appHandler.getCurrentUser(),currentCommunity)){
            console.error("You cannot edit this post as you are not the owner.");
            appHandler.setCurrentState(AppHandler.State.ON_COMMUNITY);
            return true;
        }

        switch (editType){
            case"description" ->{
                String newDescription = console.getMultiLineInput("Please enter yout new description for this community:");
                currentCommunity.setDescription(newDescription);
            }
            case "name" ->{
                String newCommunityName;
                while(true){
                    newCommunityName = console.getStringInput("Please enter community name:");
                    newCommunityName = communityService.formatName(newCommunityName);
                    if(communityCheck.isDuplicate(newCommunityName)){
                        console.error("Community name already exists. Please choose a different name.");
                        continue;
                    }
                    break;
                }
                currentCommunity.setNickname(newCommunityName);
            }
            case"topic" -> {
                List<String> topics = communityService.getAvailableTopics();
                console.printIndexList("Topics", topics);
                int choice = console.getIntInRangeInput(1, topics.size(), "");
                String newSelectedTopic = topics.get(choice - 1);
                currentCommunity.setTopic(newSelectedTopic);
            }
        }
        boolean updated = communityRepository.updateCommunity(currentCommunity);
        if(updated){
            console.success("Community updated successfully!");
            Logger.info("Community updated successfully!");
        }
        else{
            console.error("Community failed to update");
            Logger.severe("Community failed to update");
        }
        appHandler.setCurrentState(AppHandler.State.ON_COMMUNITY);
        return true;
    }
}
