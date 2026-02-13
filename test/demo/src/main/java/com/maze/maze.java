package com.maze;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

public int getRows() {
        return rows;
    }

public int getColumns() {
        return columns;
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

public void sidewing(int odd, int e) {
    Random rand = new Random();
    for (int r = rows / 2; r < rows; r++) {
        ArrayList<cell> run = new ArrayList<>();
        
        for (int c = 0; c < columns; c++) {
            cell currentCell = get(r, c);
            run.add(currentCell);
            boolean close = (currentCell.east == null) || (rand.nextInt(100) < odd);

            if (close) {
                // Utilisation du plafond (Math.ceil) pour garantir au moins un passage
                int nbPassages = (int) Math.ceil((double) run.size() / e);
                
                for (int i = 0; i < nbPassages; i++) {
                    if (run.isEmpty()) break;
                    cell picked = run.remove(rand.nextInt(run.size()));
    
                    if (picked.north != null) {
                        picked.link(picked.north);
                        cell sym = get((rows - 1) - picked.row, picked.column);
                        if (sym != null && sym.south != null) {
                            sym.link(sym.south);
                        }
                    }
                }
                run.clear();
            } else {
                if (currentCell.east != null) {
                    currentCell.link(currentCell.east);
                    cell sym = get((rows - 1) - currentCell.row, currentCell.column);
                    if (sym != null && sym.east != null) {
                        sym.link(sym.east);
                    }
                }
            }
        }
    }
}

public void smartBraid() {
    Random rand = new Random();
    for (int r = rows / 2; r < rows; r++) {
        for (int c = 0; c < columns; c++) {
            cell current = get(r, c);
            
            if (current != null && current.links().size() == 1) {
                List<cell> potentialNeighbors = current.neighbors();
                // On ne garde que les voisins non liés
                potentialNeighbors.removeIf(current::isLinked);
                
                Collections.shuffle(potentialNeighbors); // Aléatoire pour la variété
                
                for (cell neighbor : potentialNeighbors) {
                    if (!completesRoom(current, neighbor)) {
                        // On lie la cellule
                        current.link(neighbor);
                        
                        // On applique la symétrie miroir 
                        cell symCurrent = get((rows - 1) - current.row, current.column);
                        cell symNeighbor = get((rows - 1) - neighbor.row, neighbor.column);
                        if (symCurrent != null && symNeighbor != null) {
                            symCurrent.link(symNeighbor);
                        }
                        
                        break; // On a cassé le cul-de-sac, on passe à la suite
                    }
                }
            }
        }
    }
}

/**
 * Vérifie si lier 'a' et 'b' créerait un espace 2x2 vide.
 */
private boolean completesRoom(cell a, cell b) {
    // Si le lien est Vertical (Nord/Sud)
    if (a.column == b.column) {
        return checkSide(a, b, -1) || checkSide(a, b, 1); // Check Ouest et Est
    } 
    // Si le lien est Horizontal (Est/Ouest)
    else {
        return checkAboveBelow(a, b, -1) || checkAboveBelow(a, b, 1); // Check Nord et Sud
    }
}

private boolean checkSide(cell a, cell b, int offset) {
    cell aSide = get(a.row, a.column + offset);
    cell bSide = get(b.row, b.column + offset);
    if (aSide == null || bSide == null) return false;
    
    // Un carré 2x2 se forme si ces 3 liens existent déjà
    return a.isLinked(aSide) && b.isLinked(bSide) && aSide.isLinked(bSide);
}

private boolean checkAboveBelow(cell a, cell b, int offset) {
    cell aVert = get(a.row + offset, a.column);
    cell bVert = get(b.row + offset, b.column);
    if (aVert == null || bVert == null) return false;
    
    return a.isLinked(aVert) && b.isLinked(bVert) && aVert.isLinked(bVert);
}
}