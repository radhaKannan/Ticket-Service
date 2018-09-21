package com.walmart.app;

import com.walmart.app.Exceptions.SessionExpiredException;

import java.util.*;

import static com.walmart.app.Constants.ILLEGAL_ACCESS_MSG;
import static com.walmart.app.Constants.SESSION_EXPIRED_MSG;

/*
Stores the list of continuous space blocks that have been assigned to a hold request.
Has a synchronized method bookOrReleaseTickets to monitor reservations or expiration of a hold request.
 */
public class SeatHold {
    private int seatHoldId;
    private int numSeats;
    private String customerEmail;
    private List<SeatsBlock> seatsInfo;

    public SeatHold(int seatHoldId, int numSeats, String customerEmail, List<SeatsBlock> seatsInfo) {
        this.seatHoldId = seatHoldId;
        this.numSeats = numSeats;
        this.customerEmail = customerEmail;
        this.seatsInfo = seatsInfo;
    }

    public int getSeatHoldId() {
        return seatHoldId;
    }

    public int getNumSeats() {
        return numSeats;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public List<SeatsBlock> getSeatsInfo() {
        return seatsInfo;
    }

    /*
    In case of reserve tickets, checks for if the request is available in the heldTickets map or if it has expired.
    Also checks if the unique email associated with each hold request is the same id that is being used to book them.
    If valid reserve request, removes the hold request from heldTickets, indicating that those seats have been booked.

    In case of expiry of hold tickets and automatic trigger of release tickets:
    Check if the request has been already finalised or not.
    Iterate through all seat blocks in that seatHold object and add them back to the rowSpaceMap. Correspondingly,
    continuousSpaceMap should also be updated.
    Check if left block in rowSpaceMap is continuous with released block and merge if necessary. Check if right block
    is continuous and merge accordingly. Every time a merge happens, continuousSpaceMap needs to be updated too.
    Add the released seats back to the total number of seats available. Remove request from heldTickets map.
     */
    public synchronized void bookOrReleaseTickets(Map<Integer,SeatHold> heldTickets, ReserveOrRelease function, String customerEmail, Map<Integer, List<List<Integer>>> rowSpaceMap, TreeMap<Integer, List<Integer>> continuousSpaceMap, Venue venue) throws SessionExpiredException, IllegalAccessException {
        if (function == ReserveOrRelease.RESERVE) {
            if (!heldTickets.containsKey(this.getSeatHoldId()))
                throw new SessionExpiredException(SESSION_EXPIRED_MSG);
            if (!this.getCustomerEmail().equals(customerEmail))
                throw new IllegalAccessException(ILLEGAL_ACCESS_MSG);
            heldTickets.remove(this.getSeatHoldId());
        }
        else if (function == ReserveOrRelease.EXPIRED) {
            if (heldTickets.containsKey(this.getSeatHoldId())) {
                for (SeatsBlock seatsBlock : this.getSeatsInfo()) {
                    List<Integer> addBlock = new ArrayList<>();
                    addBlock.add(seatsBlock.getStart());
                    addBlock.add(seatsBlock.getEnd());
                    List<List<Integer>> rowSeatBlocks = rowSpaceMap.get(seatsBlock.getRow());
                    int continuousSeats = seatsBlock.getEnd() - seatsBlock.getStart() + 1;
                    int i = 0;
                    while (i < rowSeatBlocks.size()) {
                        if (rowSeatBlocks.get(i).get(0) > addBlock.get(1))
                            break;
                        i++;
                    }
                    rowSeatBlocks.add(i, addBlock);
                    List<Integer> continuousSpace = new ArrayList<>();
                    if (continuousSpaceMap.containsKey(continuousSeats))
                        continuousSpace = continuousSpaceMap.get(continuousSeats);
                    continuousSpace.add(seatsBlock.getRow());
                    continuousSpaceMap.put(continuousSeats, continuousSpace);
                    int mergeLeft = Utilities.mergeLeftRowMap(rowSeatBlocks, i, continuousSpaceMap, seatsBlock);
                    int mergeRight;
                    if (mergeLeft == 0)
                        mergeRight = Utilities.mergeLeftRowMap(rowSeatBlocks, i+1, continuousSpaceMap, seatsBlock);
                    else {
                        continuousSpace = new ArrayList<>();
                        if (continuousSpaceMap.containsKey(mergeLeft))
                            continuousSpace = continuousSpaceMap.get(mergeLeft);
                        continuousSpace.add(seatsBlock.getRow());
                        continuousSpaceMap.put(mergeLeft, continuousSpace);
                        mergeRight = Utilities.mergeLeftRowMap(rowSeatBlocks, i, continuousSpaceMap, seatsBlock);
                    }
                    if (mergeRight != 0) {
                        continuousSpace = new ArrayList<>();
                        if (continuousSpaceMap.containsKey(mergeRight))
                            continuousSpace = continuousSpaceMap.get(mergeRight);
                        continuousSpace.add(seatsBlock.getRow());
                        continuousSpaceMap.put(mergeRight, continuousSpace);
                    }
                }
                venue.setNumSeatsAvailable(venue.getNumSeatsAvailable()+this.getNumSeats());
                heldTickets.remove(this.getSeatHoldId());
            }
        }
    }
}