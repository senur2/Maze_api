package fr.univ.maze.dto;

import java.util.ArrayList;
import java.util.List;

public class MazeDTO {
    public int width;
    public int height;
    public long seed;
    public String algo;
    public int odd;
    public int e;
    public List<CellDTO> cells = new ArrayList<>();
}