package cli;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class VendorCustomerController {

    @FXML
    private Button Vendor;
    @FXML
    private Button Customer;
    @FXML
    private void handleOpenVendor() {
        openFXML("Vendor.fxml", "Create your Event");
    }

    @FXML
    private void handleOpenCustomer() {
        openFXML("Customer.fxml", "Welcome to Buying tickets");
    }

    private void openFXML(String fxmlFile, String title) {
        try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Scene scene = new Scene(loader.load());

            // Create a new Stage (window)
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
