package fr.univ.maze.dto;

import java.util.ArrayList;
import java.util.List;

// Représente un objet dans la liste "cells"
public class CellDTO {
    public int row;
    public int col;
    public List<NeighborDTO> neighbors = new ArrayList<>();
}