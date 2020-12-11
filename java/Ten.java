package ten;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;


public class Ten{
    private class Pair{
        public int joltage;
        public long sum;
    
        public Pair(int joltage, long sum){
            this.joltage = joltage;
            this.sum = sum;
        }
    
        public String toString(){
            return "Joltage: " + joltage + " Sum: " + sum;
        }
    }

    private void print(ArrayList<Integer> lst){
        for(int item : lst){
            System.out.println(item);
        }
    }

    private ArrayList<Integer> readFile(String filePath){
        ArrayList<Integer> ret = new ArrayList<Integer>();
        try{
            File file = new File(filePath);
            Scanner scanner = new Scanner(file);
            while(scanner.hasNextLine()){
                ret.add(Integer.parseInt(scanner.nextLine()));
            }
        } catch(FileNotFoundException ex){
            System.out.println("File not found");
        }
        return ret;
    }

    // First is 1-diff, second is 3-diff
    private int[] calculateNrDiffs(ArrayList<Integer> joltage){
        int[] ret = {0,0};
        int prev = 0; // outlet
        for(int i : joltage){
            if(i - prev == 1) {
                ret[0] += 1;
            }
            else if(i - prev == 3) {
                ret[1] += 1;
            }
            prev = i;
        }
        return ret;
    }

    private long getSum(int joltage, Pair p){
        return (joltage - p.joltage < 4) ? p.sum : 0;
    }

    private long calulateSum(int joltage, Pair p1, Pair p2, Pair p3){
        return getSum(joltage, p1) + getSum(joltage, p2) + getSum(joltage, p3);
    }

    // Part 2: What is the total number of distinct ways you can arrange the adapters to connect the charging outlet to your device?
    // Correct answer for test is 19208
    private void part2(ArrayList<Integer> adapters) {
        ArrayList<Pair> sums = new ArrayList<Pair>();
        sums.add(new Pair(0, 1));
        sums.add(new Pair(adapters.get(0), 1));
        sums.add(new Pair(adapters.get(1), 1 + getSum(adapters.get(1), sums.get(0))));
        for(int i = 2; i < adapters.size(); i++) {
            long sum = calulateSum(adapters.get(i), sums.get(i), sums.get(i-1), sums.get(i-2));
            sums.add(new Pair(adapters.get(i), sum));
            // System.out.println("Index: " + i + " Last joltage: " + sums.get(i+1).joltage + " Last sum:" + sums.get(i+1).sum);
        }
        System.out.println("Part 2: " + sums.get(sums.size()-1).sum);
    }


    // Part 1: What is the number of 1-jolt differences multiplied by the number of 3-jolt differences?
    // For test case there are 22 differences of 1 jolt and 10 differences of 3 jolts => 220
    // Correct answer is 2272
    private void part1(ArrayList<Integer> adapters) {
        int[] ret = calculateNrDiffs(adapters);
        System.out.println("Part 1: " + ret[0] * ret[1]);
    }

    public static void main(String[] args) {
        Ten ten = new Ten();
        ArrayList<Integer> input = ten.readFile("adapters.txt");
        Collections.sort(input);
        input.add(input.get(input.size()-1) + 3); // device adapter
        ten.part1(input);
        ten.part2(input);
    }
}