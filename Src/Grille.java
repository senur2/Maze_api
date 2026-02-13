package Src;

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

    public void smartBraid() {
        Random rand = new Random();

        // On parcourt la moitié inférieure comme dans sidewing
        for (int r = rows / 2; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                Noeud current = get(r, c);
                if (current == null) continue;

                // On ne traite que les culs-de-sac (degré 1)
                int degree = 0;
                for (Noeud v : current.getVoisins()) {
                    if (v != null) degree++;
                }
                if (degree != 1) continue;

                // Candidats : voisins potentiels non encore liés
                ArrayList<Noeud> voisinsPotentiels = current.getVoisinsPotentiels();
                ArrayList<Noeud> candidats = new ArrayList<>();
                for (Noeud v : voisinsPotentiels) {
                    if (v != null && !isLinked(current, v)) {
                        candidats.add(v);
                    }
                }

                // On essaie les candidats dans un ordre aléatoire, en évitant de créer des pièces 2x2
                while (!candidats.isEmpty()) {
                    int idx = rand.nextInt(candidats.size());
                    Noeud voisinChoisi = candidats.remove(idx);

                    int dir = voisinsPotentiels.indexOf(voisinChoisi); // 0=nord,1=est,2=sud,3=ouest
                    int nr = r;
                    int nc = c;
                    if (dir == 0) {
                        nr = r - 1;
                    } else if (dir == 1) {
                        nc = c + 1;
                    } else if (dir == 2) {
                        nr = r + 1;
                    } else if (dir == 3) {
                        nc = c - 1;
                    } else {
                        continue;
                    }

                    if (nr < 0 || nr >= rows || nc < 0 || nc >= columns) {
                        continue;
                    }

                    // Si ce lien créerait une "room" 2x2 ouverte, on l'ignore
                    if (completesRoom(r, c, nr, nc)) {
                        continue;
                    }

                    // Sinon, on lie current et voisinChoisi
                    if (dir == 0) {
                        // voisin au nord de current
                        linkVertical(voisinChoisi, current);
                    } else if (dir == 2) {
                        // voisin au sud de current
                        linkVertical(current, voisinChoisi);
                    } else if (dir == 1) {
                        // voisin à l'est de current
                        linkHorizontal(current, voisinChoisi);
                    } else if (dir == 3) {
                        // voisin à l'ouest de current
                        linkHorizontal(voisinChoisi, current);
                    }

                    // Appliquer la symétrie miroir verticalement comme dans Proto2
                    int symCurrentRow = (rows - 1) - r;
                    int symNeighborRow = (rows - 1) - nr;
                    Noeud symCurrent = get(symCurrentRow, c);
                    Noeud symNeighbor = get(symNeighborRow, nc);
                    if (symCurrent != null && symNeighbor != null) {
                        if (dir == 0) {
                            // voisinChoisi était au nord de current, son symétrique est au sud
                            linkVertical(symCurrent, symNeighbor);
                        } else if (dir == 2) {
                            // voisinChoisi était au sud de current, son symétrique est au nord
                            linkVertical(symNeighbor, symCurrent);
                        } else if (dir == 1) {
                            // voisinChoisi était à l'est de current, son symétrique est aussi à l'est
                            linkHorizontal(symCurrent, symNeighbor);
                        } else if (dir == 3) {
                            // voisinChoisi était à l'ouest de current, son symétrique est aussi à l'ouest
                            linkHorizontal(symNeighbor, symCurrent);
                        }
                    }

                    // On a cassé le cul-de-sac, on passe à la cellule suivante
                    break;
                }
            }
        }
    }

    // Vérifie si lier les cellules (ar,ac) et (br,bc) créerait une pièce 2x2 ouverte
    private boolean completesRoom(int ar, int ac, int br, int bc) {
        if (ac == bc) {
            // Lien vertical (Nord/Sud)
            return checkSide(ar, ac, br, bc, -1) || checkSide(ar, ac, br, bc, 1);
        } else {
            // Lien horizontal (Est/Ouest)
            return checkAboveBelow(ar, ac, br, bc, -1) || checkAboveBelow(ar, ac, br, bc, 1);
        }
    }

    private boolean checkSide(int ar, int ac, int br, int bc, int offset) {
        Noeud a = get(ar, ac);
        Noeud b = get(br, bc);
        if (a == null || b == null) return false;

        Noeud aSide = get(ar, ac + offset);
        Noeud bSide = get(br, bc + offset);
        if (aSide == null || bSide == null) return false;

        // Un carré 2x2 se forme si ces 3 liens existent déjà
        return isLinked(a, aSide) && isLinked(b, bSide) && isLinked(aSide, bSide);
    }

    private boolean checkAboveBelow(int ar, int ac, int br, int bc, int offset) {
        Noeud a = get(ar, ac);
        Noeud b = get(br, bc);
        if (a == null || b == null) return false;

        Noeud aVert = get(ar + offset, ac);
        Noeud bVert = get(br + offset, bc);
        if (aVert == null || bVert == null) return false;

        return isLinked(a, aVert) && isLinked(b, bVert) && isLinked(aVert, bVert);
    }
}