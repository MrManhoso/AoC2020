package com.aoc2020;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.IntStream;

public class Thirteen {
    public static String[] readFile(String filePath){
        try {
            File file = new File(filePath);
            Scanner s = new Scanner(file);
            ArrayList<String> lines = new ArrayList<>();
            while(s.hasNextLine()){
                lines.add(s.nextLine());
            }
            s.close();
            return lines.toArray(String[]::new);
        } catch(FileNotFoundException ex){
            System.out.println("File not found");
            return null;
        }
    }

    // [0] = bus id, [1] = wait time
    private int[] minWaitTime(int[] first, int[] second){
        return first[1] < second[1] ? first : second;
    }

    private int getWaitTime(int earliestDeparture, int interval){
        int remainder = earliestDeparture % interval;
        return remainder == 0 ? remainder : interval - remainder;
    }

    private int[] getShortestWaitTimeAndBusId(int earliestDeparture, int[] availableIntervals) {
        return Arrays
                .stream(availableIntervals)
                .filter(x -> x >= 0)
                .mapToObj(i -> new int[]{i, getWaitTime(earliestDeparture, i)})
                .reduce(new int[]{availableIntervals[0], getWaitTime(earliestDeparture, availableIntervals[0])}, this::minWaitTime);
    }

    // What is the ID of the earliest bus you can take to the airport multiplied by the number of minutes you'll need to wait for that bus?
    // correct answer is 246 (295 for test)
    private void part1(int earliestDeparture, int[] availableIntervals){
        int[] idAndWaitTime = getShortestWaitTimeAndBusId(earliestDeparture, availableIntervals);
        System.out.println("Part 1: " + String.valueOf(idAndWaitTime[0] * idAndWaitTime[1]));
    }

    //-------------------------------------------------------------

    // a1: remainder, n1 departure,
    // returns: first is result, second is next modulo
    public long[] sieve(long a1, long n1, long n2, long congruentTo){
        int i = 0;
        while((a1 + (n1 * i)) % n2 != congruentTo) ++i;
        return new long[]{a1 + (n1 * i), n1*n2};
    }

    // chineese remainder theorem using sieve (quadratic->slow)
    public long getEarliestTimestamp(int[] departures){
        long[][] x = IntStream.range(0,departures.length)
                .filter(i -> departures[i] != -1)
                .mapToObj(i -> new long[]{departures[i]-i, departures[i]})// first is remainder, second is departure time
                .sorted((i1, i2) -> i1[1] < i2[1] ? -1 : i1[1] == i2[1] ? 0 : 1)
                .toArray(n -> new long[n][2]);
        return Arrays.stream(x)
                .skip(1)
                .reduce(x[0], (b, d) -> sieve(b[0], b[1], d[1], d[0] % d[1]))[0];

    }

    // What is the earliest timestamp such that all of the listed bus IDs depart at offsets matching their positions in the list?
    // use "chineese remainder theorem"? We at least know departures are coprime (I checked)
    private void part2(int[] departures){
        System.out.println("Part 2: " + getEarliestTimestamp(departures));
    }

    public static void main(String[] args) {
        // Note! input is coprime
        String file = "bus_schedule.txt";
        String[] lines = readFile(file);
        Thirteen thirteen = new Thirteen();
        int earliestDeparture = Integer.parseInt(lines[0]);
        int[] availableIntervals = Arrays.stream(lines[1].split(",")).mapToInt(x -> x.equals("x") ? -1 : Integer.parseInt(x)).toArray();
        thirteen.part1(earliestDeparture, availableIntervals);
        thirteen.part2(availableIntervals);
    }
}
