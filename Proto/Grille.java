package Proto;

import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

// Générateur de labyrinthe basé sur l'algorithme Sidewinder,
// adapté pour utiliser les Noeud de Proto (et non plus Cell de Proto2).
public class Grille {

    private final int rows;//a supprimer
    private final int columns;//a suprimer
    // la structure du labyrinthe est portée uniquement par la grille de Noeud et leurs lien
    private final Noeud[][] grid; // grille interne de Noeud
    private final Random random = new Random();

    public Grille(int rows, int columns) {
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

        // Itération sur chaque cellule (Noeud) pour dessiner les murs
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                Noeud cell = grid[r][c];

                int x1 = c * cellSize;
                int y1 = r * cellSize;
                int x2 = (c + 1) * cellSize;
                int y2 = (r + 1) * cellSize;

                Noeud north = get(r - 1, c);
                Noeud south = get(r + 1, c);
                Noeud west  = get(r, c - 1);
                Noeud east  = get(r, c + 1);

                // Si pas de voisin au nord, on trace le mur extérieur haut
                if (north == null) {
                    g2d.drawLine(x1, y1, x2, y1);
                }
                // Si pas de voisin à l'ouest, on trace le mur extérieur gauche
                if (west == null) {
                    g2d.drawLine(x1, y1, x1, y2);
                }

                // On trace le mur à l'EST si la cellule n'est pas liée à son voisin de droite
                if (!isLinked(cell, east)) {
                    g2d.drawLine(x2, y1, x2, y2);
                }
                // On trace le mur au SUD si la cellule n'est pas liée à son voisin du bas
                if (!isLinked(cell, south)) {
                    g2d.drawLine(x1, y2, x2, y2);
                }
            }
        }

        g2d.dispose();

        try {
            ImageIO.write(img, "png", new File("maze.png"));
            System.out.println("Labyrinthe généré sous : maze.png");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Prépare une grille de Noeud
    protected Noeud[][] prepareGrid() {
        Noeud[][] cells = new Noeud[rows][columns];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                cells[r][c] = new Noeud(0);
            }
        }
        return cells;
    }

    // Configure les voisins potentiels (N,E,S,O) pour chaque Noeud
    protected void configureCells() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                Noeud cell = grid[r][c];

                if (r - 1 >= 0) cell.setVoisinPotentiel(0, grid[r - 1][c]); // nord
                if (c + 1 < columns) cell.setVoisinPotentiel(1, grid[r][c + 1]); // est
                if (r + 1 < rows) cell.setVoisinPotentiel(2, grid[r + 1][c]); // sud
                if (c - 1 >= 0) cell.setVoisinPotentiel(3, grid[r][c - 1]); // ouest
            }
        }
    }

    public Noeud get(int row, int column) {
        if (row < 0 || row >= rows) return null;
        if (column < 0 || column >= columns) return null;
        return grid[row][column];
    }

    public Noeud randomCell() {
        int row = random.nextInt(rows);
        int col = random.nextInt(columns);
        return get(row, col);
    }

    public int size() {
        return rows * columns;
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    // Algorithme Sidewinder adapté à Noeud
    public void sidewing(int odd, int e) {
        Random rand = new Random();
        for (int r = rows / 2; r < rows; r++) {
            ArrayList<int[]> run = new ArrayList<>(); // stocke les coordonnées (row, col)
            
            for (int c = 0; c < columns; c++) {
                Noeud currentCell = get(r, c);
                run.add(new int[]{r, c});
                boolean eastExists = (c + 1 < columns);
                boolean close = (!eastExists) || (rand.nextInt(100) < odd);

                if (close) {
                    // Utilisation du plafond (Math.ceil) pour garantir au moins un passage
                    int nbPassages = (int) Math.ceil((double) run.size() / e);
                    
                    for (int i = 0; i < nbPassages; i++) {
                        if (run.isEmpty()) break;
                        int[] pickedCoords = run.remove(rand.nextInt(run.size()));
                        int pr = pickedCoords[0];
                        int pc = pickedCoords[1];

                        if (pr - 1 >= 0) {
                            Noeud picked = get(pr, pc);
                            Noeud north = get(pr - 1, pc);
                            linkVertical(north, picked); // lie picked avec son nord

                            int symRow = (rows - 1) - pr;
                            Noeud sym = get(symRow, pc);
                            if (sym != null && symRow + 1 < rows) {
                                Noeud southOfSym = get(symRow + 1, pc);
                                linkVertical(sym, southOfSym);
                            }
                        }
                    }
                    run.clear();
                } else {
                    if (c + 1 < columns) {
                        Noeud east = get(r, c + 1);
                        linkHorizontal(currentCell, east);

                        int symRow = (rows - 1) - r;
                        Noeud sym = get(symRow, c);
                        if (sym != null && c + 1 < columns) {
                            Noeud symEast = get(symRow, c + 1);
                            linkHorizontal(sym, symEast);
                        }
                    }
                }
            }
        }
    }

    // Renvoie vrai si deux Noeud sont déjà liés (via leur liste de voisins)
    private boolean isLinked(Noeud a, Noeud b) {
        if (a == null || b == null) return false;
        return a.getVoisins().contains(b);
    }

    // Lie deux noeuds verticalement (nord/sud)
    private void linkVertical(Noeud north, Noeud south) {
        if (north == null || south == null) return;
        north.ajouterVoisin(2, south); // sud de north
        south.ajouterVoisin(0, north); // nord de south
    }

    // Lie deux noeuds horizontalement (ouest/est)
    private void linkHorizontal(Noeud west, Noeud east) {
        if (west == null || east == null) return;
        west.ajouterVoisin(1, east); // est de west
        east.ajouterVoisin(3, west); // ouest de east
    }
}