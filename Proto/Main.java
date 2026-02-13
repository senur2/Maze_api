package Proto;

public class Main {
    public static void main(String[] args) {
        try {
            Grille grille = new Grille(30, 30);
            grille.sidewing(50, 5);
            Labirinthe lab = Utilitaire.genererLabirinthe(grille);
            Benchmark benchmark = new Benchmark(grille);
            benchmark.print();
            grille.renderMaze(20);
            lab.renderGraphe(20);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}