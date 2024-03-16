package org.example.domain;

import static org.example.domain.Constants.EXP_DRIVER_MIN_YEARS;
import static org.example.domain.Constants.PICKUP_TRUCK_NEW_DRIVER_SURCHARGE;
import static org.example.domain.Constants.PICKUP_TRUCK_PRICE;
import static org.example.domain.Constants.SEDAN_DISCOUNT_DAY_LIMIT;
import static org.example.domain.Constants.SEDAN_DISCOUNT_PRICE;
import static org.example.domain.Constants.SEDAN_NORMAL_PRICE;
import static org.example.domain.Constants.SUV_BASE_PRICE;
import static org.example.domain.Constants.SUV_MILEAGE_FEE;
import static org.example.domain.Constants.VAN_CLEANING_FEE;
import static org.example.domain.Constants.VAN_PRICE;

public enum VehicleType {
    SEDAN {
        @Override
        public double calculatePrice(long days, int mileage, int licenseYears) {
            return days * (days < SEDAN_DISCOUNT_DAY_LIMIT ? SEDAN_NORMAL_PRICE : SEDAN_DISCOUNT_PRICE);
        }
    },
    VAN {
        @Override
        public double calculatePrice(long days, int mileage, int licenseYears) {
            double price = days * VAN_PRICE;
            return price + price * VAN_CLEANING_FEE; // cleaning fee
        }
    },
    SUV {
        @Override
        public double calculatePrice(long days, int mileage, int licenseYears) {
            return days * SUV_BASE_PRICE + mileage * SUV_MILEAGE_FEE;
        }
    },
    PICKUP_TRUCK {
        @Override
        public double calculatePrice(long days, int mileage, int licenseYears) {
            double price = days * PICKUP_TRUCK_PRICE;
            if (licenseYears < EXP_DRIVER_MIN_YEARS) {
                price += price * PICKUP_TRUCK_NEW_DRIVER_SURCHARGE; // surcharge
            }
            return price;
        }
    };



    public abstract double calculatePrice(long days, int mileage, int licenseYears);
}
