package com.aoc2020.tests;

import com.aoc2020.Thirteen;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ThirteenTest {

    @Test
    public void earliestTimestampFor67_7_59_61_Is_754018(){
        testEarliestTimestamp(754018L, new int[]{67,7,59,61});
    }

    @Test
    public void earliestTimestampFor17_x_13_19_Is_3417(){
        testEarliestTimestamp(3417L, new int[]{17,-1,13,19});
    }

    private void testEarliestTimestamp(long expected, int[] departures){
        Thirteen thirteen = new Thirteen();
        var timestamp = thirteen.getEarliestTimestamp(departures);
        assertEquals(expected, timestamp);
    }
}