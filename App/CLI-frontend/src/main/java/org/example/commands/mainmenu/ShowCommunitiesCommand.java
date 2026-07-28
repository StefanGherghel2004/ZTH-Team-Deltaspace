package org.example.commands.mainmenu;

import org.example.Community;
import org.example.commands.Command;
import org.example.handlers.AppHandler;
import org.example.userinterface.readers.Console;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.util.List;

public class ShowCommunitiesCommand implements Command {
    private final RestClient restClient = RestClient.builder()
            .baseUrl("http://localhost:8080")
            .build();
    @Override
    public boolean execute() {
        AppHandler appHandler = AppHandler.getInstance();
        Console console = Console.getInstance();

        List<Community> communities=restClient.get()
                .uri("/api/communities")
                .retrieve()
                .body(new ParameterizedTypeReference<List<Community>>() {});
        return true;
    }
}