package com.walmart.app;

import com.walmart.app.Exceptions.SessionExpiredException;

import java.util.List;
import java.util.Map;

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

    public synchronized void bookOrReleaseTickets(Map<Integer,SeatHold> heldTickets, ReserveOrRelease function, Venue venue, String customerEmail) throws SessionExpiredException, IllegalAccessException {
        States[][] layout = venue.getSeatInfo();
        if (function == ReserveOrRelease.RESERVE) {
            if (!heldTickets.containsKey(this.getSeatHoldId())) {
                throw new SessionExpiredException(SESSION_EXPIRED_MSG);
            }
            if (!this.getCustomerEmail().equals(customerEmail)) {
                throw new IllegalAccessException(ILLEGAL_ACCESS_MSG);
            }
            for(Seat seat : this.getSeatsInfo()) {
                layout[seat.getRow()][seat.getColumn()] = States.RESERVED;
            }
            venue.setSeatInfo(layout);
            heldTickets.remove(this.getSeatHoldId());
        }
        else if (function == ReserveOrRelease.EXPIRED) {
            if (heldTickets.containsKey(this.getSeatHoldId())) {
                for (Seat seat : this.getSeatsInfo()) {
                    layout[seat.getRow()][seat.getColumn()] = States.AVAILABLE;
                }
                venue.setSeatInfo(layout);
                heldTickets.remove(this.getSeatHoldId());
            }
        }
    }
}