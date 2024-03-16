package org.example.domain;

import lombok.Data;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.example.service.InputValidator.validateInputs;

@Data
public class ReservationDetails {
    private final UUID id;
    private final Vehicle vehicle;

    private final double price;
    private final LocalDate fromDate;
    private final LocalDate toDate;

    private ReservationDetails(Vehicle vehicle, double price, LocalDate fromDate, LocalDate toDate) {
        this.id = UUID.randomUUID();
        this.vehicle = vehicle;
        this.price = price;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public static class ReservationDetailsBuilder {
        private LocalDate fromDate;
        private LocalDate toDate;
        private int mileage;
        private int licenseYears;
        private Vehicle vehicle;

        public static ReservationDetailsBuilder newInstance() {
            return new ReservationDetailsBuilder();
        }

        public ReservationDetailsBuilder fromDate(LocalDate fromDate) {
            if (fromDate.isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("'from' date must not be in the past");
            }
            this.fromDate = fromDate;
            return this;
        }

        public ReservationDetailsBuilder toDate(LocalDate toDate) {
            if (toDate.isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("'to' date must not be in the past");
            }
            this.toDate = toDate;
            return this;
        }

        public ReservationDetailsBuilder mileage(int mileage) {
            this.mileage = mileage;
            return this;
        }

        public ReservationDetailsBuilder licenseYears(int licenseYears) {
            this.licenseYears = licenseYears;
            return this;
        }

        public ReservationDetailsBuilder vehicle(Vehicle vehicle) {
            this.vehicle = vehicle;
            return this;
        }

        public ReservationDetails build() {
            validateInputs(vehicle, fromDate, toDate, mileage, licenseYears);
            long days = ChronoUnit.DAYS.between(this.fromDate, this.toDate);
            double price = getPrice(days);
            return new ReservationDetails(this.vehicle, price, fromDate, toDate);
        }

        private double getPrice(long days) {
            return vehicle.getType().calculatePrice(days, this.mileage, this.licenseYears);
        }
    }

}
