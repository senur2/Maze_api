package fr.univ.maze.render;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import fr.univ.maze.dto.NeighborDTO;
import fr.univ.maze.dto.MazeDTO;
import fr.univ.maze.dto.CellDTO;

public class MazeRenderer {

    public static void renderMazeFromJson(MazeDTO maze, int cellSize, String outputPath) {
        int imgWidth = maze.width * cellSize;
        int imgHeight = maze.height * cellSize;

        // Création de l'image
        BufferedImage img = new BufferedImage(imgWidth + 1, imgHeight + 1, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();

        // Fond blanc
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, imgWidth + 1, imgHeight + 1);

        // Murs noirs
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(1));

        // Itération sur chaque cellule reçue dans le JSON
        for (CellDTO cell : maze.cells) {
            int r = cell.row;
            int c = cell.col;

            int x1 = c * cellSize;
            int y1 = r * cellSize;
            int x2 = (c + 1) * cellSize;
            int y2 = (r + 1) * cellSize;

            // Si r == 0 (pas de voisin au nord), on trace le mur extérieur haut
            if (r == 0) {
                g2d.drawLine(x1, y1, x2, y1);
            }
            // Si c == 0 (pas de voisin à l'ouest), on trace le mur extérieur gauche
            if (c == 0) {
                g2d.drawLine(x1, y1, x1, y2);
            }

            // Vérification des liaisons avec les voisins Est et Sud d'après le JSON
            boolean linkedEast = false;
            boolean linkedSouth = false;

            for (NeighborDTO neighbor : cell.neighbors) {
                if (neighbor.row == r && neighbor.col == c + 1) {
                    linkedEast = true;
                }
                if (neighbor.row == r + 1 && neighbor.col == c) {
                    linkedSouth = true;
                }
            }

            // On trace le mur à l'EST si la cellule n'est pas liée à son voisin de droite
            if (!linkedEast) {
                g2d.drawLine(x2, y1, x2, y2);
            }
            // On trace le mur au SUD si la cellule n'est pas liée à son voisin du bas
            if (!linkedSouth) {
                g2d.drawLine(x1, y2, x2, y2);
            }
        }

        g2d.dispose();

        // Sauvegarde de l'image sur le disque
        try {
            ImageIO.write(img, "png", new File(outputPath));
            System.out.println("Image du labyrinthe sauvegardée : " + outputPath);
        } catch (Exception e) {
            System.err.println("Erreur lors de la sauvegarde de l'image : " + e.getMessage());
        }
    }
}