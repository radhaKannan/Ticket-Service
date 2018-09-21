package com.walmart.app;

/*
Represents a continuous seatBlock in a row.
Indicates in which row it is present and
begins from seatNumber start to seatNumber end (both included).
In case of 1 seat, start and end would be same.
 */
public class SeatsBlock {
    private int row;
    private int start;
    private int end;

    public SeatsBlock(int row, int start, int end) {
        this.row = row;
        this.start = start;
        this.end = end;
    }

    public int getRow() {
        return row;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }
}
