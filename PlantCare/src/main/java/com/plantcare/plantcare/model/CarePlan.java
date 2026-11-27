package com.plantcare.plantcare.model;

/** Model: the care plan of one plant (linked to the Plant from the Plant Management part). */
public class CarePlan {

    private Plant plant;
    private int wateringDays;        // water every N days
    private boolean needsFertilizer;
    private String lightLevel;       // Low / Medium / High

    public CarePlan(Plant plant, int wateringDays, boolean needsFertilizer, String lightLevel) {
        this.plant = plant;
        this.wateringDays = wateringDays;
        this.needsFertilizer = needsFertilizer;
        this.lightLevel = lightLevel;
    }

    public Plant getPlant() { return plant; }
    public void setPlant(Plant plant) { this.plant = plant; }

    public int getWateringDays() { return wateringDays; }
    public void setWateringDays(int wateringDays) { this.wateringDays = wateringDays; }

    public boolean isNeedsFertilizer() { return needsFertilizer; }
    public void setNeedsFertilizer(boolean needsFertilizer) { this.needsFertilizer = needsFertilizer; }

    public String getLightLevel() { return lightLevel; }
    public void setLightLevel(String lightLevel) { this.lightLevel = lightLevel; }

    public String getPlantName() { return plant.getName(); }

    @Override
    public String toString() {
        return plant.getName();   // so a ComboBox shows the plant name
    }
}
