package Src;

/**
 * Classe responsable du parsing et de la validation des arguments
 * de la ligne de commande.
 *
 * PRINCIPE DE FONCTIONNEMENT :
 * Les arguments sont lus par paires : un flag suivi de sa valeur.
 * Exemple : --width 28 → flag="--width", valeur="28"
 *
 * - Si un flag est inconnu → IllegalArgumentException
 * - Si une valeur est manquante ou de mauvais type → IllegalArgumentException
 * - Si toutes les validations passent → retourne un objet MazeConfig
 *
 * SÉPARATION DES RESPONSABILITÉS :
 * Cette classe fait UNIQUEMENT le parsing et la validation.
 * La génération du labyrinthe est dans MazeRunner.
 *
 * Usage :
 * MazeConfig config = ArgsParser.parse(args);
 *
 * Arguments supportés (tous optionnels) :
 * --width <int> Largeur du labyrinthe (défaut : 28)
 * --height <int> Hauteur du labyrinthe (défaut : 31)
 * --seed <long> Graine aléatoire (défaut : tirage aléatoire)
 * --algo <string> Algorithme (défaut : sidewinder)
 * --odd <int> Biais horizontal en % (défaut : 45, entre 0 et 100)
 * --e <int> Longueur max d'un run (défaut : 5, >= 1)
 * --out <path> Fichier de sortie JSON (défaut : affichage terminal)
 * --help Affiche ce manuel et quitte
 */
public class ArgsParser {

    private static final String HELP_TEXT = "Usage: java -jar MazeApp.jar [OPTIONS]\n" +
            "\n" +
            "Options:\n" +
            "  --width  <int>    Largeur du labyrinthe        (défaut: 28)\n" +
            "  --height <int>    Hauteur du labyrinthe        (défaut: 31)\n" +
            "  --seed   <long>   Graine pour la reproductibilité\n" +
            "  --algo   <string> Algorithme de génération     (défaut: sidewinder)\n" +
            "  --odd    <int>    Probabilité biais horizontal (défaut: 45, entre 0 et 100)\n" +
            "  --e      <int>    Longueur max des runs        (défaut: 5, >= 1)\n" +
            "  --out    <path>   Fichier de sortie JSON       (défaut: affichage terminal)\n" +
            "  --help            Affiche ce message\n" +
            "\n" +
            "Exemples:\n" +
            "  java -jar MazeApp.jar --width 28 --height 31 --seed 42\n" +
            "  java -jar MazeApp.jar --algo sidewinder --odd 60 --e 4 --out maze.json\n";

    /**
     * Parse le tableau d'arguments et retourne un MazeConfig valide.
     *
     * @param args arguments de la ligne de commande
     * @return configuration du labyrinthe
     * @throws IllegalArgumentException si un argument est invalide
     */
    public static MazeConfig parse(String[] args) {

        // Valeurs par défaut
        int width = 28;
        int height = 31;
        long seed = System.currentTimeMillis();
        boolean seedSet = false;
        String algo = "sidewinder";
        int odd = 45;
        int e = 5;
        String out = null;

        // Parcours des arguments par paires (flag → valeur)
        // Chaque case du switch lit le flag, puis avance i pour lire sa valeur.
        // requireNext() vérifie qu'il y a bien une valeur derrière le flag.
        int i = 0;
        while (i < args.length) {
            String flag = args[i];

            switch (flag) {
                case "--help":
                    // Afficher l'aide et quitter proprement (code 0 = succès)
                    System.out.println(HELP_TEXT);
                    System.exit(0);
                    break;

                case "--width":
                    requireNext(args, i, "--width");
                    width = parseInt(args[++i], "--width"); // ++i avance au mot suivant
                    break;

                case "--height":
                    requireNext(args, i, "--height");
                    height = parseInt(args[++i], "--height");
                    break;

                case "--seed":
                    requireNext(args, i, "--seed");
                    seed = parseLong(args[++i], "--seed");
                    seedSet = true; // on mémorise que l'utilisateur a fourni un seed
                    break;

                case "--algo":
                    requireNext(args, i, "--algo");
                    algo = args[++i]; // la validation du nom se fait plus bas
                    break;

                case "--odd":
                    requireNext(args, i, "--odd");
                    odd = parseInt(args[++i], "--odd");
                    break;

                case "--e":
                    requireNext(args, i, "--e");
                    e = parseInt(args[++i], "--e");
                    break;

                case "--out":
                    requireNext(args, i, "--out");
                    out = args[++i]; // chemin du fichier JSON de sortie
                    break;

                default:
                    // Flag non reconnu → on lève une exception qui sera capturée dans Main
                    throw new IllegalArgumentException(
                            "Argument inconnu : \"" + flag + "\". Utilisez --help pour la liste des options.");
            }
            i++;
        }

        // --- Validation des valeurs ---
        // On valide APRÈS le parsing complet pour pouvoir afficher des messages
        // d'erreur précis sur chaque paramètre.
        if (width <= 0) {
            throw new IllegalArgumentException("--width doit être > 0 (valeur reçue : " + width + ")");
        }
        if (height <= 0) {
            throw new IllegalArgumentException("--height doit être > 0 (valeur reçue : " + height + ")");
        }
        if (odd < 0 || odd > 100) {
            throw new IllegalArgumentException("--odd doit être entre 0 et 100 (valeur reçue : " + odd + ")");
        }
        if (e < 1) {
            throw new IllegalArgumentException("--e doit être >= 1 (valeur reçue : " + e + ")");
        }
        if (!algo.equalsIgnoreCase("sidewinder")) {
            // Pour ajouter un nouvel algorithme : l'ajouter ici ET dans MazeRunner.run()
            throw new IllegalArgumentException(
                    "Algorithme inconnu : \"" + algo + "\". Algorithmes disponibles : sidewinder");
        }

        // Toutes les validations passées → on construit la config
        return new MazeConfig(width, height, seed, seedSet, algo, odd, e, out);
    }

    // ---------------------------------------------------------------
    // Méthodes utilitaires privées
    // ---------------------------------------------------------------

    /** Vérifie qu'un flag a bien une valeur derrière lui. */
    private static void requireNext(String[] args, int i, String flag) {
        if (i + 1 >= args.length) {
            throw new IllegalArgumentException(
                    "Le flag " + flag + " attend une valeur après lui.");
        }
    }

    /** Parse un entier, avec message d'erreur contextualisé. */
    private static int parseInt(String value, String flag) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(
                    flag + " attend un entier, reçu : \"" + value + "\"");
        }
    }

    /** Parse un long, avec message d'erreur contextualisé. */
    private static long parseLong(String value, String flag) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(
                    flag + " attend un entier long, reçu : \"" + value + "\"");
        }
    }
}
