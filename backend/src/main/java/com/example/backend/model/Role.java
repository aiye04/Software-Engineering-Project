package com.example.backend.model;

public enum Role {
    ADMIN,
    TEACHER,
    CAPTAIN,
    CORE,
    MEMBER,
    TRAINEE;

    public static Role from(String value) {
        return Role.valueOf(value.trim().toUpperCase());
    }
}
