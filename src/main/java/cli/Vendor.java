package cli;

public class Vendor {
    private int TotalTickets;
    private int TicketReleaseRate;
    private int CustomerRetrievalRate;
    private int MaxTicketCapacity;

    public void Configure(int totalTickets, int ticketReleaseRate, int customerRetrievalRate, int maxTicketCapacity){
        this.TotalTickets = totalTickets;
        this.TicketReleaseRate = ticketReleaseRate;
        this.CustomerRetrievalRate = customerRetrievalRate;
        this.MaxTicketCapacity = maxTicketCapacity;

    }
}
