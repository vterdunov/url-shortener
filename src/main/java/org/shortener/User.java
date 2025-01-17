package org.shortener;

import java.util.UUID;

public class User {
    private final String id;

    public User() {
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }
}