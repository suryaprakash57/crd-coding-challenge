package org.example.errors;

public class IllegalLicenseYearsException extends RuntimeException {
    public IllegalLicenseYearsException(String message) {
        super(message);
    }
}
