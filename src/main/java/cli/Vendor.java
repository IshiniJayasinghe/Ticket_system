package cli;

import javafx.scene.control.Alert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Vendor {
    private volatile int TicketReleaseRate = 0;
    private static int instanceCount = 0;
    private final int instanceId;

    public Vendor() {
        instanceId = ++instanceCount;
        System.out.println("Creating Vendor instance #" + instanceId);
    }

    public synchronized void setTicketReleaseRate(int ticketReleaseRate) {
        System.out.println("Vendor #" + instanceId + " - Setting TicketReleaseRate to: " + ticketReleaseRate);
        this.TicketReleaseRate = ticketReleaseRate;
    }

    public synchronized int getTicketReleaseRate() {
        System.out.println("Vendor #" + instanceId + " - Getting TicketReleaseRate: " + TicketReleaseRate);
        return TicketReleaseRate;
    }

    public int getInstanceId() {
        return instanceId;
    }


}
