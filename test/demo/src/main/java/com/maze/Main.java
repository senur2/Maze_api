package com.maze;

public class Main {
    public static void main(String[] args) {
        try {
            maze maze = new maze(30, 30);
            maze.sidewing(30, 3);
            maze.braid(20);
            Benchmark benchmark = new Benchmark(maze);
            benchmark.print();
            maze.renderMaze(20);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}