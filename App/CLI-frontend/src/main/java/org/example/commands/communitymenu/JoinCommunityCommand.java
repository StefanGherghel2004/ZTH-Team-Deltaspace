package org.example.commands.communitymenu;

import org.example.Community;
import org.example.apiclients.CommunityApiClient;
import org.example.commands.Command;
import org.example.handlers.AppHandler;
import org.example.userinterface.readers.Console;

public class JoinCommunityCommand implements Command {
    private final CommunityApiClient communityApiClient = CommunityApiClient.getInstance();
    @Override
    public boolean execute() {
        AppHandler app = AppHandler.getInstance();
        Console console = Console.getInstance();
        Community community = app.getCurrentCommunity();

        boolean success=communityApiClient.joinCommunity(community);

        if (!success) {
            console.error("Failed to register vote on the server.");
        }

        return true;
    }
}
