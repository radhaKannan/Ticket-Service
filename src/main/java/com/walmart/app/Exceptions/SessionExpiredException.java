package com.walmart.app.Exceptions;

public class SessionExpiredException extends Exception {
    public SessionExpiredException(String message) {
        super(message);
    }
}
