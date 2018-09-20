package com.walmart.app;

import com.walmart.app.mockData.DummyObjects;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class UtilitiesTest {
    Venue mockedVenue = mock(Venue.class);

    @Test
    public void initializeRowSpace() {
        when(mockedVenue.getNumRows()).thenReturn(2);
        when(mockedVenue.getNumColumns()).thenReturn(4);
        assertEquals(Utilities.initializeRowSpace(mockedVenue), DummyObjects.returnRowSpace());
    }

    @Test
    public void initializeContinuousSpace() {
        when(mockedVenue.getNumRows()).thenReturn(2);
        when(mockedVenue.getNumColumns()).thenReturn(4);
        assertEquals(Utilities.initializeContinuousSpace(mockedVenue), DummyObjects.returnContinuousSpace());
    }

    @Test
    public void assignSeatBlock() {
        Map<Integer, List<Integer>> expectedContinuousSpaceMap = DummyObjects.returnContinuousSpace();
        Map<Integer, List<List<Integer>>> expectedRowSpaceMap = DummyObjects.returnRowSpace();
        SeatsBlock seatsBlock = Utilities.assignSeatBlocks(expectedContinuousSpaceMap, 4, expectedRowSpaceMap);
        assertEquals(seatsBlock.getRow(), 0);
        assertEquals(seatsBlock.getStart(), 0);
        assertEquals(seatsBlock.getEnd(), 3);
        Map<Integer, List<Integer>> actualContinuousMap = DummyObjects.returnContinuousSpace();
        Map<Integer, List<List<Integer>>> actualRowMap = DummyObjects.returnRowSpace();
        int row = actualContinuousMap.get(4).remove(0);
        actualRowMap.get(row).remove(0);
        assertEquals(expectedContinuousSpaceMap, actualContinuousMap);
        assertEquals(expectedRowSpaceMap, actualRowMap);
    }

    @Test
    public void assignPartialSeatBlock() {
        TreeMap<Integer, List<Integer>> expectedContinuousSpaceMap = DummyObjects.returnContinuousSpace();
        Map<Integer, List<List<Integer>>> expectedRowSpaceMap = DummyObjects.returnRowSpace();
        SeatsBlock seatsBlock = Utilities.assignRemainingSeatBlock(expectedContinuousSpaceMap, 2, expectedRowSpaceMap);
        assertEquals(seatsBlock.getRow(), 0);
        assertEquals(seatsBlock.getStart(), 0);
        assertEquals(seatsBlock.getEnd(), 1);
        Map<Integer, List<Integer>> actualContinuousMap = DummyObjects.returnContinuousSpace();
        Map<Integer, List<List<Integer>>> actualRowMap = DummyObjects.returnRowSpace();
        int row = actualContinuousMap.get(4).remove(0);
        List<Integer> rowList = new ArrayList<>();
        rowList.add(row);
        actualContinuousMap.put(2,rowList);
        List<Integer> rowBlock = actualRowMap.get(0).get(0);
        rowBlock.set(0,2);
        assertEquals(expectedContinuousSpaceMap, actualContinuousMap);
        assertEquals(expectedRowSpaceMap, actualRowMap);
    }

    @Test
    public void mergeTwoContinuousBlocks() {
        List<List<Integer>> expectedRowSeatBlocks = DummyObjects.returnRowSpace().get(0);
        expectedRowSeatBlocks.get(0).set(1,1);
        List<Integer> rowList = new ArrayList<>();
        rowList.add(2);
        rowList.add(3);
        expectedRowSeatBlocks.add(rowList);
        TreeMap<Integer, List<Integer>> expectedContinuousSpaceMap = DummyObjects.returnContinuousSpace();
        expectedContinuousSpaceMap.get(4).remove(0);
        rowList = new ArrayList<>();
        rowList.add(0);
        expectedContinuousSpaceMap.put(2,rowList);
        SeatsBlock seatsBlock = new SeatsBlock(0,2,3);
        assertEquals((long)Utilities.mergeLeftRowMap(expectedRowSeatBlocks,1,expectedContinuousSpaceMap,seatsBlock),4);
        List<List<Integer>> actualRowSeatBlocks = DummyObjects.returnRowSpace().get(0);
        TreeMap<Integer, List<Integer>> actualContinuousSpaceMap = DummyObjects.returnContinuousSpace();
        assertEquals(expectedRowSeatBlocks, actualRowSeatBlocks);
    }

    @Test
    public void mergeTwoContinuousBlocksNotPossible() {
        List<List<Integer>> expectedRowSeatBlocks = DummyObjects.returnRowSpace().get(0);
        expectedRowSeatBlocks.get(0).set(1,1);
        TreeMap<Integer, List<Integer>> expectedContinuousSpaceMap = DummyObjects.returnContinuousSpace();
        expectedContinuousSpaceMap.get(4).remove(0);
        List<Integer> rowList = new ArrayList<>();
        rowList.add(0);
        expectedContinuousSpaceMap.put(2,rowList);
        SeatsBlock seatsBlock = new SeatsBlock(0,0,1);
        assertEquals((long)Utilities.mergeLeftRowMap(expectedRowSeatBlocks,0,expectedContinuousSpaceMap,seatsBlock),0);
        assertEquals((long)Utilities.mergeLeftRowMap(expectedRowSeatBlocks,1,expectedContinuousSpaceMap,seatsBlock),0);
    }
}
