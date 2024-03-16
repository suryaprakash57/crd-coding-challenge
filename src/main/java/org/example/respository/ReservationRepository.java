package org.example.respository;

import org.example.domain.ReservationDetails;

import java.time.LocalDate;
import java.util.UUID;

public interface ReservationRepository {
    void save(ReservationDetails reservationDetails);

    ReservationDetails remove(UUID reservationDetailsId);

    boolean isAvailableOnDates(UUID vehicleId, LocalDate fromDate, LocalDate toDate);

    void deleteAll();
}
