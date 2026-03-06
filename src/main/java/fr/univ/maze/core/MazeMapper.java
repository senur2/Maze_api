package fr.univ.maze.core;

import fr.univ.maze.dto.CellDTO;
import fr.univ.maze.dto.MazeDTO;
import fr.univ.maze.dto.NeighborDTO;


public class MazeMapper {

    public static MazeDTO toDTO(MazeConfig config, Grille grille) {
        // 1. Initialisation du conteneur principal
        MazeDTO mazeDTO = new MazeDTO();
        mazeDTO.width = config.width;
        mazeDTO.height = config.height;
        mazeDTO.seed = config.seed;
        mazeDTO.algo = config.algo;
        mazeDTO.odd = config.odd;
        mazeDTO.e = config.e;

        int rows = grille.getRows();
        int cols = grille.getColumns();

        // 2. Parcours itératif de la topologie
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Noeud noeud = grille.get(r, c);
                if (noeud == null) continue;

                // Création du DTO pour la cellule courante
                CellDTO cellDTO = new CellDTO();
                cellDTO.row = r;
                cellDTO.col = c;

                // 3. Extraction des voisins (le coeur de la liste d'adjacence)
                // On regarde uniquement les passages ouverts (liens existants)
                for (Noeud voisin : noeud.getVoisins()) {
                    if (voisin != null) {
                        NeighborDTO neighborDTO = new NeighborDTO();
                        // On stocke uniquement les primitives (les coordonnées), pas l'objet
                        neighborDTO.row = voisin.getRow();
                        neighborDTO.col = voisin.getCol();
                        cellDTO.neighbors.add(neighborDTO);
                    }
                }

                // Ajout de la cellule traitée à la liste principale
                mazeDTO.cells.add(cellDTO);
            }
        }

        return mazeDTO;
    }
}