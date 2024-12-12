package cli;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VendorController {

    OpenSQL db = new OpenSQL();

    @FXML
    private TextField MaxCapacity;

    @FXML
    private TextField MaxTicketCapacityPerSession;

    @FXML
    private TextField EventName;

    @FXML
    private Button GoBack;

    @FXML
    private Label Submit;

    @FXML
    private Button OK;

    Vendor vendor = new Vendor();

    private final ExecutorService executorService = Executors.newFixedThreadPool(5); // Thread pool with 5 threads
    private final EventManager eventManager = EventManager.getInstance();
    int maxTicketCapacityPerSession;

    @FXML
    public void ToTheDatabase() {
        String eventName = EventName.getText();
        int maxCapacity;

        try {
            maxCapacity = Integer.parseInt(MaxCapacity.getText());
            maxTicketCapacityPerSession = Integer.parseInt(MaxTicketCapacityPerSession.getText());
            System.out.println("VendorController setting rate " + maxTicketCapacityPerSession +
                    " on Vendor #" + vendor.getInstanceId());

            EventManager.getInstance().createEvent(eventName, maxCapacity, maxTicketCapacityPerSession);
            // Set the rate immediately
            vendor.setTicketReleaseRate(maxTicketCapacityPerSession);

            // Verify the rate was set
            System.out.println("Verification - Current rate for Vendor #" +
                    vendor.getInstanceId() + " is: " + vendor.getTicketReleaseRate());

            // Now submit the database operation
            executorService.submit(() -> insertEventDetails(eventName, maxCapacity, maxTicketCapacityPerSession));

            Event event = eventManager.getEvent(eventName);
            if (event!=null){
                System.out.println("Event created and stored: "+event.getStatus());
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter valid numbers for capacities.", Alert.AlertType.ERROR);
        }
    }

    private void insertEventDetails(String eventName, int maxCapacity, int maxTicketCapacityPerSession) {
        String insertQuery = "INSERT INTO event (`Event Name`, `Max Ticket Capacity`, `Per Session Capacity`) VALUES (?, ?, ?)";

        try (Connection con = db.initializeConnection(); PreparedStatement pd = con.prepareStatement(insertQuery)) {
            pd.setString(1, eventName);
            pd.setInt(2, maxCapacity);
            pd.setInt(3, maxTicketCapacityPerSession);

            int rowsAffected = pd.executeUpdate();
            if (rowsAffected > 0) {
                updateUI("Event details added successfully...", Alert.AlertType.INFORMATION);
            } else {
                updateUI("Failed to add event details...", Alert.AlertType.WARNING);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            updateUI("Database error occurred: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }


    private void updateUI(String message, Alert.AlertType alertType) {
        // Update UI elements on the JavaFX Application Thread
        javafx.application.Platform.runLater(() -> {
            showAlert(alertType == Alert.AlertType.INFORMATION ? "Success" : "Error", message, alertType);
        });
    }

    public static void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    public void setVendor(Vendor sharedVendor) {
        System.out.println("VendorController received Vendor #" + sharedVendor.getInstanceId());
        this.vendor = sharedVendor;
    }

    public void shutdownExecutorService() {
        executorService.shutdown();
    }

    @FXML
    private void handleExit() {
        try {
            // Load the VendorCustomer FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("VendorCustomer.fxml")); // Ensure this path is correct
            Parent vendorCustomerRoot = loader.load();

            // Get the current stage
            Stage currentStage = (Stage) GoBack.getScene().getWindow();

            // Set the VendorCustomer scene
            Scene vendorCustomerScene = new Scene(vendorCustomerRoot);
            currentStage.setScene(vendorCustomerScene);
            currentStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load the menu: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

}