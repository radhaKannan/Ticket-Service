package com.walmart.app;

import java.util.*;

/*
Venue is a singleton class and holds the number of seats available at any given point in time.
During object creation, number of rows and columns is set which is used to calculate the number of seats available.
 */
public class Venue {
    private static Venue venueInstance = null;
    private int numRows;
    private int numColumns;
    private int numSeatsAvailable;
    private Map<Integer,SeatHold> heldTickets;
    private Map<Integer, List<List<Integer>>> rowSpaceMap;
    private TreeMap<Integer, List<Integer>> continuousSpaceMap;

    private Venue(int numRows, int numColumns) {
        this.numRows = numRows;
        this.numColumns = numColumns;
        this.heldTickets = new HashMap<>();
        this.rowSpaceMap = initializeRowSpace();
        this.numSeatsAvailable = numRows * numColumns;
        this.continuousSpaceMap = initializeContinuousSpace();
    }

    public static Venue getInstance(int numRows, int numColumns) {
        if (venueInstance == null)
            venueInstance = new Venue(numRows, numColumns);
        return venueInstance;
    }

    public int getNumRows() {
        return numRows;
    }

    public int getNumColumns() {
        return numColumns;
    }

    public int getNumSeatsAvailable() {
        return numSeatsAvailable;
    }

    public void setNumSeatsAvailable(int numSeatsAvailable) {
        this.numSeatsAvailable = numSeatsAvailable;
    }

    public Map<Integer, SeatHold> getHeldTickets() {
        return heldTickets;
    }

    public Map<Integer, List<List<Integer>>> getRowSpaceMap() {
        return rowSpaceMap;
    }

    public TreeMap<Integer, List<Integer>> getContinuousSpaceMap() {
        return continuousSpaceMap;
    }

    /*
        Fills the rowSpaceMap. If 2 rows and 4 columns present, it would look like:
        {
            0: [[0 3]]
            1: [[0 3]]
        }
        The keys are the different rows and the value is the list of continuous spaces available in that row.
         */
    private HashMap<Integer, List<List<Integer>>> initializeRowSpace() {
        HashMap<Integer, List<List<Integer>>> rowSpace = new HashMap<>();
        for (int row = 0; row < this.getNumRows(); row++) {
            List<Integer> initialSpace = new ArrayList<>();
            initialSpace.add(0);
            initialSpace.add(this.getNumColumns()-1);
            List<List<Integer>> rowSpaceBlocks = new ArrayList<>();
            rowSpaceBlocks.add(initialSpace);
            rowSpace.put(row, rowSpaceBlocks);
        }
        return rowSpace;
    }

    /*
    Fills the continuous space map. If 2 rows and 4 columns:
    {
        4: [0 1]
    }
    The key indicates the continuous space available in the venue and
    the value says in which all rows that space is available.
     */
    private TreeMap<Integer, List<Integer>> initializeContinuousSpace() {
        TreeMap<Integer, List<Integer>> continuousSpace = new TreeMap<>();
        List<Integer> rows = new ArrayList<>();
        for(int row = 0; row < this.getNumRows(); row++)
            rows.add(row);
        continuousSpace.put(this.getNumColumns(),rows);
        return continuousSpace;
    }
}
