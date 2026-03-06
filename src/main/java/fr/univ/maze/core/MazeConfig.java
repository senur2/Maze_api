package fr.univ.maze.core;

/**
 * Objet de configuration du labyrinthe, construit par ArgsParser.
 * Porte toutes les valeurs des paramètres CLI.
 */
public class MazeConfig {

    public final int width;
    public final int height;
    public final long seed;
    public final boolean seedSet; // true si --seed a été fourni explicitement
    public final String algo;
    public final int odd;
    public final int e;
    public final String out; // null = affichage terminal

    public MazeConfig(int width, int height, long seed, boolean seedSet,
            String algo, int odd, int e, String out) {
        this.width = width;
        this.height = height;
        this.seed = seed;
        this.seedSet = seedSet;
        this.algo = algo;
        this.odd = odd;
        this.e = e;
        this.out = out;
    }

    @Override
    public String toString() {
        return "MazeConfig{" +
                "width=" + width +
                ", height=" + height +
                ", seed=" + seed +
                ", algo='" + algo + '\'' +
                ", odd=" + odd +
                ", e=" + e +
                ", out='" + (out != null ? out : "(terminal)") + '\'' +
                '}';
    }
}
