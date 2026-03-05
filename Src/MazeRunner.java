package Src;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Orchestre la génération du labyrinthe à partir d'un MazeConfig.
 *
 * Étapes :
 * 1. Crée la Grille avec seed
 * 2. Lance l'algorithme (sidewing + smartBraid)
 * 3. Génère le Labirinthe
 * 4. Calcule et affiche les métriques (Benchmark)
 * 5. Produit le JSON (via module externe) et l'écrit dans --out ou le terminal
 */
public class MazeRunner {

    public static void run(MazeConfig config) {

        // ── 1. Affichage de la configuration ──────────────────────────
        System.out.println("=== Génération du labyrinthe ===");
        System.out.println("Dimensions : " + config.width + " x " + config.height);
        System.out.println("Algorithme : " + config.algo);
        System.out.println("Seed       : " + config.seed
                + (config.seedSet ? "" : " (aléatoire)"));
        System.out.println("Paramètres : odd=" + config.odd + ", e=" + config.e);
        System.out.println();

        // ── 2. Création de la grille (avec seed pour reproductibilité) ─
        Grille grille = new Grille(config.height, config.width, config.seed);

        // ── 3. Génération selon l'algorithme ──────────────────────────
        switch (config.algo.toLowerCase()) {
            case "sidewinder":
                grille.sidewing(config.odd, config.e);
                    grille.smartBraid();
                    grille.breakLongCorridors(2);
                    grille.carveCentralRoom();
                    grille.renderMaze(20);
                    break;
                default:
                    // Déjà validé dans ArgsParser, mais sécurité défensive
                throw new IllegalArgumentException("Algorithme non supporté : " + config.algo);
        }

        // ── 4. Construction du Labyrinthe ─────────────────────────────
        Utilitaire.genererLabirinthe(grille);

        // ── 5. Métriques ──────────────────────────────────────────────
        Benchmark benchmark = new Benchmark(grille);
        benchmark.print();

        // ── 6. Export JSON ────────────────────────────────────────────
        //
        // ╔══════════════════════════════════════════════════════════════╗
        // ║ POINT D'INTEGRATION POUR LE MODULE JSON (collègue) ║
        // ╠══════════════════════════════════════════════════════════════╣
        // ║ Remplace l'appel à buildJson(config, grille) ci-dessous ║
        // ║ par l'appel à ta propre méthode de sérialisation JSON. ║
        // ║ ║
        // ║ Ce que tu as à disposition ici : ║
        // ║ - grille : objet Grille (lignes, colonnes, liens) ║
        // ║ - config : paramètres CLI (width, height, seed, algo…) ║
        // ║ ║
        // ║ Ce que ta méthode doit retourner : un String JSON valide ║
        // ║ ║
        // ║ Exemple de remplacement : ║
        // ║ String json = MonModuleJson.serialiser(grille, config); ║
        // ║ ║
        // ║ Ne pas modifier writeToFile() ni la logique --out, ║
        // ║ juste remplacer l'appel à buildJson() ci-dessous. ║
        // ╚══════════════════════════════════════════════════════════════╝
        String json = buildJson(config, grille); // ← REMPLACER CET APPEL

        if (config.out != null) {
            writeToFile(config.out, json);
        } else {
            System.out.println("=== JSON ===");
            System.out.println(json);
        }
    }

    // ----------------------------------------------------------------
    // Construction du JSON sans bibliothèque externe
    // ----------------------------------------------------------------

    /**
     * [PROVISOIRE] Sérialisation JSON interne — sans bibliothèque externe.
     *
     * Cette méthode est un PLACEHOLDER en attendant le module JSON du collègue.
     * Elle produit un JSON valide de la forme :
     *
     * {
     * "width": 28, "height": 31, "seed": 42, "algo": "sidewinder",
     * "cells": [
     * {"row": 0, "col": 0, "neighbors": [{"row":1,"col":0}]},
     * ...
     * ]
     * }
     *
     * ──────────────────────────────────────────────────────────────────
     * COLLÈGUE (module JSON) : tu peux SUPPRIMER cette méthode entière
     * et remplacer son appel dans run() par ta propre implémentation.
     * La signature attendue est :
     *
     * public static String serialiser(Grille grille, MazeConfig config)
     *
     * Le reste du code (writeToFile, gestion --out) ne change pas.
     * ──────────────────────────────────────────────────────────────────
     */
    private static String buildJson(MazeConfig config, Grille grille) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"width\": ").append(config.width).append(",\n");
        sb.append("  \"height\": ").append(config.height).append(",\n");
        sb.append("  \"seed\": ").append(config.seed).append(",\n");
        sb.append("  \"algo\": \"").append(config.algo).append("\",\n");
        sb.append("  \"odd\": ").append(config.odd).append(",\n");
        sb.append("  \"e\": ").append(config.e).append(",\n");
        sb.append("  \"cells\": [\n");

        int rows = grille.getRows();
        int cols = grille.getColumns();
        boolean firstCell = true;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Noeud cell = grille.get(r, c);
                if (cell == null)
                    continue;

                if (!firstCell)
                    sb.append(",\n");
                firstCell = false;

                sb.append("    {");
                sb.append("\"row\": ").append(r);
                sb.append(", \"col\": ").append(c);
                sb.append(", \"neighbors\": [");

                // Voisins liés (passages ouverts)
                boolean firstNeighbor = true;
                int[][] directions = { { -1, 0 }, { 0, 1 }, { 1, 0 }, { 0, -1 } };
                for (int[] dir : directions) {
                    int nr = r + dir[0];
                    int nc = c + dir[1];
                    Noeud neighbor = grille.get(nr, nc);
                    if (neighbor != null && cell.getVoisins().contains(neighbor)) {
                        if (!firstNeighbor)
                            sb.append(", ");
                        sb.append("{\"row\": ").append(nr)
                                .append(", \"col\": ").append(nc).append("}");
                        firstNeighbor = false;
                    }
                }
                sb.append("]}");
            }
        }

        sb.append("\n  ]\n}");
        return sb.toString();
    }

    /** Écrit le contenu JSON dans un fichier texte. */
    private static void writeToFile(String path, String content) {
        try (FileWriter fw = new FileWriter(path)) {
            fw.write(content);
            System.out.println("JSON écrit dans : " + path);
        } catch (IOException e) {
            System.err.println("Erreur lors de l'écriture du fichier \"" + path + "\" : " + e.getMessage());
            System.exit(2);
        }
    }
}
