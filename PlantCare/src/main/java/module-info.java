module com.plantcare.plantcare {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.plantcare.plantcare to javafx.fxml;
    exports com.plantcare.plantcare;
}
