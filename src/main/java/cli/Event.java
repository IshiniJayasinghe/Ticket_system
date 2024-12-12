package cli;

public class Event {
    private final String name;
    private volatile int totalTicketCapacity;  // Maximum total tickets
    private volatile int availableTickets;     // Current total available
    private volatile int ticketsPerSession;    // Current session tickets
    private final int maxTicketsPerSession;    // Maximum tickets per session
    private final Object lock = new Object();

    public Event(String name, int totalTickets, int maxTicketsPerSession) {
        this.name = name;
        this.totalTicketCapacity = totalTickets;
        this.availableTickets = totalTickets;
        this.maxTicketsPerSession = maxTicketsPerSession;
        this.ticketsPerSession = Math.min(maxTicketsPerSession, totalTickets);

        System.out.println("Event created: " + name);
        System.out.println("Total capacity: " + totalTickets);
        System.out.println("Per session capacity: " + ticketsPerSession);
    }

    public boolean purchaseTickets(int quantity) {
        synchronized (lock) {
            System.out.println("\nPurchase attempt for " + name + ":");
            System.out.println("Requested tickets: " + quantity);
            System.out.println("Current session available: " + ticketsPerSession);
            System.out.println("Total available: " + availableTickets);

            // Check if we have enough tickets both in session and total
            if ((quantity <= ticketsPerSession && quantity <= availableTickets)||(quantity > ticketsPerSession && quantity <= availableTickets)) {
                // Deduct tickets from both pools
                ticketsPerSession -= quantity;
                availableTickets -= quantity;

                System.out.println("Purchase successful!");
                System.out.println("Remaining in session: " + ticketsPerSession);
                System.out.println("Total remaining: " + availableTickets);

                // Check if session needs refill
                if (ticketsPerSession <= 0 && availableTickets > 0) {
                    refillSession();
                }

                return true;
            } else {
                System.out.println("Purchase failed - insufficient tickets");
                if (availableTickets > 0 && ticketsPerSession <= 0) {
                    refillSession();
                }
                return false;
            }
        }
    }

    private void refillSession() {
        synchronized (lock) {
            int refillAmount = Math.min(maxTicketsPerSession, availableTickets);
            ticketsPerSession = refillAmount;
            System.out.println("Session refilled with " + refillAmount + " tickets");
            System.out.println("New session capacity: " + ticketsPerSession);
            System.out.println("Remaining total capacity: " + availableTickets);
        }
    }

    public int getAvailableTickets() {
        synchronized (lock) {
            return availableTickets;
        }
    }

    public int getTicketsPerSession() {
        synchronized (lock) {
            return ticketsPerSession;
        }
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        synchronized (lock) {
            return String.format("Event: %s, Session Available: %d, Total Available: %d",
                    name, ticketsPerSession, availableTickets);
        }
    }
}