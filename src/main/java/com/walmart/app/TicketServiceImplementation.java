package com.walmart.app;

import com.walmart.app.Exceptions.SeatsUnavailableException;
import com.walmart.app.Exceptions.SessionExpiredException;

import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.walmart.app.Constants.*;

/*
Implementation of TicketService interface. Singleton class. Synchronized on hold seats method.
No 2 objects of this instance can want to hold seats at same time. Best available seats assigned on first come basis.
Maintains three maps to keep track of the seats available in the venue and to check for validity of request.
rowSpaceMap keeps track of the different continuous space blocks in each row.
continuousSpaceMap keeps track of the rows corresponding to a continuous space.
heldTickets keeps track of the hold requests that have been placed.
 */
public class TicketServiceImplementation implements TicketService {
    private Venue venue;
    private Map<Integer,SeatHold> heldTickets;
    private ScheduledExecutorService scheduler;
    private Map<Integer, List<List<Integer>>> rowSpaceMap;
    private TreeMap<Integer, List<Integer>> continuousSpaceMap;
    private static TicketServiceImplementation ticketService = null;

    private TicketServiceImplementation(Venue venue, ScheduledExecutorService scheduler) {
        this.venue = venue;
        this.scheduler = scheduler;
        this.heldTickets = venue.getHeldTickets();
        this.rowSpaceMap = venue.getRowSpaceMap();
        this.continuousSpaceMap = venue.getContinuousSpaceMap();
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

    /*
    If a continuous seat block is available, assign that.
    Else, keep assigning the maximum seat blocks until the maximum block is greater than seats needed.
    After that, if still seats needed is not zero, modify the maximum block and assign parts of it.
    Update the total number of seats available in the venue.
    Create the SeatHold object, add it to the heldTickets map. Schedule the timer task.
     */
    @Override
    public synchronized SeatHold findAndHoldSeats(int numSeats, String customerEmail) throws SeatsUnavailableException {
        if (numSeats < 1 || numSeats > venue.getNumSeatsAvailable())
            throw new SeatsUnavailableException(SEATS_UNAVAILABLE_MSG);
        List<SeatsBlock> seatBlocks = new ArrayList<>();
        if (continuousSpaceMap.containsKey(numSeats))
            seatBlocks.add(Utilities.assignSeatBlocks(continuousSpaceMap, numSeats, rowSpaceMap));
        else {
            int seatsNeeded = numSeats;
            int maxContinuous = continuousSpaceMap.lastKey();
            while (seatsNeeded != 0 && maxContinuous <= seatsNeeded) {
                seatBlocks.add(Utilities.assignSeatBlocks(continuousSpaceMap, maxContinuous, rowSpaceMap));
                seatsNeeded = seatsNeeded - maxContinuous;
                if (!continuousSpaceMap.isEmpty()) {
                    if (continuousSpaceMap.containsKey(seatsNeeded))
                        maxContinuous = seatsNeeded;
                    else
                        maxContinuous = continuousSpaceMap.lastKey();
                }
            }
            if (seatsNeeded > 0)
                seatBlocks.add(Utilities.assignRemainingSeatBlock(continuousSpaceMap, seatsNeeded, rowSpaceMap));
        }
        venue.setNumSeatsAvailable(venue.getNumSeatsAvailable()-numSeats);
        Random random = new Random();
        int seatHoldId = random.nextInt() & Integer.MAX_VALUE;
        while(heldTickets.containsKey(seatHoldId))
            seatHoldId = random.nextInt();
        SeatHold seatHoldInfo = new SeatHold(seatHoldId, numSeats, customerEmail, seatBlocks);
        heldTickets.put(seatHoldId, seatHoldInfo);
        ScheduleTask scheduleTask = new ScheduleTask(seatHoldId, heldTickets, rowSpaceMap, continuousSpaceMap, venue);
        scheduler.schedule(scheduleTask, TIME_LEFT, TimeUnit.SECONDS);
        return seatHoldInfo;
    }

    /*
    Call the method bookOrReleaseTickets, that is synchronized on the seatHold object, so that no two threads book and
    release the same tickets at the same time. Throws exception if seatHoldId is not present in the heldTickets map.
     */
    @Override
    public String reserveSeats(int seatHoldId, String customerEmail) throws SessionExpiredException, IllegalAccessException {
        SeatHold ticketsInfo = heldTickets.get(seatHoldId);
        if (ticketsInfo == null)
            throw new SessionExpiredException(SESSION_EXPIRED_MSG);
        ticketsInfo.bookOrReleaseTickets(heldTickets, ReserveOrRelease.RESERVE, customerEmail, null, null, null);
        return UUID.randomUUID().toString();
    }
}
