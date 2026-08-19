package com.example.demo.event;

import java.util.UUID;

public record PostUpdatedEvent(UUID postId, String oldContent) {}