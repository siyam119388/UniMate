package com.unimate.util;

public class AppException extends Exception {

    private final int statusCode;

    public AppException(String message) {
        this(message, 400);
    }

    public AppException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
