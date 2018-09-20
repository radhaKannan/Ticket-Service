package com.walmart.app;

import com.walmart.app.Exceptions.SessionExpiredException;
import com.walmart.app.mockData.DummyObjects;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class SeatHoldTest {
    Venue mockedVenue = mock(Venue.class);
    Map<Integer,SeatHold> heldTickets = new HashMap<>();
    SeatHold seatHold;

    @Test
    public void reserveTickets() throws SessionExpiredException, IllegalAccessException {
        seatHold = new SeatHold(1, 0, "email", null);
        heldTickets.put(1,seatHold);
        seatHold.bookOrReleaseTickets(heldTickets, ReserveOrRelease.RESERVE, "email", null, null, mockedVenue);
        assertFalse(heldTickets.containsKey(1));
    }

    @Test (expected = SessionExpiredException.class)
    public void sessionExpired() throws SessionExpiredException, IllegalAccessException {
        seatHold = new SeatHold(1, 0, "email", null);
        seatHold.bookOrReleaseTickets(heldTickets, ReserveOrRelease.RESERVE, "email", null, null, mockedVenue);
    }

    @Test (expected = IllegalAccessException.class)
    public void illegalAccess() throws SessionExpiredException, IllegalAccessException {
        seatHold = new SeatHold(1, 0, "email", null);
        heldTickets.put(1,seatHold);
        seatHold.bookOrReleaseTickets(heldTickets, ReserveOrRelease.RESERVE, "mail", null, null, mockedVenue);
    }

    @Test
    public void releaseTickets() throws SessionExpiredException, IllegalAccessException {
        SeatsBlock seatsBlock = new SeatsBlock(0,2,3);
        List<SeatsBlock> rowBlock = new ArrayList<>();
        rowBlock.add(seatsBlock);
        seatHold = new SeatHold(1, 2, "email", rowBlock);
        heldTickets.put(1,seatHold);
        TreeMap<Integer, List<Integer>> continuousSpaceMap = DummyObjects.returnContinuousSpace();
        continuousSpaceMap.get(4).remove(0);
        List<Integer> rowList = new ArrayList<>();
        rowList.add(0);
        rowList.add(0);
        continuousSpaceMap.put(2,rowList);
        Map<Integer, List<List<Integer>>> rowSpaceMap = DummyObjects.returnRowSpace();
        List<List<Integer>> rowBlocks = rowSpaceMap.get(0);
        rowBlocks.get(0).set(1,1);
        rowList = new ArrayList<>();
        rowList.add(4);
        rowList.add(5);
        rowBlocks.add(rowList);
        when(mockedVenue.getNumSeatsAvailable()).thenReturn(0);
        doNothing().when(mockedVenue).setNumSeatsAvailable(anyInt());
        seatHold.bookOrReleaseTickets(heldTickets, ReserveOrRelease.EXPIRED, "email", rowSpaceMap, continuousSpaceMap, mockedVenue);
        assertFalse(heldTickets.containsKey(1));
        TreeMap<Integer, List<Integer>> actualContinuousSpaceMap = DummyObjects.returnContinuousSpace();
        actualContinuousSpaceMap.get(4).remove(0);
        rowList = new ArrayList<>();
        rowList.add(0);
        actualContinuousSpaceMap.put(6,rowList);
        assertEquals(actualContinuousSpaceMap,continuousSpaceMap);
        Map<Integer, List<List<Integer>>> actualRowSpaceMap = DummyObjects.returnRowSpace();
        rowBlocks = actualRowSpaceMap.get(0);
        rowBlocks.get(0).set(1,5);
        assertEquals(rowSpaceMap,actualRowSpaceMap);
    }
}
