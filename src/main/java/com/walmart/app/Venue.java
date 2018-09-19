package com.walmart.app;

public class Venue {
    private static Venue venueInstance = null;
    private int numRows;
    private int numColumns;
    private int numSeatsAvailable;

    private Venue(int numRows, int numColumns) {
        this.numRows = numRows;
        this.numColumns = numColumns;
        this.numSeatsAvailable = numRows * numColumns;
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
}