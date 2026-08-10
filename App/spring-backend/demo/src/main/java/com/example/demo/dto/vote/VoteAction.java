package com.example.demo.dto.vote;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum VoteAction {
    UP("up"),
    DOWN("down"),
    NONE("none");

    private final String value;

    VoteAction(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static VoteAction fromValue(String value) {
        if (value == null) return null;
        for (VoteAction action : VoteAction.values()) {
            if (action.value.equalsIgnoreCase(value) || action.name().equalsIgnoreCase(value)) {
                return action;
            }
        }
        throw new IllegalArgumentException("Invalid voteType. Allowed: 'up', 'down', 'none'");
    }
}