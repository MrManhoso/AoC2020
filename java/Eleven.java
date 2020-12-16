package eleven;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Arrays;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.Collectors;
import java.util.Comparator;

interface NewSeatState{
    char newSeatStateFunc(String adjPrevSeats, String adjCurrSeats, String adjNextSeats);
}

public class Eleven {
    private class Pair<T1, T2>{
        public T1 first;
        public T2 second;

        public Pair(T1 first, T2 second){
            this.first = first;
            this.second = second;
        }

        public T1 getFirst(){
            return first;
        }

        private String intArrayToString(){
            return Arrays.stream((int[])second)
                .mapToObj(i -> String.valueOf(i))
                .reduce("[", (curr,str) -> curr + " " + str) + "]";
        }

        public String toString() { 
            String s = (second instanceof int[]) ? intArrayToString() : second.toString(); 
            return first.toString() + ":" +  s;
        }
    }

    private class Seat{
        private Pair<Integer, Character> _pair;

        public int pos(){
            return _pair.first;
        }

        public char state(){
            return _pair.second;
        }

        public char state(char newState){
            _pair.second = newState;
            return _pair.second;
        }

        public Seat(int pos, char state){
            _pair = new Pair(pos, state);
        }

        public String toString(){ return _pair.toString(); }
    }

    private static String _floorString;
    private static int _rowSize;
    private static int _nrRows;
    private static final int NR_RELATIONS = 8;
    private static final int REL_NOT_SET = -2;
    private static final int REL_NOT_FOUND = -1;


    private String print(Map<Integer, Pair<Character, int[]>> seatRelations){
        List<Pair<Integer, Character>> seats = seatRelations.entrySet().stream()
            .map(e -> new Pair<Integer, Character>(e.getKey(), e.getValue().first))
            .sorted(Comparator.comparing(Pair<Integer, Character>::getFirst))
            .collect(Collectors.toList());//n -> new Pair<Integer, Character>[n]);
        int prev = -1;
        String str = "";
        int count = 0;
        for(Pair<Integer, Character> s : seats){
            while(prev + 1 != s.first){
                str += ".";
                if(++count % _rowSize == 0) str += "\n";
                ++prev;
            }
            str += s.second;
            if(++count % _rowSize == 0) str += "\n";
            prev = s.first;
        }
        return str;
    }

    private static int[] getNewSeatRelations(){
        return new int[]{ REL_NOT_SET, REL_NOT_SET, REL_NOT_SET, REL_NOT_SET, REL_NOT_SET, REL_NOT_SET, REL_NOT_SET, REL_NOT_SET };
    }

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

    private static int insertChars(char[] chars, String vals, int pos){
        for(int i = 0; i < vals.length(); ++i){
            chars[i+pos] = vals.charAt(i);
        }
        return pos + vals.length();
    }

