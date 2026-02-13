package com.maze;

public class Main {
    public static void main(String[] args) {
        try {
            maze maze = new maze(30, 30);
            maze.sidewing(35, 2);
            maze.renderMaze(20);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}