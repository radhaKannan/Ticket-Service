package com.walmart.app;

import com.walmart.app.Exceptions.SeatsUnavailableException;
import com.walmart.app.Exceptions.SessionExpiredException;

import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class App 
{
    public static void main( String[] args )
    {
        Scanner scan = new Scanner(System.in);
        System.out.print("Enter the number of rows in the venue: ");
        int row = scan.nextInt();
        System.out.println();
        System.out.print("Enter the number of columns in the venue: ");
        int col = scan.nextInt();
        System.out.println();
        Venue venue = Venue.getInstance(row, col);
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        TicketServiceImplementation ticketServiceImplementation = TicketServiceImplementation.getInstance(venue, scheduler);
        System.out.println("Press x if you want to stop booking tickets.");
        String choice = menuChoice();
        while(!choice.toLowerCase().equals("x")) {
            switch (choice) {
                case "1": {
                    System.out.println("Available seats: " + ticketServiceImplementation.numSeatsAvailable());
                    System.out.print("Press any letter to proceed.");
                    scan.next();
                    System.out.println();
                    break;
                }
                case "2": {
                    System.out.print("Number of seats to hold: ");
                    int numSeats = scan.nextInt();
                    System.out.println();
                    System.out.print("Email ID: ");
                    String email = scan.next();
                    try {
                        SeatHold seatHold = ticketServiceImplementation.findAndHoldSeats(numSeats, email);
                        System.out.println("Your hold ID is: " + seatHold.getSeatHoldId());
                        for (SeatsBlock seats : seatHold.getSeatsInfo()) {
                            System.out.println("Seats assigned in row: " + seats.getRow());
                            int start = seats.getStart();
                            while(start <= seats.getEnd()) {
                                System.out.println("Seat Number: " + start);
                                start++;
                            }
                        }
                    } catch (SeatsUnavailableException e) {
                        System.out.println(e.getMessage());
                    }
                    System.out.print("Press any letter to proceed.");
                    scan.next();
                    System.out.println();
                    break;
                }
                case "3": {
                    System.out.print("Enter hold ID: ");
                    int holdID = scan.nextInt();
                    System.out.println();
                    System.out.print("Enter email: ");
                    String email = scan.next();
                    System.out.println();
                    try {
                        System.out.println("Your confirmation code is: " + ticketServiceImplementation.reserveSeats(holdID, email));
                    } catch (IllegalAccessException e) {
                        System.out.println(e.getMessage());
                    } catch (SessionExpiredException e) {
                        System.out.println(e.getMessage());
                    }
                    System.out.print("Press any letter to proceed.");
                    scan.next();
                    System.out.println();
                    break;
                }
            }
            choice = menuChoice();
        }
        scheduler.shutdown();
        System.exit(0);
    }

    public static String menuChoice() {
        Scanner scan = new Scanner(System.in);
        System.out.println("\nMenu \n1. Availability \n2. Hold Tickets \n3. Book Tickets");
        System.out.print("Your choice (1, 2 or 3)? ");
        String choice = scan.next();
        System.out.println();
        return choice;
    }
}
