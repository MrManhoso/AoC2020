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

// TODO: move action to separate interface with functions turn, move (forward), position (according to specified direction)
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

    private static void print(int[] a){
        Arrays.stream(a).forEach(i -> System.out.print(String.valueOf(i) + " "));
        System.out.println("");
    }

    private class WayPoint{
        private int[] _pos = new int[]{1, 10, 0, 0};
        private int _rotation = 0;

        private int getDirIndex(Direction dir){ return (4 + dir.getValue() - getRotationOffset()) % 4; }
        private int getDirValue(Direction dir){ return _pos[getDirIndex(dir)]; }

        private int zeroOrPos(int v){
            return v > 0 ? v : 0;
        }
        private int zeroOrAbs(int v){
            return v < 0 ? Math.abs(v) : 0;
        }
        public int[] getRelativePositions(){
            int ns = getDirValue(Direction.N) - getDirValue(Direction.S);
            int ew = getDirValue(Direction.E) - getDirValue(Direction.W);
            return new int[]{zeroOrPos(ns), zeroOrPos(ew), zeroOrAbs(ns), zeroOrAbs(ew) };
        }

        private int getRotationOffset(){
            return (_rotation / 90) % 4;
        }

        public WayPoint position(Direction dir, int steps){
            _pos[getDirIndex(dir)] += steps;
            return this;
        }

        public WayPoint turn(Rotation rot, int degrees){
            _rotation += rot == Rotation.R ? degrees : 360 - degrees;
            return this;
        }
    }

    // north, east, south, west
    private int[] _moves = new int[]{0,0,0,0};
    private int _rotation = 90; // east
    private WayPoint _waypoint = null; //new WayPoint();

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
        int index = dir == Direction.F ? getCurrentDirection() : dir.getValue();
        _moves[index] += steps;
    }

    private void doTurn(Rotation rot, int degrees){
        _rotation += rot == Rotation.R ? degrees : 360 - degrees;
    }

    private static ActionType getActionType(char action){
        return action == 'R' || action == 'L' ? ActionType.Turn : ActionType.Move;
    }

    private void turn(String dir, int degrees){
        if(_waypoint == null) doTurn(Enum.valueOf(Rotation.class, dir), degrees);
        else _waypoint.turn(Enum.valueOf(Rotation.class, dir), degrees);
    }

    private void move(String dir, int steps){
        Direction d = Enum.valueOf(Direction.class, dir);
        if(_waypoint == null) doMove(Enum.valueOf(Direction.class, dir), steps);
        else {
            if(d != Direction.F) _waypoint.position(d, steps);
            else {
                int[] relPos = _waypoint.getRelativePositions();
                _moves[Direction.N.getValue()] += relPos[Direction.N.getValue()] * steps;
                _moves[Direction.E.getValue()] += relPos[Direction.E.getValue()] * steps;
                _moves[Direction.S.getValue()] += relPos[Direction.S.getValue()] * steps;
                _moves[Direction.W.getValue()] += relPos[Direction.W.getValue()] * steps;
            }
        }
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

    private int manhattanDistance(){
        int ew = Math.abs(_moves[Direction.E.getValue()] - _moves[Direction.W.getValue()]);
        int ns = Math.abs(_moves[Direction.N.getValue()] - _moves[Direction.S.getValue()]);
        return ew + ns;
    }

    private void execute(String[] instructions){
        Arrays.stream(instructions).forEach(s -> act(s));
    }

    // What is the Manhattan distance between that location and the ship's starting position?
    // Manhattan distance (sum of the absolute values of its east/west position and its north/south position)
    // correct answer is 1482 (25 for test)
    private void part1(String[] instructions){
        execute(instructions);
        System.out.println("Part 1: " + String.valueOf(manhattanDistance()));
    }

    // The waypoint starts 10 units east and 1 unit north relative to the ship. The waypoint is relative to the ship; that is, if the ship moves, the waypoint moves with it
    // What is the Manhattan distance between that location and the ship's starting position?
    // correct answer is ? (286 for test)
    private void part2(String[] instructions){
        _waypoint = new WayPoint();
        _moves = new int[]{0,0,0,0};
        execute(instructions);
        System.out.println("Part 2: " + String.valueOf(manhattanDistance()));
    }

    public static void main(String[] args) {
        String file = "navigation_instructions.txt";
        String[] lines = readFile(file);
        Twelve twelve = new Twelve();
        twelve.part1(lines);
        twelve.part2(lines);
    }
}
