package com.walmart.app;

import com.walmart.app.Exceptions.SessionExpiredException;

import java.util.*;

import static com.walmart.app.Constants.ILLEGAL_ACCESS_MSG;
import static com.walmart.app.Constants.SESSION_EXPIRED_MSG;

public class SeatHold {
    private int seatHoldId;
    private int numSeats;
    private String customerEmail;
    private List<Seat> seatsInfo;

    public SeatHold(int seatHoldId, int numSeats, String customerEmail, List<Seat> seatsInfo) {
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

    public List<Seat> getSeatsInfo() {
        return seatsInfo;
    }

    public synchronized void bookOrReleaseTickets(Map<Integer,SeatHold> heldTickets, ReserveOrRelease function, String customerEmail, Map<Integer, List<List<Integer>>> rowSpaceMap, TreeMap<Integer, List<Integer>> continuousSpaceMap) throws SessionExpiredException, IllegalAccessException {
        if (function == ReserveOrRelease.RESERVE) {
            if (!heldTickets.containsKey(this.getSeatHoldId()))
                throw new SessionExpiredException(SESSION_EXPIRED_MSG);
            if (!this.getCustomerEmail().equals(customerEmail))
                throw new IllegalAccessException(ILLEGAL_ACCESS_MSG);
            heldTickets.remove(this.getSeatHoldId());
        }
        else if (function == ReserveOrRelease.EXPIRED) {
            if (heldTickets.containsKey(this.getSeatHoldId())) {
                for (Seat seat : this.getSeatsInfo()) {
                    List<Integer> addBlock = new ArrayList<>();
                    addBlock.add(seat.getStart());
                    addBlock.add(seat.getEnd());
                    List<List<Integer>> rowSeatBlocks = rowSpaceMap.get(seat.getRow());
                    int continuousSeats = seat.getEnd() - seat.getRow() + 1;
                    int i = 0;
                    while (i < rowSeatBlocks.size()) {
                        if (rowSeatBlocks.get(i).get(0) > addBlock.get(1))
                            break;
                        i++;
                    }
                    rowSeatBlocks.add(i, addBlock);
                    int mergeLeft = Utilities.mergeLeftRowMap(rowSeatBlocks, i, continuousSpaceMap, seat);
                    int mergeRight;
                    if (mergeLeft == 0)
                        mergeRight = Utilities.mergeLeftRowMap(rowSeatBlocks, i+1, continuousSpaceMap, seat);
                    else
                        mergeRight = Utilities.mergeLeftRowMap(rowSeatBlocks, i, continuousSpaceMap, seat);
                    if (mergeRight != 0)
                        continuousSeats = mergeRight;
                    else if (mergeLeft != 0)
                        continuousSeats = mergeLeft;
                    List<Integer> continuousSpace = new ArrayList<>();
                    if (continuousSpaceMap.containsKey(continuousSeats))
                        continuousSpace = continuousSpaceMap.get(continuousSeats);
                    continuousSpace.add(seat.getRow());
                    continuousSpaceMap.put(continuousSeats, continuousSpace);
                }
                heldTickets.remove(this.getSeatHoldId());
            }
        }
    }
}