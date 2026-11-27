package com.plantcare.plantcare.model;

/** Model: the care plan of one plant (a simple class with no JavaFX code). */
public class CarePlan {

    private final String plantName;
    private final int wateringDays;        // water every N days
    private final boolean needsFertilizer;
    private final String lightLevel;       // Low / Medium / High

    public CarePlan(String plantName, int wateringDays, boolean needsFertilizer, String lightLevel) {
        this.plantName = plantName;
        this.wateringDays = wateringDays;
        this.needsFertilizer = needsFertilizer;
        this.lightLevel = lightLevel;
    }

    public String getPlantName() { return plantName; }
    public int getWateringDays() { return wateringDays; }
    public boolean isNeedsFertilizer() { return needsFertilizer; }
    public String getLightLevel() { return lightLevel; }
}