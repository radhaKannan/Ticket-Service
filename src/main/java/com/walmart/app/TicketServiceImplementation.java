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
    private TreeMap<Integer, List<Integer>> continuousSpaceMap;
    private Map<Integer, List<List<Integer>>> rowSpaceMap;

    private TicketServiceImplementation(Venue venue, ScheduledExecutorService scheduler) {
        this.venue = venue;
        this.scheduler = scheduler;
        this.heldTickets = new HashMap<>();
        this.continuousSpaceMap = Utilities.initializeContinuousSpace(venue);
        this.rowSpaceMap = Utilities.initializeRowSpace(venue);
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

        if (continuousSpaceMap.containsKey(numSeats))
            seats.add(Utilities.assignSeatBlocks(continuousSpaceMap, numSeats, rowSpaceMap));
        else {
            int seatsNeeded = numSeats;
            int maxContinuous = continuousSpaceMap.lastKey();
            while (maxContinuous <= seatsNeeded) {
                seats.add(Utilities.assignSeatBlocks(continuousSpaceMap, maxContinuous, rowSpaceMap));
                seatsNeeded = seatsNeeded - maxContinuous;
                maxContinuous = continuousSpaceMap.lastKey();
            }
            if (seatsNeeded > 0)
                seats.add(Utilities.assignRemainingSeatBlock(continuousSpaceMap, maxContinuous, rowSpaceMap));
        }

        Random random = new Random();
        int seatHoldId = random.nextInt();
        while(heldTickets.containsKey(seatHoldId))
            seatHoldId = random.nextInt();
        SeatHold seatHoldInfo = new SeatHold(seatHoldId, numSeats, customerEmail, seats);
        heldTickets.put(seatHoldId, seatHoldInfo);
        ScheduleTask scheduleTask = new ScheduleTask(seatHoldId, heldTickets, rowSpaceMap, continuousSpaceMap);
        scheduler.schedule(scheduleTask, TIME_LEFT, TimeUnit.SECONDS);
        return seatHoldInfo;
    }

    @Override
    public String reserveSeats(int seatHoldId, String customerEmail) throws SessionExpiredException, IllegalAccessException {
        SeatHold ticketsInfo = heldTickets.get(seatHoldId);
        if (ticketsInfo == null)
            throw new SessionExpiredException(SESSION_EXPIRED_MSG);
        ticketsInfo.bookOrReleaseTickets(heldTickets, ReserveOrRelease.RESERVE, customerEmail, null, null);
        return UUID.randomUUID().toString();
    }
}
