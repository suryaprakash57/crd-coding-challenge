package org.example.errors;

public class VehicleNotFoundException extends RuntimeException {
    public VehicleNotFoundException(String id) {
        super(String.format("Given Vehicle Id [%s] is invalid!", id));
    }
}
