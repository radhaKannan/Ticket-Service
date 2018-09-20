package com.walmart.app;

import java.util.*;

public final class Utilities {
    public static HashMap<Integer, List<List<Integer>>> initializeRowSpace(Venue venue) {
        HashMap<Integer, List<List<Integer>>> rowSpace = new HashMap<>();
        for (int row = 0; row < venue.getNumRows(); row++) {
            List<Integer> initialSpace = new ArrayList<>();
            initialSpace.add(0);
            initialSpace.add(venue.getNumColumns()-1);
            List<List<Integer>> rowSpaceBlocks = new ArrayList<>();
            rowSpaceBlocks.add(initialSpace);
            rowSpace.put(row, rowSpaceBlocks);
        }
        return rowSpace;
    }

    public static TreeMap<Integer, List<Integer>> initializeContinuousSpace(Venue venue) {
        TreeMap<Integer, List<Integer>> continuousSpace = new TreeMap<>();
        List<Integer> rows = new ArrayList<>();
        for(int row = 0; row < venue.getNumRows(); row++)
            rows.add(row);
        continuousSpace.put(venue.getNumColumns(),rows);
        return continuousSpace;
    }

    public static SeatsBlock assignSeatBlocks(Map<Integer, List<Integer>> continuousSpaceMap, int numSeats, Map<Integer, List<List<Integer>>> rowSpaceMap) {
        SeatsBlock seatsBlock = null;
        int row = continuousSpaceMap.get(numSeats).remove(0);
        if (continuousSpaceMap.get(numSeats).isEmpty())
            continuousSpaceMap.remove(numSeats);
        List<List<Integer>> rowBlock = rowSpaceMap.get(row);
        for (List<Integer> block : rowBlock)
            if(block.get(1)-block.get(0)+1 == numSeats) {
                seatsBlock = new SeatsBlock(row, block.get(0), block.get(1));
                rowBlock.remove(block);
                break;
            }
        return seatsBlock;
    }

    public static SeatsBlock assignRemainingSeatBlock(TreeMap<Integer, List<Integer>> continuousSpaceMap, int numSeats, Map<Integer, List<List<Integer>>> rowSpaceMap) {
        SeatsBlock seatsBlock = null;
        int availableSpace = continuousSpaceMap.lastKey();
        int row = continuousSpaceMap.get(availableSpace).remove(0);
        if (continuousSpaceMap.get(availableSpace).isEmpty())
            continuousSpaceMap.remove(availableSpace);
        int newAvailableForThatBlock = availableSpace - numSeats;
        List<Integer> newRow = new ArrayList<>();
        if (continuousSpaceMap.containsKey(newAvailableForThatBlock))
            newRow = continuousSpaceMap.get(newAvailableForThatBlock);
        newRow.add(row);
        continuousSpaceMap.put(newAvailableForThatBlock, newRow);
        List<List<Integer>> rowBlock = rowSpaceMap.get(row);
        for (List<Integer> block : rowBlock)
            if(block.get(1)-block.get(0)+1 == availableSpace) {
                seatsBlock = new SeatsBlock(row, block.get(0), block.get(0)+numSeats-1);
                block.set(0,block.get(0)+numSeats);
                break;
            }
        return seatsBlock;
    }

    public static Integer mergeLeftRowMap(List<List<Integer>> rowSeatBlocks, int index, TreeMap<Integer, List<Integer>> continuousSpaceMap, SeatsBlock seatsBlock) {
        if (index == 0 || index >= rowSeatBlocks.size())
            return 0;
        int newContinuousSpace = 0;
        int oldContinuousSpace;
        int indexSpace;
        if (rowSeatBlocks.get(index).get(0) == rowSeatBlocks.get(index-1).get(1)+1) {
            newContinuousSpace = rowSeatBlocks.get(index).get(1) - rowSeatBlocks.get(index-1).get(0) + 1;
            oldContinuousSpace = rowSeatBlocks.get(index-1).get(1) - rowSeatBlocks.get(index-1).get(0) + 1;
            indexSpace = rowSeatBlocks.get(index).get(1) - rowSeatBlocks.get(index).get(0) + 1;
            rowSeatBlocks.get(index-1).set(1,rowSeatBlocks.get(index).get(1));
            rowSeatBlocks.remove(index);
            continuousSpaceMap.get(oldContinuousSpace).remove((Integer) seatsBlock.getRow());
            continuousSpaceMap.get(indexSpace).remove((Integer) seatsBlock.getRow());
            if (continuousSpaceMap.get(oldContinuousSpace).isEmpty())
                continuousSpaceMap.remove(oldContinuousSpace);
            if (continuousSpaceMap.containsKey(indexSpace) && continuousSpaceMap.get(indexSpace).isEmpty())
                continuousSpaceMap.remove(indexSpace);
        }
        return newContinuousSpace;
    }
}
