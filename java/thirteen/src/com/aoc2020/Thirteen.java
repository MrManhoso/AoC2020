package com.aoc2020;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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

    public static Set<Integer> getPrimeFactors(int n){
        Set<Integer> ret = new HashSet<Integer>();
        while(n%2 == 0){
            ret.add(2);
            n /= 2;
        }
        for(int i = 3; i <= Math.sqrt(n); i = i+2){
            while(n%i == 0){
                ret.add(i);
                n /= i;
            }
        }
        if(n > 2){
            ret.add(n);
        }
        return ret;
    }

    // eulers fi function
    public static int fi(int n){
        Set<Integer> primes = getPrimeFactors(n);
        return primes.stream().reduce(n, (curr, p) -> curr-(curr/p));
    }

    public static int calculateB(long bigN, int n){
        int b = (int) (Math.pow(bigN/n, fi(n)-1) % n);
        return b;
    }

    public static long getMinX(long x, long bigN){
        while(x >= bigN) x -= bigN;
        return x;
    }

    // ai = rest of t mod ni, N = n1 * n2 * ... * ni
    public static long sumPart(int a, int b, long bigN, int n){
        long c = bigN/n;
        return a*b*c;
    }

    // "Kinesiska restklassatsen"
    // x = sum(ai*bi(N/ni)
    // ai = rest of t mod ni which in this case also is ni-x where x is position in array of departures
    public long getEarliestTimestamp(int[] departures){
        long bigN = Arrays.stream(departures)
                .filter(i -> i > -1)
//                .skip(1)
                .mapToLong(x -> x)
                .reduce(1L, Math::multiplyExact);
        long x = IntStream.range(0,departures.length)
                .filter(i -> departures[i] != -1)
                .mapToObj(i -> new int[]{departures[i]-i, departures[i]})
                .reduce(0L, (tot, d) -> tot + sumPart(d[0], calculateB(bigN, d[1]), bigN, d[1]), Long::sum);
        return getMinX(x, bigN);
    }

    // What is the earliest timestamp such that all of the listed bus IDs depart at offsets matching their positions in the list?
    // use "kinesiska restklassatsen"? We at least know departures are coprime (I checked)
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
