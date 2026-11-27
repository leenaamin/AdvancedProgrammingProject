module com.plantcare.plantcare {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.plantcare.plantcare to javafx.fxml;
    opens com.plantcare.plantcare.controller to javafx.fxml;
    opens com.plantcare.plantcare.model to javafx.base;
    exports com.plantcare.plantcare;
}
