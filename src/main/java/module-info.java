module org.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires mysql.connector.j;
    requires java.sql;
    requires javafx.graphics;

    opens cli to javafx.fxml;
    exports cli;
}