package Src;

public class Main {

    public static void main(String[] args) {
        try {
            MazeConfig config = ArgsParser.parse(args);
            MazeRunner.run(config);
        } catch (IllegalArgumentException e) {
            System.err.println("Erreur : " + e.getMessage());
            System.err.println("Utilisez --help pour voir les options disponibles.");
            System.exit(1);
        }
    }
}
