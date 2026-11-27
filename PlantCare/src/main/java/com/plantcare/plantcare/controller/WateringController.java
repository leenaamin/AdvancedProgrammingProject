package com.plantcare.plantcare.controller;

import com.plantcare.plantcare.model.CarePlan;
import com.plantcare.plantcare.model.WateringRecord;
import com.plantcare.plantcare.service.DataStore;
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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/** Controller for the Watering Schedule window: next watering date, status filter, sorting and Mark as Watered. */
public class WateringController {

    // Controls from watering_schedule.fxml (the names match the fx:id values)
    @FXML private ComboBox<CarePlan> planBox;
    @FXML private ComboBox<String> statusFilter;
    @FXML private DatePicker lastWateredPicker;
    @FXML private Button wateredButton;
    @FXML private TableView<WateringRecord> table;
    @FXML private TableColumn<WateringRecord, String> nameCol, lastCol, intervalCol, nextCol, statusCol;

    // The watering records are kept in the shared DataStore
    private final List<WateringRecord> records = DataStore.wateringRecords;

    @FXML
    private void initialize() {
        planBox.getItems().setAll(DataStore.carePlans);   // the plants that have a care plan
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
        records.add(new WateringRecord(planBox.getValue(), lastWateredPicker.getValue()));
        planBox.setValue(null);
        lastWateredPicker.setValue(LocalDate.now());
        refresh();
    }

    /** The plant was watered today: the last watering date changes, so the next date is recalculated. */
    @FXML
    private void onMarkWatered() {
        table.getSelectionModel().getSelectedItem().setLastWatered(LocalDate.now());
        refresh();
    }

    // ===== Filter and Sorting (Stream) =====

    /** Shows the plants that match the status filter, sorted by the nearest next watering date. */
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
        CarePlan plan = planBox.getValue();
        if (plan == null) {
            return "Please choose a plant (add its care plan first).";
        }
        if (records.stream().anyMatch(r -> r.getCarePlan() == plan)) {
            return "This plant is already in the schedule.";
        }
        LocalDate last = lastWateredPicker.getValue();
       
        if (last.isAfter(LocalDate.now())) {
            return "The last watering date cannot be in the future.";
        }
        return null;
    }

    private void showError(String message) {
        new Alert(Alert.AlertType.ERROR, message).showAndWait();
    }
}
