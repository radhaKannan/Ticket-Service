package com.walmart.app;

import com.walmart.app.Exceptions.SessionExpiredException;

import java.util.*;

import static com.walmart.app.Constants.ILLEGAL_ACCESS_MSG;
import static com.walmart.app.Constants.SESSION_EXPIRED_MSG;

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
                    int continuousSeats = seatsBlock.getEnd() - seatsBlock.getRow() + 1;
                    int i = 0;
                    while (i < rowSeatBlocks.size()) {
                        if (rowSeatBlocks.get(i).get(0) > addBlock.get(1))
                            break;
                        i++;
                    }
                    rowSeatBlocks.add(i, addBlock);
                    int mergeLeft = Utilities.mergeLeftRowMap(rowSeatBlocks, i, continuousSpaceMap, seatsBlock);
                    int mergeRight;
                    if (mergeLeft == 0)
                        mergeRight = Utilities.mergeLeftRowMap(rowSeatBlocks, i+1, continuousSpaceMap, seatsBlock);
                    else
                        mergeRight = Utilities.mergeLeftRowMap(rowSeatBlocks, i, continuousSpaceMap, seatsBlock);
                    if (mergeRight != 0)
                        continuousSeats = mergeRight;
                    else if (mergeLeft != 0)
                        continuousSeats = mergeLeft;
                    List<Integer> continuousSpace = new ArrayList<>();
                    if (continuousSpaceMap.containsKey(continuousSeats))
                        continuousSpace = continuousSpaceMap.get(continuousSeats);
                    continuousSpace.add(seatsBlock.getRow());
                    continuousSpaceMap.put(continuousSeats, continuousSpace);
                }
                venue.setNumSeatsAvailable(venue.getNumSeatsAvailable()-this.getNumSeats());
                heldTickets.remove(this.getSeatHoldId());
            }
        }
    }
}