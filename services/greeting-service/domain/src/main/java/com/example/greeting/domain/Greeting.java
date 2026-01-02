package com.example.greeting.domain;

import java.util.UUID;

public record Greeting(UUID id, String message) {}
