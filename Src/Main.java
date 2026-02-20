package Src;

public class Main {
    public static void main(String[] args) {
        try {
            Grille grille = new Grille(30, 30);
            grille.sidewing(45, 5);
            grille.smartBraid();
            grille.breakLongCorridors(1);
            Benchmark benchmark = new Benchmark(grille);
            benchmark.print();
            grille.carveCentralRoom(); // Ajout de la salle centrale
            Labyrinthe lab = Utilitaire.genererLabyrinthe(grille);
            Utilitaire.renderPacman(lab, 10);
            grille.renderMaze(20);
            lab.renderGraphe(20);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}