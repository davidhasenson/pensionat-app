package org.example.pensionatapp.pensionat.error;

public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}