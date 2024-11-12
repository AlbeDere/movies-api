package com.example.movies_api.exceptions;

public class InvalidPaginationException extends RuntimeException {

    public InvalidPaginationException(String message) {
        super(message);
    }
}
