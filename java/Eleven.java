package eleven;

import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.stream.IntStream;

interface NewSeatState{
    char newSeatStateFunc(String adjPrevSeats, String adjCurrSeats, String adjNextSeats);
}

public class Eleven {

    private static String _floorString;

    private static ArrayList<String> readFile(String filePath){
        ArrayList<String> ret =  new ArrayList<>();
        try{
            File file = new File(filePath);
            Scanner scanner = new Scanner(file);
            while(scanner.hasNextLine()){
                ret.add(scanner.nextLine());
            }
        } catch(FileNotFoundException ex){
            System.out.println("File not found");
        }
        return ret;
    }

    private String getAdjacentSeats(int seatIndex, String row){
        char first = seatIndex > 0 ? row.charAt(seatIndex-1) : '.';
        char third = seatIndex < row.length()-1 ? row.charAt(seatIndex+1) : '.';
        return String.valueOf(first) + row.substring(seatIndex, seatIndex + 1) + String.valueOf(third);
    }

    private long countOccupiedSeats(String seats){
        return seats.chars().filter(c -> c == '#').count();
    }

    private boolean noOccupiedSeats(String seats){
        return countOccupiedSeats(seats) == 0;
    }

    private char newStateForSeat(int seatIndex, int row, ArrayList<String> seating, NewSeatState func){
        String prevRow = row > 0 ? seating.get(row-1) : _floorString;
        String nextRow = row < seating.size() - 1 ? seating.get(row+1) : _floorString;
        String currRow = seating.get(row);
        String adjPrevRowSeats = getAdjacentSeats(seatIndex, prevRow);
        String adjNextRowSeats = getAdjacentSeats(seatIndex, nextRow);
        String adjCurrRowSeats = getAdjacentSeats(seatIndex, currRow);
        adjCurrRowSeats = adjCurrRowSeats.substring(0, 1) + adjCurrRowSeats.substring(2);
        // System.out.println(adjPrevRowSeats + " " + adjCurrRowSeats + " "+ adjNextRowSeats + " ");
        return func.newSeatStateFunc(adjPrevRowSeats, adjCurrRowSeats, adjNextRowSeats);
    }

    // If a seat is empty (L) and there are no occupied seats adjacent to it, the seat becomes occupied.
    private char newStateForEmptySeat(int seatIndex, int row, ArrayList<String> seating) { 
        return newStateForSeat(seatIndex, row, seating, (prev, curr, next) -> {
            return noOccupiedSeats(prev) && noOccupiedSeats(curr) && noOccupiedSeats(next) ? '#' : 'L';
        });
    }
    
    // If a seat is occupied (#) and four or more seats adjacent to it are also occupied, the seat becomes empty.
    private char newStateForOccupiedSeat(int seatIndex, int row, ArrayList<String> seating){
        return newStateForSeat(seatIndex, row, seating, (prev, curr, next) -> {
            return countOccupiedSeats(prev) + countOccupiedSeats(curr) + countOccupiedSeats(next) > 3 ? 'L' : '#';
        });
    }

    private char newSeatState(int i, char c, ArrayList<String> seating, int row){
        if(c == '.') return c;
        if(c == 'L') return newStateForEmptySeat(i, row, seating);
        return newStateForOccupiedSeat(i, row, seating);
    }
    
    // Otherwise, the seat's state does not change.
    private String updateRow(int row, ArrayList<String> seating){
        String seatRow = seating.get(row);
        return IntStream
            .range(0, seatRow.length())
            .map(i -> newSeatState(i, seatRow.charAt(i), seating, row))
            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
            .toString();
    }

    private boolean changesMade(ArrayList<String> currentSeating, ArrayList<String> newSeating) {
        boolean changed = false;
        for(int i = 0; i < currentSeating.size(); i++) {
            String row = updateRow(i, currentSeating);
            if(!row.equals(currentSeating.get(i))){
                changed = true;
            }
            newSeating.add(row);
        }
        return changed;
    }

    private long nrSeatsOccupied(ArrayList<String> seats){
        // System.out.println(seats);
        return seats.stream().reduce(0L, (curr, row) -> curr + row.chars().filter(c -> c == '#').count(), (a1, a2) -> a1 + a2);        
    }

    // 
    // Correct answer for test is 37 (5 rounds before stable)
    private void part1(ArrayList<String> seats){
        ArrayList<String> newSeating = new ArrayList<>(); 
        while(changesMade(seats, newSeating)){
            seats = (ArrayList<String>)newSeating.clone();
            newSeating.clear();   
        }
        System.out.println("Part 1: " + nrSeatsOccupied(newSeating));
    }

    public static void main(String[] args){
        ArrayList<String> input = Eleven.readFile("waiting_area_seats.txt");
        Eleven._floorString = new String(new char[input.get(0).length()]).replace("\0", ".");
        Eleven eleven = new Eleven();
        // OBS part1 prob changes input so we need to read it again if needed in part2
        eleven.part1(input);
    }    
}
