package com.walmart.app.mockData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public final class DummyObjects {
    public static HashMap<Integer, List<List<Integer>>> returnRowSpace() {
        HashMap<Integer, List<List<Integer>>> rowSpace = new HashMap<>();
        int i = 0;
        while (i < 2) {
            List<Integer> block = new ArrayList<>();
            block.add(0);
            block.add(3);
            List<List<Integer>> rowBlocks = new ArrayList<>();
            rowBlocks.add(block);
            rowSpace.put(i, rowBlocks);
            ++i;
        }
        return rowSpace;
    }

    public static TreeMap<Integer, List<Integer>> returnContinuousSpace() {
        List<Integer> rowList = new ArrayList<>();
        rowList.add(0);
        rowList.add(1);
        TreeMap<Integer, List<Integer>> continuousSpace = new TreeMap<>();
        continuousSpace.put(4,rowList);
        return continuousSpace;
    }
}
