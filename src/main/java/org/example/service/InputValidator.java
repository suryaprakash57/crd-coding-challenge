package org.example.service;

import org.example.domain.Vehicle;
import org.example.errors.IllegalLicenseYearsException;
import org.example.errors.IllegalMileageException;
import org.example.errors.InvalidDateRangeException;

import java.time.LocalDate;

public class InputValidator {

    public static void validateInputs(LocalDate fromDate, LocalDate toDate, int mileage, int licenseYears) {
        if (fromDate == null || toDate == null) {
            throwIllegalArgumentException("Dates must be set");
        }
        if (mileage <= 0) {
            throwMileageException();
        }
        if (licenseYears <= 0) {
            throwInvalidLicenseException();
        }
        if (fromDate.isAfter(toDate)) {
            throwInvalidDateRangeException();
        }
    }

    private static void throwInvalidDateRangeException() {
        throw new InvalidDateRangeException("'from' date must be before 'to' date");
    }

    private static void throwInvalidLicenseException() {
        throw new IllegalLicenseYearsException("License-years should be positive");
    }

    private static void throwMileageException() {
        throw new IllegalMileageException("Mileage should be positive");
    }

    public static void validateInputs(Vehicle vehicle, LocalDate fromDate, LocalDate toDate, int mileage, int licenseYears) {
        if (fromDate == null || toDate == null || vehicle == null) {
            throwIllegalArgumentException("All fields must be set");
        }
        if (mileage <= 0) {
            throwMileageException();
        }
        if (licenseYears <= 0) {
            throwInvalidLicenseException();
        }
        if (fromDate.isAfter(toDate)) {
            throwInvalidDateRangeException();
        }
    }

    private static void throwIllegalArgumentException(String message) {
        throw new IllegalArgumentException(message);
    }
}
