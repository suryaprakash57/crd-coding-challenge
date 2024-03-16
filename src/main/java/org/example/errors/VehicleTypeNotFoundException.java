package org.example.errors;

public class VehicleTypeNotFoundException extends RuntimeException {
    public VehicleTypeNotFoundException(String id) {
        super(String.format("%s Vehicle Type not available!", id));
    }
}
