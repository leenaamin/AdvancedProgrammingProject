package com.plantcare.plantcare.service;

import com.plantcare.plantcare.model.CarePlan;
import com.plantcare.plantcare.model.Plant;
import com.plantcare.plantcare.model.WateringRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * The data shared by all the windows. The lists are static, so they stay in memory
 * while the program is running, and every window reads and writes the same data.
 */
public class DataStore {

    public static final List<Plant> plants = new ArrayList<>();
    public static final List<CarePlan> carePlans = new ArrayList<>();
    public static final List<WateringRecord> wateringRecords = new ArrayList<>();

    /** Deletes a care plan together with its watering record. */
    public static void deleteCarePlan(CarePlan plan) {
        carePlans.remove(plan);
        wateringRecords.removeIf(record -> record.getCarePlan() == plan);
    }

    /** Deletes a plant together with its care plan (and the watering record of that plan). */
    public static void deletePlant(Plant plant) {
        plants.remove(plant);
        carePlans.stream()
                .filter(plan -> plan.getPlant() == plant)
                .findFirst()
                .ifPresent(DataStore::deleteCarePlan);
    }
}