    private static char[] readFile2(String filePath){
        try{
            File file = new File(filePath);
            Scanner scanner = new Scanner(file);
            String line = scanner.nextLine();
            char[] ret =  new char[_rowSize * _nrRows];
            int index = insertChars(ret, line, 0);
            while(scanner.hasNextLine()){
                index = insertChars(ret, scanner.nextLine(), index);
            }
            return ret;
        } catch(FileNotFoundException ex){
            System.out.println("File not found");
        }
        return null;
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

    private long nrSeatsOccupied(ArrayList<String> seats) {
        return seats.stream().reduce(0L, (curr, row) -> curr + row.chars().filter(c -> c == '#').count(), (a1, a2) -> a1 + a2);        
    }

    // 
    // Correct answer for test is 37 (5 rounds before stable)
    private void part1(ArrayList<String> seats) {
        ArrayList<String> newSeating = new ArrayList<>(); 
        while(changesMade(seats, newSeating)){
            seats = (ArrayList<String>)newSeating.clone();
            newSeating.clear();   
        }
        System.out.println("Part 1: " + nrSeatsOccupied(newSeating));
    }

    //-------------------------------------------------------------------------------------------------------------------------// 
    //-------------------------------------------------------------------------------------------------------------------------//

    private boolean outOfBounds(int row, int col){
        return row < 0 || col < 0 || row > _nrRows-1 || col > _rowSize-1;
    }

    private int getOneDim(int row, int col){
        return (row * _rowSize) + col;
    }

    private Seat seatFromOffset(int oneDimPos, int rowOffset, int colOffset, char[] seats){
        int row = (oneDimPos / _rowSize) + rowOffset; // i
        int col = (oneDimPos % _rowSize) + colOffset; // j
        if(outOfBounds(row, col)) return new Seat(REL_NOT_FOUND, '.');
        int pos = getOneDim(row, col);
        return new Seat(pos, seats[pos]); 
    }

    private Seat getNearestSeat(int fromPos, char[] seats, int direction) {
        switch(direction){
        case 0: // up-left, i-1, j-1
            return seatFromOffset(fromPos, -1, -1, seats);
        case 1: // up, i-1, j
            return seatFromOffset(fromPos, -1, 0, seats);
        case 2: // up-right, i-1, j+1
            return seatFromOffset(fromPos, -1, 1, seats);
        case 3: // left, i, j-1
            return seatFromOffset(fromPos, 0, -1, seats);
        case 4: // right, i, j+1
            return seatFromOffset(fromPos, 0, 1, seats);
        case 5: // down-left, i+1, j-1
            return seatFromOffset(fromPos, 1, -1, seats);
        case 6: // down, i+1, j
            return seatFromOffset(fromPos, 1, 0, seats);
        case 7: // down-right, i+1, j+1
            return seatFromOffset(fromPos, 1, 1, seats);
        default: break;
        }
        return new Seat(REL_NOT_FOUND, '.');    
    }

    private int getDirectionInverse(int direction){
        switch(direction){
        case 0: return 7;
        case 1: return 6;
        case 2: return 5;
        case 3: return 4;
        case 4: return 3;
        case 5: return 2;
        case 6: return 1;
        case 7: return 0;
        default: break;
        }
        // throw
        return -100;    
    }

    private Seat getRelationToSeat(int seatPos, char[] seats, int direction){
        Seat nearestSeatInDirection = getNearestSeat(seatPos, seats, direction);
        if(nearestSeatInDirection.pos() == REL_NOT_FOUND || seats[nearestSeatInDirection.pos()] != '.'){
            return nearestSeatInDirection;
        }
        return getRelationToSeat(nearestSeatInDirection.pos(), seats, direction);
         
    }

    private int[] getRelationsForSeat(Seat seat, char[] seats, Map<Integer, Pair<Character, int[]>> seatRelations){
        int[] relationsForSeat = seatRelations.get(seat.pos()).second;
        for(int i = 0; i < NR_RELATIONS; ++i){
            if(relationsForSeat[i] == REL_NOT_SET){
                Seat relatedSeat = getRelationToSeat(seat.pos(), seats, i);
                seatRelations.get(seat.pos()).second[i] = relatedSeat.pos();
                if(relatedSeat.pos() != REL_NOT_FOUND) {
                    if(!seatRelations.containsKey(relatedSeat.pos())){
                        seatRelations.put(relatedSeat.pos(), new Pair(relatedSeat.state(), getNewSeatRelations()));
                    }
                    seatRelations.get(relatedSeat.pos()).second[getDirectionInverse(i)] = seat.pos();
                }
            }
        }
        return seatRelations.get(seat.pos()).second;
    }

    private int[] mergeSeenSeats(int[] first, int[] second){
        int[] ret = new int[first.length];
        for(int i = 0; i < first.length; ++i){
            ret[i] = first[i] < 0 ? second[i] : first[i];
        }
        return ret;
    }

    private Map<Integer, Pair<Character, int[]>> mergeMaps(Map<Integer, Pair<Character, int[]>> m1, Map<Integer, Pair<Character, int[]>> m2){
        return Stream
            .concat(m1.entrySet().stream(), m2.entrySet().stream())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (p1, p2) -> new Pair(p2.first, mergeSeenSeats(p1.second, p2.second))));
    }

    private Map<Integer, Pair<Character, int[]>> firstSeenSeats(int rowIndex, Map<Integer, Pair<Character, int[]>> foundRelations, char[] seats){
        return IntStream
            .range(0, _rowSize)
            .map(i -> getOneDim(rowIndex, i))
            .filter(i -> seats[i] != '.')
            .mapToObj(i -> new Seat(i, seats[i]))
            .reduce(foundRelations, (curr, seat) -> {
                    if(!curr.containsKey(seat.pos())){
                        curr.put(seat.pos(), new Pair(seat.state(), getNewSeatRelations()));
                    }
                    Pair<Character, int[]> val = curr.get(seat.pos());
                    val.second = getRelationsForSeat(seat, seats, curr);
                    return curr;
                }, (m1, m2) -> mergeMaps(m1, m2));
    }

    private static char[] getRow(int row, char[] seatsInRow){
        return Arrays.copyOfRange(seatsInRow, row * _rowSize, row * _rowSize + _rowSize);
    }

    // x,y coordinates. 
    //          (seat diag up-left), seat up, (seat diag up-right) 
    // seat =   (seat left), this seats value, (seat right)
    //          (seat diag down-left), seat down, (seat diag down-right)
    // 
    // When searching
    //          (i-1,j-1), (i-1,j), (i-1,j+1) 
    // seat =   (i,j-1), (i,j), (i,j+1)
    //          (i+1,j-1), (i+1,j), (i+1,j+1)
    private Map<Integer, Pair<Character, int[]>> getRelationToSeats(char[] seats) {
        Map<Integer, Pair<Character, int[]>> init = new HashMap<Integer, Pair<Character, int[]>>();
        return IntStream
            .range(0, _nrRows)
            .mapToObj(i -> i)
            .reduce(init, (curr, i) -> {
                    Map<Integer, Pair<Character, int[]>> seatRelations = firstSeenSeats(i, curr, seats);
                    return mergeMaps(seatRelations, curr);
                }, (m1, m2) -> mergeMaps(m1, m2));
    }


    // five or more visible occupied seats for an occupied seat to become empty
    private boolean becomesEmpty(int[] relatedSeats, Map<Integer, Pair<Character, int[]>> seatRelations){
        return Arrays.stream(relatedSeats).reduce(0, (curr, index) -> curr + (index >= 0 && seatRelations.get(index).first == '#' ? 1 : 0)) > 4;
    }

    // empty seats that see no occupied seats become occupied, 
    private boolean becomesOccupied(int[] relatedSeats, Map<Integer, Pair<Character, int[]>> seatRelations){
        return Arrays.stream(relatedSeats).reduce(0, (curr, index) -> curr + (index < 0 || seatRelations.get(index).first == 'L' ? 1 : 0)) == 8;
    }

    private Seat updateSeatState(int index, Pair<Character, int[]> seat, Map<Integer, Pair<Character, int[]>> seatRelations){
        if(seat.first == '#' && becomesEmpty(seat.second, seatRelations)) {
            return new Seat(index, 'L');
        }
        if(seat.first == 'L' && becomesOccupied(seat.second, seatRelations)) {
            return new Seat(index, '#');
        }
        return null;
    }

    private Seat[] getUpdatedStates(Map<Integer, Pair<Character, int[]>> seatRelations){
        return seatRelations.entrySet().stream()
            .map(e -> updateSeatState(e.getKey(), e.getValue(), seatRelations))
            .filter(s -> s != null)
            .toArray(Seat[]::new);
    }

    private void doUpdates(Seat[] updatedSeats, Map<Integer, Pair<Character, int[]>> seatRelations){
        for(Seat s : updatedSeats){
            seatRelations.get(s.pos()).first = s.state();
        }
    }


    private long nrSeatsOccupied(Map<Integer, Pair<Character, int[]>> seatRelations){
        return seatRelations.entrySet().stream().filter(e -> e.getValue().first == '#').count();
    }

    // 2117 is the correct answer
    private void part2(char[] seats){
        Map<Integer, Pair<Character, int[]>> seatRelations = getRelationToSeats(seats);
        Seat[] updatedSeats = getUpdatedStates(seatRelations);

        int counter = 0;
        while(updatedSeats.length > 0) {
            doUpdates(updatedSeats, seatRelations);
            updatedSeats = getUpdatedStates(seatRelations);
        }
        System.out.println("");        
        System.out.println("Part 2: " + nrSeatsOccupied(seatRelations));
    }

    public static void main(String[] args){
        String file = "waiting_area_seats.txt";
        ArrayList<String> input = Eleven.readFile(file);
        Eleven._rowSize = input.get(0).length();
        Eleven._nrRows = input.size();
        Eleven._floorString = new String(new char[_rowSize]).replace("\0", ".");
        Eleven eleven = new Eleven();
        // OBS part1 prob changes input so we need to read it again if needed in part2
        // eleven.part1(input);

        char[] input2 = Eleven.readFile2(file);
        eleven.part2(input2);
    }    
}
