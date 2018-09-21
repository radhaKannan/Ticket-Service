package com.walmart.app;

/*
Venue is a singleton class and holds the number of seats available at any given point in time.
During object creation, number of rows and columns is set which is used to calculate the number of seats available.
 */
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

    public void setNumSeatsAvailable(int numSeatsAvailable) {
        this.numSeatsAvailable = numSeatsAvailable;
    }
}