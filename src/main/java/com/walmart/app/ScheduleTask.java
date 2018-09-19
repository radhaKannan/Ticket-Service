package com.walmart.app;

import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.TreeMap;

public class ScheduleTask extends TimerTask {
    private SeatHold seatsInfo;
    private Map<Integer,SeatHold> heldTickets;
    private Map<Integer, List<List<Integer>>> rowSpaceMap;
    private TreeMap<Integer, List<Integer>> continuousSpaceMap;

    public ScheduleTask(int seatHoldId, Map<Integer,SeatHold> heldTickets, Map<Integer, List<List<Integer>>> rowSpaceMap, TreeMap<Integer, List<Integer>> continuousSpaceMap) {
        this.heldTickets = heldTickets;
        this.rowSpaceMap = rowSpaceMap;
        this.continuousSpaceMap = continuousSpaceMap;
        this.seatsInfo = heldTickets.get(seatHoldId);
    }

    @Override
    public void run() {
        try {
            seatsInfo.bookOrReleaseTickets(heldTickets, ReserveOrRelease.EXPIRED,null, rowSpaceMap, continuousSpaceMap);
        } catch (Exception e) {}
    }
}
