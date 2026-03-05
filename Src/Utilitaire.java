package Src;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class Utilitaire {

    // Construit un objet Labirinthe STRICTEMENT équivalent au Maze actuel.
    // On ne regénère PAS un nouveau labyrinthe : on copie simplement
    // la structure déjà encodée dans les Noeud (voisins) vers un Graphe.
    public static Labirinthe genererLabirinthe(Grille grille) {
        int rows = grille.getRows();
        int columns = grille.getColumns();

        Graphe graph = new Graphe();

        // 1) Ajouter au graphe tous les noeuds qui correspondent à des cases de passage
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                Noeud n = grille.get(r, c);
                if (!n.getVoisins().isEmpty()) {
                    graph.ajouterNoeud(n);
                }
            }
        }

        // 2) Ajouter les arretes du graphe en fonction des liaisons entre Noeud
        //    IMPORTANT : on ne modifie PAS les voisins des Noeud ici, on
        //    se contente de créer les objets Arrete dans le Graphe.
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                Noeud n = grille.get(r, c);
                // on ne crée des arretes qu'entre cases de passage (noeuds qui ont des voisins)
                if (n.getVoisins().isEmpty()) continue;

                // pour éviter les doublons, on ne regarde que l'est et le sud
                Noeud east = grille.get(r, c + 1);
                if (east != null && !east.getVoisins().isEmpty() && isLinked(n, east)) {
                    graph.getListeDesArretes().add(new Arrete(n, east));
                }

                Noeud south = grille.get(r + 1, c);
                if (south != null && !south.getVoisins().isEmpty() && isLinked(n, south)) {
                    graph.getListeDesArretes().add(new Arrete(n, south));
                }
            }
        }

        return new Labirinthe(graph, grille);
    }

    // Renvoie vrai si deux Noeud sont déjà liés (via leur liste de voisins)
    private static boolean isLinked(Noeud a, Noeud b) {
        if (a == null || b == null) return false;
        return a.getVoisins().contains(b);
    }

    // Affiche un Labirinthe comme un labyrinthe de Pac-Man avec des murs pleins
    public static void renderPacman(Labirinthe labirinthe, int cellSize) {
        Grille grille = labirinthe.getGrille();
        int rows = grille.getRows();
        int columns = grille.getColumns();

        // On construit une grille "mur/passage" de taille (2*rows+1) x (2*columns+1)
        int wallRows = 2 * rows + 1;
        int wallCols = 2 * columns + 1;
        boolean[][] passages = new boolean[wallRows][wallCols];

        // Initialisation des passages en fonction des liens entre Noeud
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                Noeud cell = grille.get(r, c);
                if (cell == null) continue;

                int pr = 2 * r + 1;
                int pc = 2 * c + 1;
                passages[pr][pc] = true; // centre de la case = couloir

                // Lien vers l'est
                Noeud east = grille.get(r, c + 1);
                if (east != null && isLinked(cell, east)) {
                    passages[pr][pc + 1] = true; // ouverture horizontale
                }

                // Lien vers le sud
                Noeud south = grille.get(r + 1, c);
                if (south != null && isLinked(cell, south)) {
                    passages[pr + 1][pc] = true; // ouverture verticale
                }
            }
        }

        // On ne rend que la partie intérieure (sans la bordure extérieure)
        int innerRows = wallRows - 2;
        int innerCols = wallCols - 2;
        int imgWidth = innerCols * cellSize;
        int imgHeight = innerRows * cellSize;

        BufferedImage img = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();

        // Fond noir pour l'ensemble de l'image (couloirs)
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, imgWidth, imgHeight);

        // Dessin des murs sur la zone intérieure seulement
        for (int r = 1; r < wallRows - 1; r++) {
            for (int c = 1; c < wallCols - 1; c++) {
                if (!passages[r][c]) {
                    int x = (c - 1) * cellSize;
                    int y = (r - 1) * cellSize;
                    // Mur plein (bleu Pac-Man)
                    g2d.setColor(new Color(0, 0, 200));
                    g2d.fillRect(x, y, cellSize, cellSize);
                }
            }
        }

        g2d.dispose();

        try {
            ImageIO.write(img, "png", new File("labyrinthe_pacman.png"));
            System.out.println("Labyrinthe Pac-Man généré : labyrinthe_pacman.png");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
