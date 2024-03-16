package org.example.errors;

public class IllegalMileageException extends RuntimeException {
    public IllegalMileageException(String message) {
        super(message);
    }
}
