package cli;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CustomerController {
    public Button GoBack;
    public Label PurchaseStatus;
    @FXML
    private ComboBox<String> EventDropdown;

    @FXML
    private TextField TicketsToBuy;

    @FXML
    private Button BuyButton;

    @FXML
    private Button Cancel;

    Vendor vendor = new Vendor();
    OpenSQL db = new OpenSQL();
    private final ExecutorService executorService = Executors.newFixedThreadPool(5); // Thread pool with 5 threads

    @FXML
    public void initialize() {
        // Populate the dropdown menu with events from the database
        loadEventsIntoDropdown();
    }

    @FXML
    private void loadEventsIntoDropdown() {

        String query = "SELECT `Event Name` FROM event";

        try (Connection con = db.initializeConnection();
             PreparedStatement stmt = con.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String eventName = rs.getString("Event Name");
                EventDropdown.getItems().add(eventName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load events from the database: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void handlePurchase() {
        String eventName = EventDropdown.getValue();
        int ticketsToBuy;

        try {
            ticketsToBuy = Integer.parseInt(TicketsToBuy.getText());
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid number of tickets to buy.", Alert.AlertType.ERROR);
            return;
        }

        // Submit the ticket purchase task to the thread pool
        executorService.submit(() -> purchaseTickets(eventName, ticketsToBuy));
    }

    private void purchaseTickets(String eventName, int ticketsToBuy) {
        System.out.println("CustomerController using Vendor #" + vendor.getInstanceId() +
                " with rate: " + vendor.getTicketReleaseRate());
        int missingTickets;
        String selectQuery = "SELECT `Max Ticket Capacity`, `Per Session Capacity` FROM event WHERE `Event Name` = ?";
        String updateTicketsQuery = "UPDATE event SET `Max Ticket Capacity` = ?, `Per Session Capacity` = ? WHERE `Event Name` = ?";

        try (Connection con = db.initializeConnection();
             PreparedStatement selectStmt = con.prepareStatement(selectQuery);
             PreparedStatement updateStmt = con.prepareStatement(updateTicketsQuery)) {

            // Fetch event details
            selectStmt.setString(1, eventName);
            ResultSet rs = selectStmt.executeQuery();

            if (rs.next()) {
                int availableTickets = rs.getInt("Max Ticket Capacity");
                int ticketsPerSession = rs.getInt("Per Session Capacity");
                missingTickets = ticketsToBuy - ticketsPerSession;
                // Check if enough tickets are available in the session
                if (ticketsToBuy <= availableTickets) {
                    if (missingTickets < 0) {
                        availableTickets = availableTickets - missingTickets;
                        ticketsPerSession = ticketsPerSession + missingTickets;
                    }
                    ticketsPerSession -= ticketsToBuy;  // Deduct tickets from the current session
                    availableTickets -= ticketsToBuy;   // Deduct tickets from the available total

                    System.out.println("Vendor released " + vendor.getTicketReleaseRate());
                    // Refill session if depleted
                    if (ticketsPerSession <= 0 && availableTickets > 0) {
                        ticketsPerSession = Math.min(availableTickets, vendor.getTicketReleaseRate());
                    }

                    // Update the database
                    updateStmt.setInt(1, availableTickets);
                    updateStmt.setInt(2, ticketsPerSession);
                    updateStmt.setString(3, eventName);

                    int rowsAffected = updateStmt.executeUpdate();
                    if (rowsAffected > 0) {
                        updateUI("Tickets purchased successfully.", Alert.AlertType.INFORMATION);
                    } else {
                        updateUI("Failed to update tickets.", Alert.AlertType.ERROR);
                    }
                } else {
                    updateUI("Not enough tickets available in the current session.", Alert.AlertType.WARNING);
                }
            } else {
                updateUI("Event not found.", Alert.AlertType.WARNING);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            updateUI("Database error occurred: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void updateUI(String message, Alert.AlertType alertType) {
        Platform.runLater(() -> {
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
        System.out.println("CustomerController received Vendor #" + sharedVendor.getInstanceId());
        this.vendor = sharedVendor;
    }

    @FXML
    private void shutdownExecutorService() {
        System.exit(0);
    }

    @FXML
    private void handleExit() {
        try {
            // Load the VendorCustomer FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("VendorCustomer.fxml")); // Ensure this path is correct
            Parent vendorCustomerRoot = loader.load();

            // Get the current stage
            Stage currentStage = (Stage) Cancel.getScene().getWindow();

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
