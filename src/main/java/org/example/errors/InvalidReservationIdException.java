package org.example.errors;

public class InvalidReservationIdException extends RuntimeException {
    public InvalidReservationIdException(String id) {
        super(String.format("Invalid Reservation Id:{%s}", id));
    }
}
