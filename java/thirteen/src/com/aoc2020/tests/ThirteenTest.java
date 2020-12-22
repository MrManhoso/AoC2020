package com.aoc2020.tests;

import com.aoc2020.Thirteen;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
//import static org.hamcrest.CoreMatchers.*;
//import static org.junit.matchers.JUnitMatchers.*;

class ThirteenTest {
    // Prime factorisation tests
    @Test
    public  void primeFactorsOf10Are2And5(){
        testPrimeFactors(10, new int[]{2,5});
    }
    @Test
    public  void primeFactorsOf7Are7(){
        testPrimeFactors(7, new int[]{7});
    }
    @Test
    public  void primeFactorsOf8Are2(){
        testPrimeFactors(8, new int[]{2});
    }
    @Test
    public  void primeFactorsOf15Are3And5(){
        testPrimeFactors(15, new int[]{3,5});
    }

    // Fi calculation tests
    @Test
    public void fiOf10Is4(){
        testFi(10, 4);
    }
    @Test
    public void fiOf3Is2(){
        testFi(3, 2);
    }
    @Test
    public void fiOf7Is6(){
        testFi(7, 6);
    }
    @Test
    public void fiOf67Is66(){
        testFi(67, 66);
    }
    @Test
    public void fiOf59Is58(){
        testFi(59, 58);
    }
    @Test
    public void fiOf61Is60(){
        testFi(61, 60);
    }

    // CalculateB tests
    @Test
    public void bOf210And3Is1(){
        assertEquals(1, Thirteen.calculateB(210, 3));
    }
    @Test
    public void bOf210And7Is4(){
        assertEquals(4, Thirteen.calculateB(210, 7));
    }
    @Test
    public void bOf210And10Is1(){
        assertEquals(1, Thirteen.calculateB(210, 10));
    }

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

    private void testPrimeFactors(int n, int[] expected){
        Set<Integer> ps = Thirteen.getPrimeFactors(n);
        assertEquals(expected.length, ps.size());
        Arrays.stream(expected).forEach(p -> assertTrue(ps.contains(p)));
    }

    private void testFi(int n, int expected){
        assertEquals(expected, Thirteen.fi(n));
    }
}