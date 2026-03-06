package fr.univ.maze.core;

import fr.univ.maze.dto.MazeDTO;

public class MazeRunner {

    // Cette méthode devient l'unique endroit où l'on orchestre les algos !
    public static MazeDTO generateMaze(MazeConfig config) {
        
        // 1. Création de la grille
        Grille grille = new Grille(config.height, config.width, config.seed);

        // 2. Choix et exécution de l'algorithme
        if ("sidewinder".equalsIgnoreCase(config.algo)) {
            grille.sidewing(config.odd, config.e);
            grille.smartBraid();
            grille.breakLongCorridors(2);
            grille.carveCentralRoom();
        } else {
            throw new IllegalArgumentException("Algorithme non supporté : " + config.algo);
        }

        Benchmark benchmark = new Benchmark(grille);
        benchmark.print();

        // 3. Transformation en DTO pour l'envoi sur le web
        return MazeMapper.toDTO(config, grille);
    }
}
