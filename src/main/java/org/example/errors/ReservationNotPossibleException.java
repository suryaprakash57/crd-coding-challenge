package org.example.errors;

import org.example.domain.VehicleType;

import java.time.LocalDate;
import java.util.UUID;

public class ReservationNotPossibleException extends RuntimeException {
    public ReservationNotPossibleException(VehicleType type, LocalDate fromDate, LocalDate toDate) {
        super(String.format("Reservation not available for type:%s between dates %s & %s", type, fromDate, toDate));
    }

    public ReservationNotPossibleException(UUID vehicleId, LocalDate fromDate, LocalDate toDate) {
        super(String.format("Reservation not available for Vehicle:%s between dates %s & %s", vehicleId, fromDate, toDate));
    }
}
