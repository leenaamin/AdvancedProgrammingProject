package com.plantcare.plantcare.service;

import com.plantcare.plantcare.model.WateringRecord;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * File handling: saves the watering records to a text file and loads them back.
 * One record per line: plantName;wateringDays;lastWatered (for example Aloe;7;2026-11-10)
 */
public class WateringStorage {

    private static final Path FILE = Path.of("watering.txt");

    /** Reads the records from the file (an empty list if the file does not exist yet). */
    public static List<WateringRecord> load() {
        List<WateringRecord> records = new ArrayList<>();
        if (!Files.exists(FILE)) {
            return records;
        }
        try {
            for (String line : Files.readAllLines(FILE)) {
                String[] parts = line.split(";");
                if (parts.length == 3) {
                    records.add(new WateringRecord(parts[0], Integer.parseInt(parts[1]), LocalDate.parse(parts[2])));
                }
            }
        } catch (IOException | NumberFormatException | DateTimeParseException e) {
            System.err.println("Could not read " + FILE + ": " + e.getMessage());
        }
        return records;
    }

    /** Writes all the records to the file. */
    public static void save(List<WateringRecord> records) {
        List<String> lines = records.stream()
                .map(r -> r.getPlantName() + ";" + r.getWateringDays() + ";" + r.getLastWatered())
                .collect(Collectors.toList());
        try {
            Files.write(FILE, lines);
        } catch (IOException e) {
            System.err.println("Could not save " + FILE + ": " + e.getMessage());
        }
    }
}