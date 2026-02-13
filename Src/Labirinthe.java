package Src;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class Labirinthe {
    private Graphe graphe;
    private Grille grille;

    public Labirinthe(Graphe graphe, Grille grille) {
        this.graphe = graphe;
        this.grille = grille;
    }

    public Graphe getGraphe() {
        return graphe;
    }

    public Grille getGrille() {
        return grille;
    }

    // Affiche la version "graphe" (noeuds et arretes) dans une image PNG.
    // Ici, on place simplement les noeuds sur une grille logique en se basant
    // sur leur indice dans la liste du graphe (ce n'est plus lié à Grille).
    public void renderGraphe(int cellSize) {
        java.util.ArrayList<Noeud> noeuds = graphe.getListeDesNoeuds();
        int n = noeuds.size();
        if (n == 0) {
            System.out.println("Graphe vide, rien à afficher.");
            return;
        }

        int cols = (int) Math.ceil(Math.sqrt(n));
        int lignes = (int) Math.ceil((double) n / cols);

        int imgWidth = cols * cellSize;
        int imgHeight = lignes * cellSize;

        BufferedImage img = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();

        // fond blanc
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, imgWidth, imgHeight);

        // dessiner les arretes
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.setStroke(new BasicStroke(2));
        for (int i = 0; i < n; i++) {
            Noeud ni = noeuds.get(i);
            int ri = i / cols;
            int ci = i % cols;
            int x1 = ci * cellSize + cellSize / 2;
            int y1 = ri * cellSize + cellSize / 2;

            for (Noeud v : graphe.voisins(ni)) {
                int j = noeuds.indexOf(v);
                if (j == -1) continue;
                int rj = j / cols;
                int cj = j % cols;
                int x2 = cj * cellSize + cellSize / 2;
                int y2 = rj * cellSize + cellSize / 2;
                g2d.drawLine(x1, y1, x2, y2);
            }
        }

        // dessiner les noeuds
        g2d.setColor(Color.RED);
        int radius = cellSize / 4;
        g2d.setColor(Color.RED);
        for (int i = 0; i < n; i++) {
            int r = i / cols;
            int c = i % cols;
            int cx = c * cellSize + cellSize / 2;
            int cy = r * cellSize + cellSize / 2;
            g2d.fillOval(cx - radius, cy - radius, radius * 2, radius * 2);
        }

        g2d.dispose();

        try {
            ImageIO.write(img, "png", new File("labyrinthe_graphe.png"));
            System.out.println("Graphe du labyrinthe généré : labyrinthe_graphe.png");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}