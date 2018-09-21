package com.walmart.app;

import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.TreeMap;

/*
Timer Task to release held tickets in case of time expiry.
Calls the synchronized method on SeatHold object to avoid clashes with reserving the same tickets at the same time.
 */
public class ScheduleTask extends TimerTask {
    private Venue venue;
    private SeatHold seatsInfo;
    private Map<Integer,SeatHold> heldTickets;
    private Map<Integer, List<List<Integer>>> rowSpaceMap;
    private TreeMap<Integer, List<Integer>> continuousSpaceMap;

    public ScheduleTask(int seatHoldId, Map<Integer,SeatHold> heldTickets, Map<Integer, List<List<Integer>>> rowSpaceMap, TreeMap<Integer, List<Integer>> continuousSpaceMap, Venue venue) {
        this.venue = venue;
        this.heldTickets = heldTickets;
        this.rowSpaceMap = rowSpaceMap;
        this.continuousSpaceMap = continuousSpaceMap;
        this.seatsInfo = heldTickets.get(seatHoldId);
    }

    @Override
    public void run() {
        try {
            seatsInfo.bookOrReleaseTickets(heldTickets, ReserveOrRelease.EXPIRED,null, rowSpaceMap, continuousSpaceMap, venue);
        } catch (Exception e) {}
    }
}
