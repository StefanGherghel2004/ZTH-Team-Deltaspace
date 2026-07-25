package cli.backend.commands;

import cli.backend.Community;
import cli.backend.handlers.AppHandler;
import cli.backend.services.CommunityService;
import org.apache.commons.lang3.ObjectUtils;

public class BackCommand implements Command {
    @Override
    public boolean execute() {
        AppHandler app = AppHandler.getInstance();
        CommunityService communityService = CommunityService.getInstance();
        switch (app.getCurrentState()) {
            case ON_COMMENT:
                app.setCurrentComment(null);
                app.setCurrentState(AppHandler.State.ON_POST);
                break;

            case ON_POST:
                if (app.getCurrentPost().getCommunityName()!= null) {
                    Community community = communityService.getCommunityByName(app.getCurrentPost().getCommunityName());
                    app.setCurrentCommunity(community);
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