package com.plantcare.plantcare.controller;

import com.plantcare.plantcare.App;
import javafx.fxml.FXML;

import java.io.IOException;

/** Controller of the side menu (sidebar.fxml): opens the other windows. */
public class SidebarController {

    @FXML
    private void showPlants() throws IOException {
        App.setRoot("view/plant_management");
    }

    @FXML
    private void showCarePlans() throws IOException {
        App.setRoot("view/care_plans");
    }

    @FXML
    private void showWatering() throws IOException {
        App.setRoot("view/watering_schedule");
    }
}
