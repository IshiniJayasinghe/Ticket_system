package org.example.demo;

import java.util.Scanner;

public class Config {
    Scanner sc = new Scanner(System.in);
    public int maxTicketCapacity;
    public int ReleasePerSession;
    public int ticketReleaseRate;
    public int customerRetrievalRate;

    public void configureDetails(){
        System.out.println("Enter max capacity: ");
        this.maxTicketCapacity = sc.nextInt();

        System.out.println("Enter release per session: ");
        this.ReleasePerSession = sc.nextInt();

        System.out.println("Enter ticket release rate: ");
        this.ticketReleaseRate = sc.nextInt();

        System.out.println("Enter customer retrieval rate: ");
        this.customerRetrievalRate = sc.nextInt();
    }

}
