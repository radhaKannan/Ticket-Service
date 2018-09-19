package com.walmart.app;

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
