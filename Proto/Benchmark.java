package Proto;

import java.util.*;

public class Benchmark {
    private final Grille grille;

    public Benchmark(Grille grille) {
        this.grille = grille;
    }

    public int mainConnectedComponentSize() {
        Noeud startCell = grille.get(0, 0);
        if (startCell == null) return 0;

        Set<Noeud> visited = new HashSet<>();
        Queue<Noeud> queue = new LinkedList<>();

        queue.offer(startCell);
        visited.add(startCell);
        int count = 0;

        while (!queue.isEmpty()) {
            Noeud current = queue.poll();
            count++;
            for (Noeud neighbor : current.getVoisins()) {
                if (neighbor != null && !visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.offer(neighbor);
                }
            }
        }
        return count;
    }

    public double mainConnectedComponentRatio() {
        return (double) mainConnectedComponentSize() / grille.size();
    }

    /**
     * Compte les cul-de-sac.
     * Le papier de recherche souligne que l'Improved Sidewinder réduit ce nombre[cite: 85, 123].
     */
    public int deadEndCount() {
        int count = 0;
        int rows = grille.getRows();
        int cols = grille.getColumns();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Noeud cell = grille.get(r, c);
                if (cell == null) continue;

                int degree = 0;
                for (Noeud neighbor : cell.getVoisins()) {
                    if (neighbor != null) {
                        degree++;
                    }
                }
                if (degree == 1) {
                    count++;
                }
            }
        }
        return count;
    }

    public boolean hasCycles() {
        Set<Noeud> visited = new HashSet<>();
        // On parcourt toutes les cellules au cas où le labyrinthe ne serait pas totalement connecté
        for (int r = 0; r < grille.getRows(); r++) {
            for (int c = 0; c < grille.getColumns(); c++) {
                Noeud start = grille.get(r, c);
                if (start == null) continue;
                if (!visited.contains(start)) {
                    if (detectCycleDfs(start, null, visited)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean detectCycleDfs(Noeud current, Noeud parent, Set<Noeud> visited) {
        visited.add(current);

        for (Noeud neighbor : current.getVoisins()) {
            if (neighbor == null) continue;
            if (neighbor == parent) continue;

            if (visited.contains(neighbor)) {
                return true; // Cycle trouvé
            }

            if (detectCycleDfs(neighbor, current, visited)) {
                return true;
            }
        }
        return false;
    }


    public int countOpenZones() {
        int openZones = 0;
        int rows = grille.getRows();
        int cols = grille.getColumns();

    // On parcourt la grille jusqu'à l'avant-dernière ligne/colonne
        for (int r = 0; r < rows - 1; r++) {
            for (int c = 0; c < cols - 1; c++) {
                Noeud a = grille.get(r, c);
                Noeud b = grille.get(r, c + 1);     // Est de A
                Noeud d = grille.get(r + 1, c);     // Sud de A
                Noeud eCell = grille.get(r + 1, c + 1); // Sud de B / Est de D

                if (a != null && b != null && d != null && eCell != null) {
                    // Vérification du carré 2x2 interconnecté
                    if (isLinked(a, b) && isLinked(a, d) && isLinked(b, eCell) && isLinked(d, eCell)) {
                        openZones++;
                    }
                }
            }
        }
    return openZones;
    }

    // Vérifie si deux noeuds sont liés (voisins dans le graphe de la grille)
    private boolean isLinked(Noeud a, Noeud b) {
        if (a == null || b == null) return false;
        return a.getVoisins().contains(b);
    }


    public void print() {
        System.out.println("=== Maze Metrics ===");
        System.out.println("Connected component size  : " + mainConnectedComponentSize());
        System.out.println("Connected component ratio : " + String.format("%.3f", mainConnectedComponentRatio()));
        System.out.println("Dead-end count            : " + deadEndCount());
        System.out.println("Has cycles                : " + hasCycles());
        System.out.println("Open zones (2x2)         : " + countOpenZones());
        System.out.println();
    }
}