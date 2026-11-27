package com.plantcare.plantcare.controller;

import java.util.List;
import java.util.stream.Collectors;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import com.plantcare.plantcare.model.Plant;
import com.plantcare.plantcare.service.DataStore;

public class PlantController {

    // Changed: the plants are now kept in the shared DataStore
    private List<Plant> plants = DataStore.plants;

    @FXML
    private TextField nameField;

    @FXML
    private TextField typeField;

    @FXML
    private TextField locationField;

    @FXML
    private TextField searchField;

    @FXML
    private TextField typeFilterField;

    @FXML
    private TextField locationFilterField;

    @FXML
    private TableView<Plant> plantTable;

    @FXML
    private TableColumn<Plant, String> nameColumn;

    @FXML
    private TableColumn<Plant, String> typeColumn;

    @FXML
    private TableColumn<Plant, String> locationColumn;

    @FXML
    private void initialize() {
        nameColumn.setCellValueFactory(
                new PropertyValueFactory<>("name"));
        typeColumn.setCellValueFactory(
                new PropertyValueFactory<>("type"));
        locationColumn.setCellValueFactory(
                new PropertyValueFactory<>("location"));

        // Added: show the plants that are already in the DataStore when the window opens
        filterPlants();
    }

    @FXML
    private void addPlant() {
        String name = nameField.getText().trim();
        String type = typeField.getText().trim();
        String location = locationField.getText().trim();

        if (name.isEmpty() || type.isEmpty() || location.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText(
                    "Please enter the plant name, type, and location.");
            alert.showAndWait();
            return;
        }

        Plant plant = new Plant(name, type, location);
        plants.add(plant);
        filterPlants();

        nameField.clear();
        typeField.clear();
        locationField.clear();
    }

    @FXML
    private void updatePlant() {
        Plant selectedPlant =
                plantTable.getSelectionModel().getSelectedItem();

        if (selectedPlant == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Please select a plant to update.");
            alert.showAndWait();
            return;
        }

        String name = nameField.getText().trim();
        String type = typeField.getText().trim();
        String location = locationField.getText().trim();

        if (name.isEmpty() || type.isEmpty() || location.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText(
                    "Please enter the plant name, type, and location.");
            alert.showAndWait();
            return;
        }

        selectedPlant.setName(name);
        selectedPlant.setType(type);
        selectedPlant.setLocation(location);

        filterPlants();
    }

    @FXML
    private void deletePlant() {
        Plant selectedPlant =
                plantTable.getSelectionModel().getSelectedItem();

        if (selectedPlant == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Please select a plant to delete.");
            alert.showAndWait();
            return;
        }

        // Changed: also deletes the care plan and the watering record of this plant
        DataStore.deletePlant(selectedPlant);
        filterPlants();
    }

    @FXML
    private void selectPlant() {
        Plant selectedPlant =
                plantTable.getSelectionModel().getSelectedItem();

        if (selectedPlant != null) {
            nameField.setText(selectedPlant.getName());
            typeField.setText(selectedPlant.getType());
            locationField.setText(selectedPlant.getLocation());
        }
    }

    @FXML
    private void filterPlants() {
        String name = searchField.getText().trim().toLowerCase();
        String type = typeFilterField.getText().trim().toLowerCase();
        String location =
                locationFilterField.getText().trim().toLowerCase();

        List<Plant> filteredPlants = plants.stream()
                .filter(plant ->
                        plant.getName().toLowerCase().contains(name))
                .filter(plant ->
                        plant.getType().toLowerCase().contains(type))
                .filter(plant ->
                        plant.getLocation().toLowerCase().contains(location))
                .collect(Collectors.toList());

        plantTable.getItems().setAll(filteredPlants);
    }
}
