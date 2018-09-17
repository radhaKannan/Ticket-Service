package com.walmart.app;

import java.util.Arrays;

public class Venue {
    private static Venue venueInstance = null;
    private int numRows;
    private int numColumns;
    private States[][] seatInfo;
    private int numSeatsAvailable;

    private Venue(int numRows, int numColumns) {
        this.numRows = numRows;
        this.numColumns = numColumns;
        this.numSeatsAvailable = numRows * numColumns;
        this.seatInfo = new States[numRows][numColumns];
        for (States[] row : seatInfo) {
            Arrays.fill(row, States.AVAILABLE);
        }
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

    public States[][] getSeatInfo() {
        return seatInfo.clone();
    }

    public void setSeatInfo(States[][] seatInfo) {
        this.seatInfo = seatInfo;
    }

    public int getNumSeatsAvailable() {
        return numSeatsAvailable;
    }

    public void setNumSeatsAvailable(int numSeatsAvailable) {
        this.numSeatsAvailable = numSeatsAvailable;
    }
}