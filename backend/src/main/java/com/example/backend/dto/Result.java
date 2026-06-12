package com.example.backend.dto;

import java.time.Instant;

public record Result<T>(int code, String message, T data, Instant timestamp) {
    public static <T> Result<T> ok(T data) {
        return new Result<>(0, "ok", data, Instant.now());
    }

    public static Result<Void> ok() {
        return new Result<>(0, "ok", null, Instant.now());
    }

    public static Result<Void> error(int code, String message) {
        return new Result<>(code, message, null, Instant.now());
    }
}
