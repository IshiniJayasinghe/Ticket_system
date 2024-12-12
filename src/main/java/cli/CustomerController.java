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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CustomerController {
    @FXML
    private ComboBox<String> EventDropdown;

    @FXML
    private TextField TicketsToBuy;


    @FXML
    private Button BuyButton;

    private Event event;
    @FXML
    private Button Cancel;

    @FXML
    private Button GoBack;
    private final EventManager eventManager = EventManager.getInstance();

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
        String query = "SELECT 'Event Name' FROM event";
        try (Connection con = db.initializeConnection();
             PreparedStatement stmt = con.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            // Temporarily store event names to process later
            List<String> eventNames = new ArrayList<>();
            while (rs.next()) {
                eventNames.add(rs.getString("Event Name"));
            }

            // Process each event after the ResultSet is closed
            for (String eventName : eventNames) {
                if (eventManager.getEvent(eventName) == null) {
                    loadEventFromDatabase(eventName);
                }
                EventDropdown.getItems().add(eventName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load events from the database: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    private void loadEventFromDatabase(String eventName) {
        String query = "SELECT 'Max Ticket Capacity', 'Per Session Capacity' FROM event WHERE 'Event Name' = ?";

        try (Connection con = db.initializeConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {

            stmt.setString(1, eventName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int availableTickets = rs.getInt("Max Ticket Capacity");
                int ticketsPerSession = rs.getInt("Per Session Capacity");
                eventManager.createEvent(eventName, availableTickets, ticketsPerSession);
                System.out.println("Loaded event from database: " + eventName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void handlePurchase() {
        String eventName = EventDropdown.getValue();
        try {
            int ticketsToBuy = Integer.parseInt(TicketsToBuy.getText());
            event = eventManager.getEvent(eventName);

            if (event == null) {
                System.out.println("Event not found in manager: " + eventName);
                // Try to load it from database
                loadEventFromDatabase(eventName);
                event = eventManager.getEvent(eventName);
            }

            if (event != null) {
                String customerId = "Customer-" + System.currentTimeMillis();
                System.out.println("Processing purchase for event: " + event.getStatus());
                eventManager.processPurchase(customerId, eventName, ticketsToBuy);
                purchaseTickets(eventName, ticketsToBuy);
            } else {
                showAlert("Error", "Event not found: " + eventName, Alert.AlertType.ERROR);
            }

        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid number of tickets.", Alert.AlertType.ERROR);
        }
    }

    private void purchaseTickets(String eventName, int ticketsToBuy) {
        System.out.println("CustomerController using Vendor #" + vendor.getInstanceId() +
                " with rate: " + vendor.getTicketReleaseRate());


        String selectQuery = "SELECT 'Max Ticket Capacity', 'Per Session Capacity' FROM event WHERE 'Event Name' = ?";
        String updateTicketsQuery = "UPDATE event SET 'Max Ticket Capacity' = ?, 'Per Session Capacity' = ? WHERE 'Event Name' = ?";

        try (Connection con = db.initializeConnection();
             PreparedStatement selectStmt = con.prepareStatement(selectQuery);
             PreparedStatement updateStmt = con.prepareStatement(updateTicketsQuery)) {

            // Fetch event details
            selectStmt.setString(1, eventName);
            ResultSet rs = selectStmt.executeQuery();

            if (rs.next()) {
                int availableTickets = event.getAvailableTickets();
                System.out.println("Available tick: "+availableTickets);
                int ticketsPerSession = event.getTicketsPerSession();
                System.out.println("Available per sesh: "+ticketsPerSession);

                // Update the database
                updateStmt.setInt(1, availableTickets);
                updateStmt.setInt(2, ticketsPerSession);
                updateStmt.setString(3, eventName);
                updateStmt.executeUpdate();

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