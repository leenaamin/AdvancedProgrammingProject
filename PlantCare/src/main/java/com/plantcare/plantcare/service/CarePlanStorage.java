package com.plantcare.plantcare.service;

import com.plantcare.plantcare.model.CarePlan;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * File handling: saves the care plans to a text file and loads them back.
 * One plan per line: plantName;wateringDays;needsFertilizer;lightLevel
 */
public class CarePlanStorage {

    private static final Path FILE = Path.of("careplans.txt");

    /** Reads the plans from the file (an empty list if the file does not exist yet). */
    public static List<CarePlan> load() {
        List<CarePlan> plans = new ArrayList<>();
        if (!Files.exists(FILE)) {
            return plans;
        }
        try {
            for (String line : Files.readAllLines(FILE)) {
                String[] parts = line.split(";");
                if (parts.length == 4) {
                    plans.add(new CarePlan(parts[0], Integer.parseInt(parts[1]),
                            Boolean.parseBoolean(parts[2]), parts[3]));
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Could not read " + FILE + ": " + e.getMessage());
        }
        return plans;
    }

    /** Writes all the plans to the file. */
    public static void save(List<CarePlan> plans) {
        List<String> lines = plans.stream()
                .map(p -> p.getPlantName() + ";" + p.getWateringDays() + ";"
                        + p.isNeedsFertilizer() + ";" + p.getLightLevel())
                .collect(Collectors.toList());
        try {
            Files.write(FILE, lines);
        } catch (IOException e) {
            System.err.println("Could not save " + FILE + ": " + e.getMessage());
        }
    }
}