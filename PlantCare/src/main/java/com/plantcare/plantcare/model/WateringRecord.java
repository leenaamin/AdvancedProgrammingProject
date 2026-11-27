package com.plantcare.plantcare.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/** Model: the watering schedule of one plant (a simple class with no JavaFX code). */
public class WateringRecord {

    // Watering status of a plant
    public static final String OVERDUE = "Overdue";   // the watering day has passed
    public static final String TODAY = "Today";       // needs water today
    public static final String SOON = "Soon";         // due within the next 2 days
    public static final String OK = "OK";             // not due yet

    private static final int SOON_DAYS = 2;

    private final String plantName;
    private final int wateringDays;     // water every N days
    private LocalDate lastWatered;

    public WateringRecord(String plantName, int wateringDays, LocalDate lastWatered) {
        this.plantName = plantName;
        this.wateringDays = wateringDays;
        this.lastWatered = lastWatered;
    }

    public String getPlantName() { return plantName; }
    public int getWateringDays() { return wateringDays; }
    public LocalDate getLastWatered() { return lastWatered; }
    public void setLastWatered(LocalDate lastWatered) { this.lastWatered = lastWatered; }

    /** Next watering date = last watering date + watering interval (in days). */
    public LocalDate getNextWateringDate() {
        return lastWatered.plusDays(wateringDays);
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