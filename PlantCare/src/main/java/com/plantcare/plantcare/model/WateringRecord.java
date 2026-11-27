package com.plantcare.plantcare.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Model: the watering record of one plant.
 * It is linked to the plant's CarePlan, so the watering interval always comes from the care plan.
 */
public class WateringRecord {

    // Watering status of a plant
    public static final String OVERDUE = "Overdue";   // the watering day has passed
    public static final String TODAY = "Today";       // needs water today
    public static final String SOON = "Soon";         // due within the next 2 days
    public static final String OK = "OK";             // not due yet

    private static final int SOON_DAYS = 2;

    private final CarePlan carePlan;
    private LocalDate lastWatered;

    public WateringRecord(CarePlan carePlan, LocalDate lastWatered) {
        this.carePlan = carePlan;
        this.lastWatered = lastWatered;
    }

    public CarePlan getCarePlan() { return carePlan; }
    public LocalDate getLastWatered() { return lastWatered; }
    public void setLastWatered(LocalDate lastWatered) { this.lastWatered = lastWatered; }

    public String getPlantName() { return carePlan.getPlantName(); }
    public int getWateringDays() { return carePlan.getWateringDays(); }

    /** Next watering date = last watering date + watering interval (in days). */
    public LocalDate getNextWateringDate() {
        return lastWatered.plusDays(getWateringDays());
    }

    /** Compares the next watering date with today. */
    public String getStatus() {
        long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), getNextWateringDate());
        if (daysLeft < 0) {
            return OVERDUE;
        }
        if (daysLeft == 0) {
            return TODAY;
        }
        if (daysLeft <= SOON_DAYS) {
            return SOON;
        }
        return OK;
    }
}
