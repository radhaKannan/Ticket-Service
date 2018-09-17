package com.walmart.app;

import com.walmart.app.Exceptions.SessionExpiredException;

import java.util.Map;
import java.util.TimerTask;

public class ScheduleTask extends TimerTask {
    private int seatHoldId;
    private Map<Integer,SeatHold> heldTickets;
    private States[][] layout;
    SeatHold seatsInfo;
    Venue venue;

    public ScheduleTask(int seatHoldId, Map<Integer,SeatHold> heldTickets, States[][] layout, Venue venue) {
        this.seatHoldId = seatHoldId;
        this.heldTickets = heldTickets;
        this.layout = layout;
        this.seatsInfo = heldTickets.get(seatHoldId);
        this.venue = venue;
    }

    @Override
    public void run() {
        try {
            seatsInfo.bookOrReleaseTickets(heldTickets, ReserveOrRelease.EXPIRED, venue, null);
        } catch (Exception e) {}
    }
}
