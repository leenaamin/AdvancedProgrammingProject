package com.plantcare.plantcare.controller;

import com.plantcare.plantcare.model.CarePlan;
import com.plantcare.plantcare.model.WateringRecord;
import com.plantcare.plantcare.service.CarePlanStorage;
import com.plantcare.plantcare.service.WateringStorage;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/** Controller for the Watering Schedule window: next watering date, status filter, sorting and Mark as Watered. */
public class WateringController {

    // Controls from watering_schedule.fxml (the names match the fx:id values)
    @FXML private ComboBox<String> plantBox, statusFilter;
    @FXML private DatePicker lastWateredPicker;
    @FXML private Button wateredButton;
    @FXML private TableView<WateringRecord> table;
    @FXML private TableColumn<WateringRecord, String> nameCol, lastCol, intervalCol, nextCol, statusCol;

    // The care plans (from the Care Plans part): they give the plant names and the watering intervals
    private final List<CarePlan> carePlans = CarePlanStorage.load();

    // All the watering records, loaded from the file when the window opens
    private final List<WateringRecord> records = new ArrayList<>(WateringStorage.load());

    @FXML
    private void initialize() {
        // The plants to choose from are the plants that have a care plan
        plantBox.getItems().setAll(carePlans.stream().map(CarePlan::getPlantName).collect(Collectors.toList()));

        statusFilter.getItems().setAll("All", WateringRecord.TODAY, WateringRecord.OVERDUE,
                WateringRecord.SOON, WateringRecord.OK);
        statusFilter.setValue("All");
        lastWateredPicker.setValue(LocalDate.now());

        // What each table column shows (lambda expressions)
        nameCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getPlantName()));
        lastCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getLastWatered().toString()));
        intervalCol.setCellValueFactory(d -> new SimpleStringProperty("Every " + d.getValue().getWateringDays() + " days"));
        nextCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNextWateringDate().toString()));
        statusCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatus()));

        // The table refreshes when the filter changes
        statusFilter.valueProperty().addListener((obs, oldValue, newValue) -> refresh());

        // "Mark as Watered" is disabled until a row is selected
        wateredButton.disableProperty().bind(table.getSelectionModel().selectedItemProperty().isNull());

        refresh();
    }

    // ===== Button actions =====

    @FXML
    private void onAdd() {
        String error = validate();
        if (error != null) {
            showError(error);
            return;
        }
        // The watering interval comes from the plant's care plan
        String name = plantBox.getValue();
        CarePlan plan = carePlans.stream().filter(p -> p.getPlantName().equals(name)).findFirst().orElseThrow();

        records.add(new WateringRecord(name, plan.getWateringDays(), lastWateredPicker.getValue()));
        plantBox.setValue(null);
        lastWateredPicker.setValue(LocalDate.now());
        saveAndRefresh();
    }

    /** The plant was watered today: the last watering date changes, so the next date is recalculated. */
    @FXML
    private void onMarkWatered() {
        table.getSelectionModel().getSelectedItem().setLastWatered(LocalDate.now());
        saveAndRefresh();
    }

    // ===== Filter and Sorting (Stream) =====

    /** Shows the plans that match the status filter, sorted by the nearest next watering date. */
    private void refresh() {
        String status = statusFilter.getValue();

        List<WateringRecord> result = records.stream()
                .filter(r -> status.equals("All") || r.getStatus().equals(status))   // filter by status
                .sorted(Comparator.comparing(WateringRecord::getNextWateringDate))   // nearest date first
                .collect(Collectors.toList());

        table.setItems(FXCollections.observableArrayList(result));
    }

    // ===== Validation =====

    /** Checks the form. Returns an error message, or null if the input is valid. */
    private String validate() {
        String name = plantBox.getValue();
        if (name == null) {
            return "Please choose a plant (add its care plan first if it is not in the list).";
        }
        if (records.stream().anyMatch(r -> r.getPlantName().equalsIgnoreCase(name))) {
            return "This plant is already in the schedule.";
        }
        LocalDate last = lastWateredPicker.getValue();
        if (last == null) {
            return "Please choose the last watering date.";
        }
        if (last.isAfter(LocalDate.now())) {
            return "The last watering date cannot be in the future.";
        }
        return null;
    }

    // ===== Helpers =====

    private void saveAndRefresh() {
        WateringStorage.save(records);
        refresh();
    }

    private void showError(String message) {
        new Alert(Alert.AlertType.ERROR, message).showAndWait();
    }
}