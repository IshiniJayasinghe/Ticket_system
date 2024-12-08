package cli;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class VendorController {
    @FXML
    private TextField MaxCapacity;

    @FXML
    private TextField TicketReleaseRate;

    @FXML
    private TextField CustomerRetrievalRate;

    @FXML
    private TextField MaxTicketCapacityPerSession;

    @FXML
    private TextField EventName;

    @FXML
    private Label Submit;

    @FXML
            private Button OK;

    @FXML
            private void handleExit(){
        System.exit(0);
    }

    @FXML
            private Button Cancel;

    OpenSQL db = new OpenSQL();

    public void ToTheDatabase() throws SQLException {
        OK.setOnAction(e->{
        int Maxcapacity = Integer.parseInt(MaxCapacity.getText());
        int Ticketreleaserate = Integer.parseInt(TicketReleaseRate.getText());
        int Customerretrievalrate = Integer.parseInt(CustomerRetrievalRate.getText());
        int Maxticketcapacity_Per_session = Integer.parseInt(MaxTicketCapacityPerSession.getText());
        String Eventname = EventName.getText();



        String insertQuery = "INSERT INTO event (Event Name, Max Ticket Capacity, Per Session Capacity) VALUES(?, ?, ?)";

        try(Connection con = db.initializeConnection()){
            PreparedStatement pd = con.prepareStatement(insertQuery);

            pd.setString(1,Eventname);
            pd.setInt(2, Maxcapacity);
            pd.setInt(3, Maxticketcapacity_Per_session);


            int rowsAffected = pd.executeUpdate();
            if (rowsAffected > 0){
                Submit.setText("");
                Submit.setText("Event details added successfully...");
                showAlert("Success", "Event details added successfully...", Alert.AlertType.INFORMATION);
            }

            else {
                Submit.setText("");
                Submit.setText("Failed to add event details...");
                showAlert("Failed", "Failed to add event details...", Alert.AlertType.WARNING);
            }
        }
        catch (Exception E){
            E.printStackTrace();
        }
        });
    }

    public static void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }


}
