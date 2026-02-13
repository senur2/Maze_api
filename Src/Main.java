package Src;

public class Main {
    public static void main(String[] args) {
        try {
            Grille grille = new Grille(30, 30);
            grille.sidewing(45, 5);
            grille.smartBraid();
            Labirinthe lab = Utilitaire.genererLabirinthe(grille);
            Benchmark benchmark = new Benchmark(grille);
            benchmark.print();
            Utilitaire.renderPacman(lab, 10);
            grille.renderMaze(20);
            lab.renderGraphe(20);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}