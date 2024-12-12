package cli;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EventManager {
    private static EventManager instance;
    private final ConcurrentHashMap<String, Event> events;
    private final ExecutorService executorService;

    private EventManager() {
        events = new ConcurrentHashMap<>();
        executorService = Executors.newFixedThreadPool(10);
    }

    public static synchronized EventManager getInstance() {
        if (instance == null) {
            instance = new EventManager();
        }
        return instance;
    }

    public void createEvent(String name, int totalTickets, int ticketsPerSession) {
        Event event = new Event(name, totalTickets, ticketsPerSession);
        events.put(name, event);
        System.out.println("Event created: " + name + " with " + totalTickets + " total tickets");
    }

    public void processPurchase(String customerName, String eventName, int ticketsToBuy) {
        Event event = events.get(eventName);
        if (event != null) {
            Customer customer = new Customer(customerName, event, ticketsToBuy);
            executorService.submit(customer);
        } else {
            System.out.println("Event not found: " + eventName);
        }
    }

    public Event getEvent(String eventName) {
        return events.get(eventName);
    }

    public void shutdown() {
        executorService.shutdown();
    }
}