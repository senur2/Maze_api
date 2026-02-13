package com.maze;

import java.util.ArrayList;
import java.util.Random;
import java.util.function.Consumer;
import java.util.Random;
import javax.imageio.ImageIO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

public class maze {

    private final int rows;
    private final int columns;
    private final cell[][] grid;
    private final Random random = new Random();

    public maze(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.grid = prepareGrid();
        configureCells();
    }

public void renderMaze(int cellSize) {
    int imgWidth = this.columns * cellSize;
    int imgHeight = this.rows * cellSize;

    // Création de l'image
    BufferedImage img = new BufferedImage(imgWidth + 1, imgHeight + 1, BufferedImage.TYPE_INT_RGB);
    Graphics2D g2d = img.createGraphics();

    // Fond blanc
    g2d.setColor(Color.WHITE);
    g2d.fillRect(0, 0, imgWidth + 1, imgHeight + 1);

    // Murs noirs
    g2d.setColor(Color.BLACK);
    g2d.setStroke(new BasicStroke(1));

    // Itération sur chaque cellule pour dessiner les murs
    this.eachCell(cell -> {
        int x1 = cell.column * cellSize;
        int y1 = cell.row * cellSize;
        int x2 = (cell.column + 1) * cellSize;
        int y2 = (cell.row + 1) * cellSize;

        // Si pas de voisin au nord, on trace le mur extérieur haut
        if (cell.north == null) {
            g2d.drawLine(x1, y1, x2, y1);
        }
        // Si pas de voisin à l'ouest, on trace le mur extérieur gauche
        if (cell.west == null) {
            g2d.drawLine(x1, y1, x1, y2);
        }
        
        // On trace le mur à l'EST si la cellule n'est pas liée à son voisin de droite
        if (!cell.isLinked(cell.east)) {
            g2d.drawLine(x2, y1, x2, y2);
        }
        // On trace le mur au SUD si la cellule n'est pas liée à son voisin du bas
        if (!cell.isLinked(cell.south)) {
            g2d.drawLine(x1, y2, x2, y2);
        }
    }); 

    g2d.dispose();

    try {
        ImageIO.write(img, "png", new File("maze.png"));
        System.out.println("Labyrinthe généré sous : maze.png");
    } catch (Exception e) {
        e.printStackTrace();
    }
}

protected cell[][] prepareGrid() {
        cell[][] cells = new cell[rows][columns];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                cells[r][c] = new cell(r, c);
            }
        }
        return cells;
    }

protected void configureCells() {
        eachCell(cell -> {
            int row = cell.row;
            int col = cell.column;

            cell.north = get(row - 1, col);
            cell.south = get(row + 1, col);
            cell.west  = cell.west = get(row, col - 1);
            cell.east  = get(row, col + 1);
        });
    }

public cell get(int row, int column) {
        if (row < 0 || row >= rows) return null;
        if (column < 0 || column >= columns) return null;
        return grid[row][column];
    }

public cell randomCell() {
        int row = random.nextInt(rows);
        int col = random.nextInt(columns);
        return get(row, col);
    }

public int size() {
        return rows * columns;
    }

public void eachRow(Consumer<cell[]> block) {
        for (cell[] row : grid) {
            block.accept(row);
        }
    }

public void eachCell(Consumer<cell> block) {
        eachRow(row -> {
            for (cell cell : row) {
                if (cell != null) {
                    block.accept(cell);
                }
            }
        });
    }

public void sidewing (int odd, int e){
    boolean close = false;
    for (int row = grid.length/2; row < grid.length; row++) {
        ArrayList<cell> run = new ArrayList<>();
        ArrayList<cell> cell_up = new ArrayList<>();
        for (cell cell : grid[row]) {
            run.add(cell);
            Random rand = new Random();
            if (cell.west == null || rand.nextInt(100) > odd) {
                 close = true;
            }
            if (close){
                for (int i = 0; i<(run.size()/e); i++){
                    int index = rand.nextInt(run.size());
                    cell_up.add(run.get(index));
                    run.remove(index);
                }
            while (cell_up.size() > 0){
                cell picked_cell = cell_up.remove(0);
                cell sym_cell = get((rows - 1) - cell.row, cell.column);
                if (picked_cell.north != null){
                    picked_cell.link(picked_cell.north);
                }
                if (sym_cell.south != null){
                    sym_cell.link(sym_cell.south);
                }
            }
            close = false;
            run.clear();
            }
            else {
                cell sym_cell = get((rows - 1) - cell.row, cell.column);
                if (sym_cell.east != null){
                    sym_cell.link(sym_cell.east);
                }
                if (cell.east != null){
                        cell.link(cell.east);
                }
            }
        }
    }
}
}
