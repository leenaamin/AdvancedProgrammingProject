package com.plantcare.plantcare.controller;

import com.plantcare.plantcare.model.CarePlan;
import com.plantcare.plantcare.model.Plant;
import com.plantcare.plantcare.service.DataStore;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/** Controller for the Care Plans window: add, edit, delete, search/filter, sort and validation. */
public class CarePlanController {

    // Controls from care_plans.fxml (the names match the fx:id values)
    @FXML private TextField searchField, wateringDaysField;
    @FXML private ComboBox<Plant> plantBox;
    @FXML private ComboBox<String> lightFilter, sortBox, lightBox;
    @FXML private CheckBox fertilizerCheck;
    @FXML private Button updateButton, deleteButton;
    @FXML private TableView<CarePlan> table;
    @FXML private TableColumn<CarePlan, String> nameCol, wateringCol, fertilizerCol, lightCol;

    // The care plans are kept in the shared DataStore
    private final List<CarePlan> plans = DataStore.carePlans;

    @FXML
    private void initialize() {
        plantBox.getItems().setAll(DataStore.plants);   // the plants added in Plant Management
        lightFilter.getItems().setAll("All", "Low", "Medium", "High");
        lightFilter.setValue("All");
        sortBox.getItems().setAll("Plant name", "Watering days");
        sortBox.setValue("Plant name");
        lightBox.getItems().setAll("Low", "Medium", "High");

        // What each table column shows (lambda expressions)
        nameCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getPlantName()));
        wateringCol.setCellValueFactory(d -> new SimpleStringProperty("Every " + d.getValue().getWateringDays() + " days"));
        fertilizerCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().isNeedsFertilizer() ? "Yes" : "No"));
        lightCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getLightLevel()));

        // Live search, filter and sort: the table refreshes whenever one of them changes
        searchField.textProperty().addListener((obs, oldValue, newValue) -> refresh());
        lightFilter.valueProperty().addListener((obs, oldValue, newValue) -> refresh());
        sortBox.valueProperty().addListener((obs, oldValue, newValue) -> refresh());

        // Update and Delete are disabled until a row is selected
        updateButton.disableProperty().bind(table.getSelectionModel().selectedItemProperty().isNull());
        deleteButton.disableProperty().bind(table.getSelectionModel().selectedItemProperty().isNull());

        // Selecting a row puts its data in the form
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldPlan, plan) -> {
            if (plan != null) {
                plantBox.setValue(plan.getPlant());
                wateringDaysField.setText(String.valueOf(plan.getWateringDays()));
                fertilizerCheck.setSelected(plan.isNeedsFertilizer());
                lightBox.setValue(plan.getLightLevel());
            }
        });

        refresh();
    }

    // ===== CRUD =====

    @FXML
    private void onAdd() {
        String error = validate(null);
        if (error != null) {
            showError(error);
            return;
        }
        plans.add(new CarePlan(plantBox.getValue(), Integer.parseInt(wateringDaysField.getText().trim()),
                fertilizerCheck.isSelected(), lightBox.getValue()));
        afterChange();
    }

    @FXML
    private void onUpdate() {
        CarePlan selected = table.getSelectionModel().getSelectedItem();
        String error = validate(selected);
        if (error != null) {
            showError(error);
            return;
        }
        // The plan is changed in place, so the watering record linked to it sees the new values
        selected.setPlant(plantBox.getValue());
        selected.setWateringDays(Integer.parseInt(wateringDaysField.getText().trim()));
        selected.setNeedsFertilizer(fertilizerCheck.isSelected());
        selected.setLightLevel(lightBox.getValue());
        afterChange();
    }

    @FXML
    private void onDelete() {
        DataStore.deleteCarePlan(table.getSelectionModel().getSelectedItem());
        afterChange();
    }

    @FXML
    private void onClear() {
        plantBox.setValue(null);
        wateringDaysField.clear();
        fertilizerCheck.setSelected(false);
        lightBox.setValue(null);
        table.getSelectionModel().clearSelection();
    }

    // ===== Filter and Sorting (Stream) =====

    /** Shows in the table the plans that match the search text and the light filter, in the chosen order. */
    private void refresh() {
        String search = searchField.getText().trim().toLowerCase();
        String light = lightFilter.getValue();

        Comparator<CarePlan> order = Comparator.comparing(p -> p.getPlantName().toLowerCase());
        if ("Watering days".equals(sortBox.getValue())) {
            order = Comparator.comparingInt(CarePlan::getWateringDays);
        }

        List<CarePlan> result = plans.stream()
                .filter(p -> p.getPlantName().toLowerCase().contains(search))         // search by plant name
                .filter(p -> light.equals("All") || p.getLightLevel().equals(light))  // filter by light
                .sorted(order)                                                        // sort
                .collect(Collectors.toList());

        table.setItems(FXCollections.observableArrayList(result));
    }

    // ===== Validation =====

    /**
     * Checks the form. Returns an error message, or null if the input is valid.
     * "editing" is the plan being updated (null when adding), so it is not counted as a duplicate of itself.
     */
    private String validate(CarePlan editing) {
        Plant plant = plantBox.getValue();
        if (plant == null) {
            return "Please choose a plant (add it in My Plants first).";
        }
        if (plans.stream().anyMatch(p -> p != editing && p.getPlant() == plant)) {
            return "This plant already has a care plan.";
        }
        try {
            int days = Integer.parseInt(wateringDaysField.getText().trim());
            if (days < 1 || days > 365) {
                return "Watering days must be between 1 and 365.";
            }
        } catch (NumberFormatException e) {
            return "Watering days must be a whole number.";
        }
        if (lightBox.getValue() == null) {
            return "Please choose a light level.";
        }
        return null;
    }

    // ===== Helpers =====

    private void afterChange() {
        refresh();
        onClear();
    }

    private void showError(String message) {
        new Alert(Alert.AlertType.ERROR, message).showAndWait();
    }
}
