package com.walmart.app;

import com.walmart.app.Exceptions.SeatsUnavailableException;
import com.walmart.app.Exceptions.SessionExpiredException;

import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.walmart.app.Constants.*;

public class TicketServiceImplementation implements TicketService {
    private Venue venue;
    private static TicketServiceImplementation ticketService = null;
    private Map<Integer,SeatHold> heldTickets;
    private ScheduledExecutorService scheduler;

    private TicketServiceImplementation(Venue venue, ScheduledExecutorService scheduler) {
        this.venue = venue;
        this.scheduler = scheduler;
        this.heldTickets = new HashMap<>();
    }

    public static TicketServiceImplementation getInstance(Venue venue, ScheduledExecutorService scheduler) {
        if (ticketService == null)
            ticketService = new TicketServiceImplementation(venue, scheduler);
        return ticketService;
    }

    @Override
    public int numSeatsAvailable() {
        return venue.getNumSeatsAvailable();
    }

    @Override
    public synchronized SeatHold findAndHoldSeats(int numSeats, String customerEmail) throws SeatsUnavailableException {
        if (numSeats > venue.getNumSeatsAvailable())
            throw new SeatsUnavailableException(SEATS_UNAVAILABLE_MSG);
        List<Seat> seats = new ArrayList<>();
        States[][] layout = venue.getSeatInfo();
        int ticketsBooked = 0;
        for (int row = 0; row < layout.length; row++) {
            for(int col = 0; col < layout[row].length; col++) {
                if (layout[row][col] == States.AVAILABLE) {
                    seats.add(new Seat(row,col));
                    layout[row][col] = States.HELD;
                    ticketsBooked++;
                    if(ticketsBooked == numSeats)
                        break;
                }
            }
            if(ticketsBooked == numSeats)
                break;
        }
        Random random = new Random();
        int seatHoldId = random.nextInt();
        while(heldTickets.containsKey(seatHoldId))
            seatHoldId = random.nextInt();
        SeatHold seatHoldInfo = new SeatHold(seatHoldId, numSeats, customerEmail, seats);
        heldTickets.put(seatHoldId, seatHoldInfo);
        venue.setSeatInfo(layout);
        ScheduleTask scheduleTask = new ScheduleTask(seatHoldId, heldTickets, layout, venue);
        scheduler.schedule(scheduleTask, TIME_LEFT, TimeUnit.SECONDS);
        return seatHoldInfo;
    }

    @Override
    public String reserveSeats(int seatHoldId, String customerEmail) throws SessionExpiredException, IllegalAccessException {
        SeatHold ticketsInfo = heldTickets.get(seatHoldId);
        if (ticketsInfo == null)
            throw new SessionExpiredException(SESSION_EXPIRED_MSG);
        ticketsInfo.bookOrReleaseTickets(heldTickets, ReserveOrRelease.RESERVE, venue, customerEmail);
        return UUID.randomUUID().toString();
    }
}
