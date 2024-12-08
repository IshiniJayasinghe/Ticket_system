module org.example.demo {
    requires javafx.controls;
    requires javafx.base;
    requires javafx.fxml;
    requires mysql.connector.j;
    requires java.sql;

    opens cli to javafx.fxml;
    exports cli;
}