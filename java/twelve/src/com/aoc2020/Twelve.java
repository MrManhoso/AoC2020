package com.aoc2020;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;


/*
PART 1
Action N means to move north by the given value.
Action S means to move south by the given value.
Action E means to move east by the given value.
Action W means to move west by the given value.
Action L means to turn left the given number of degrees.
Action R means to turn right the given number of degrees.
Action F means to move forward by the given value in the direction the ship is currently facing.

PART 2
Action N means to move the waypoint north by the given value.
Action S means to move the waypoint south by the given value.
Action E means to move the waypoint east by the given value.
Action W means to move the waypoint west by the given value.
Action L means to rotate the waypoint around the ship left (counter-clockwise) the given number of degrees.
Action R means to rotate the waypoint around the ship right (clockwise) the given number of degrees.
Action F means to move forward to the waypoint a number of times equal to the given value.

 */
public class Twelve {
    private enum Direction{
        N(0), E(1), S(2), W(3), F(4);

        private final int _value;
        Direction(int value){
            _value = value;
        }
        int getValue(){ return _value; }
    }

    private enum Rotation {
        R, L
    }

    private enum ActionType{
        Turn, Move
    }

    private class WayPoint{
        private int[] _pos = new int[]{1, 10, 0, 0};
        public int getNorth(){ return _pos[Direction.N.getValue()]; }
        public int getEast(){ return _pos[Direction.E.getValue()]; }
        public int getSouth(){ return _pos[Direction.S.getValue()]; }
        public int getWest(){ return _pos[Direction.W.getValue()]; }
    }

    // north, east, south, west
    private int[] _moves = new int[]{0,0,0,0};
    private int _rotation = 90; // east

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

    // gets current direction/rotation as a digit between 0-3
    private int getCurrentDirection(){
        return (_rotation / 90) % 4;
    }

    private void doMove(Direction dir, int steps){
        int index = dir == Direction.F ? getCurrentDirection(): dir.getValue();
        _moves[index] += steps;
    }

    private void doTurn(Rotation rot, int degrees){
        _rotation += rot == Rotation.R ? degrees : 360 - degrees;
    }

    private static ActionType getActionType(char action){
        return action == 'R' || action == 'L' ? ActionType.Turn : ActionType.Move;
    }

    private void turn(String dir, int degrees){
        doTurn(Enum.valueOf(Rotation.class, dir), degrees);
    }
    private void move(String dir, int steps){
        doMove(Enum.valueOf(Direction.class, dir), steps);
    }

    private void act(String action){
        String actionType = action.substring(0,1);
        int value = Integer.parseInt(action.substring(1));
        switch(getActionType(actionType.charAt(0))){
            case Turn:
                turn(actionType, value);
                break;
            case Move:
                move(actionType, value);
                break;
            default:
                break;
        }
    }

    // What is the Manhattan distance between that location and the ship's starting position?
    // Manhattan distance (sum of the absolute values of its east/west position and its north/south position)
    // correct answer is 1482
    private void part1(String[] instructions){
        Arrays.stream(instructions).forEach(s -> act(s));
        int ew = Math.abs(_moves[Direction.E.getValue()] - _moves[Direction.W.getValue()]);
        int ns = Math.abs(_moves[Direction.N.getValue()] - _moves[Direction.S.getValue()]);
        System.out.println("Part 1: " + String.valueOf(ew + ns));
    }

    // The waypoint starts 10 units east and 1 unit north relative to the ship. The waypoint is relative to the ship; that is, if the ship moves, the waypoint moves with it
    // What is the Manhattan distance between that location and the ship's starting position?
    private void part2(String[] instructions){

    }

    public static void main(String[] args) {
        String file = "navigation_instructions.txt";
        String[] lines = readFile(file);
        Twelve twelve = new Twelve();
        twelve.part1(lines);
    }
}
