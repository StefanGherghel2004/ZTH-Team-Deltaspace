package org.example.commands;

import org.example.Community;
import org.example.handlers.AppHandler;

public class BackCommand implements Command {
    @Override
    public boolean execute() {

        AppHandler app = AppHandler.getInstance();
        switch (app.getCurrentState()) {
            case ON_COMMENT:
                app.setCurrentComment(null);
                app.setCurrentState(AppHandler.State.ON_POST);
                break;

            case ON_POST:
                if (app.getCurrentPost().getSubreddit() != null) {
//                    Community community = communityService.getCommunityByName(app.getCurrentPost().getCommunityName());
//                    app.setCurrentCommunity(community);
                    app.setCurrentState(AppHandler.State.ON_COMMUNITY);
                } else {
                    app.setCurrentState(AppHandler.State.LOGGED_IN);
                }
                app.setCurrentPost(null);
                break;

            case ON_COMMUNITY:
                app.setCurrentCommunity(null);
                app.setCurrentState(AppHandler.State.LOGGED_IN);
                break;

            case EDIT_POST:
                app.setCurrentState(AppHandler.State.ON_POST);
                break;

            case EDIT_COMMUNITY:
                app.setCurrentState(AppHandler.State.ON_COMMUNITY);
            case EDIT_USER:
                app.setCurrentState(AppHandler.State.LOGGED_IN);
                break;

            default:
                System.out.println("Cum a ajuns aici?");
        }

        return true;
    }
}