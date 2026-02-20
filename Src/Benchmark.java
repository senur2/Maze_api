package Src;

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


    private int getDegree(Noeud cell) {
        if (cell == null) return 0;
        int degree = 0;
        for (Noeud neighbor : cell.getVoisins()) {
            if (neighbor != null) {
                degree++;
            }
        }
        return degree;
    }

    /**
     * Calcule la longueur moyenne des couloirs ayant une taille supérieure ou égale à minLength.
     * Un couloir est défini comme une suite ininterrompue de cases ayant exactement 2 voisins.
     * * @param minLength La longueur minimale pour qu'un segment soit considéré comme un couloir (ex: 5).
     * @return La longueur moyenne des couloirs, ou 0.0 si aucun couloir ne correspond au critère.
     */
    public double averageCorridorLength(int minLength) {
        Set<Noeud> visitedCorridorCells = new HashSet<>();
        int totalLength = 0;
        int validCorridorCount = 0;

        int rows = grille.getRows();
        int cols = grille.getColumns();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Noeud startNode = grille.get(r, c);
                
                // On cherche un point de départ : un nœud existant, de degré 2, et non encore visité
                if (startNode != null && getDegree(startNode) == 2 && !visitedCorridorCells.contains(startNode)) {
                    
                    int currentCorridorLength = 0;
                    Queue<Noeud> queue = new LinkedList<>();
                    
                    // Démarrage du BFS pour explorer ce couloir spécifique
                    queue.offer(startNode);
                    visitedCorridorCells.add(startNode);

                    while (!queue.isEmpty()) {
                        Noeud current = queue.poll();
                        currentCorridorLength++;

                        for (Noeud neighbor : current.getVoisins()) {
                            // On continue l'exploration uniquement sur les voisins de degré 2 non visités
                            if (neighbor != null && getDegree(neighbor) == 2 && !visitedCorridorCells.contains(neighbor)) {
                                visitedCorridorCells.add(neighbor);
                                queue.offer(neighbor);
                            }
                        }
                    }

                    // Vérification du seuil critique demandé
                    if (currentCorridorLength >= minLength) {
                        totalLength += currentCorridorLength;
                        validCorridorCount++;
                    }
                }
            }
        }

        // Prévention de la division par zéro
        return validCorridorCount == 0 ? 0.0 : (double) totalLength / validCorridorCount;
    }

    // Version refactorisée et plus lisible de ton compteur de culs-de-sac
    public int deadEndCount() {
        int count = 0;
        for (int r = 0; r < grille.getRows(); r++) {
            for (int c = 0; c < grille.getColumns(); c++) {
                Noeud cell = grille.get(r, c);
                if (cell != null && getDegree(cell) == 1) {
                    count++;
                }
            }
        }
        return count;
    }

    // Mise à jour de l'affichage
    public void print() {
        System.out.println("=== Maze Metrics ===");
        System.out.println("Connected component size  : " + mainConnectedComponentSize());
        System.out.println("Connected component ratio : " + String.format("%.3f", mainConnectedComponentRatio()));
        System.out.println("Dead-end count            : " + deadEndCount());
        System.out.println("Has cycles                : " + hasCycles());
        System.out.println("Open zones (2x2)          : " + countOpenZones());
        
        // Nouvelle métrique (avec un seuil à 5 cases)
        System.out.println("Avg Corridor Length (>=5) : " + String.format("%.2f", averageCorridorLength(5)));
        System.out.println();
    }
}