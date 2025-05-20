module com.example.library.personallibrarymanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.example.library.personallibrarymanager to javafx.fxml;
    opens com.example.library.personallibrarymanager.model to javafx.base;

    exports com.example.library.personallibrarymanager;
}
