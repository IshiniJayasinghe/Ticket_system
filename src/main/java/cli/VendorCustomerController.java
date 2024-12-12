package cli;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class VendorCustomerController {
    private final Vendor sharedVendor;

    public VendorCustomerController() {
        this.sharedVendor = new Vendor();
        System.out.println("VendorCustomerController created with Vendor #" + sharedVendor.getInstanceId());
    }

    @FXML
    private void handleOpenVendor() {
        System.out.println("Opening Vendor window with Vendor #" + sharedVendor.getInstanceId());
        openFXML("Vendor.fxml", "Create your Event", true);
    }

    @FXML
    private void handleOpenCustomer() {
        System.out.println("Opening Customer window with Vendor #" + sharedVendor.getInstanceId());
        openFXML("Customer.fxml", "Welcome to Buying Tickets", false);
    }

    private void openFXML(String fxmlFile, String title, boolean isVendor) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Scene scene = new Scene(loader.load());

            if (isVendor) {
                VendorController vendorController = loader.getController();
                System.out.println("Setting Vendor #" + sharedVendor.getInstanceId() + " in VendorController");
                vendorController.setVendor(sharedVendor);
            } else {
                CustomerController customerController = loader.getController();
                System.out.println("Setting Vendor #" + sharedVendor.getInstanceId() + " in CustomerController");
                customerController.setVendor(sharedVendor);
            }

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}