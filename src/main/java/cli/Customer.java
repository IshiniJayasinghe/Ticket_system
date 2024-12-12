package cli;

public class Customer implements Runnable {
    private final String name;
    private final Event event;
    private final int ticketsToBuy;
    private boolean purchaseSuccessful;

    public Customer(String name, Event event, int ticketsToBuy) {
        this.name = name;
        this.event = event;
        this.ticketsToBuy = ticketsToBuy;
    }

    @Override
    public void run() {
        System.out.println("\n" + name + " attempting to buy " + ticketsToBuy +
                " tickets for " + event.getName());
        purchaseSuccessful = event.purchaseTickets(ticketsToBuy);

        String result = purchaseSuccessful ? "successfully purchased" : "failed to purchase";
        System.out.println(name + " " + result + " " + ticketsToBuy +
                " tickets for " + event.getName());
        System.out.println("Current status: " + event.getStatus());
    }

    public boolean isPurchaseSuccessful() {
        return purchaseSuccessful;
    }
}