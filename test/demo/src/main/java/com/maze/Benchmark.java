package com.maze;

import java.util.*;

public class Benchmark {
    private final maze mazeInstance;

    public Benchmark(maze mazeInstance) {
        this.mazeInstance = mazeInstance;
    }

    public int mainConnectedComponentSize() {
        cell startCell = mazeInstance.get(0, 0);
        if (startCell == null) return 0;

        Set<cell> visited = new HashSet<>();
        Queue<cell> queue = new LinkedList<>();

        queue.offer(startCell);
        visited.add(startCell);
        int count = 0;

        while (!queue.isEmpty()) {
            cell current = queue.poll();
            count++;
            for (cell neighbor : current.links()) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.offer(neighbor);
                }
            }
        }
        return count;
    }

    public double mainConnectedComponentRatio() {
        return (double) mainConnectedComponentSize() / mazeInstance.size();
    }

    /**
     * Compte les cul-de-sac.
     * Le papier de recherche souligne que l'Improved Sidewinder réduit ce nombre[cite: 85, 123].
     */
    public int deadEndCount() {
        // On utilise un tableau à un seul élément pour contourner la restriction effective final des lambdas
        final int[] count = {0};
        mazeInstance.eachCell(cell -> {
            if (cell.links().size() == 1) {
                count[0]++;
            }
        });
        return count[0];
    }

    public boolean hasCycles() {
        Set<cell> visited = new HashSet<>();
        // On parcourt toutes les cellules au cas où le labyrinthe ne serait pas totalement connecté
        for (int r = 0; r < mazeInstance.getRows(); r++) {
            for (int c = 0; c < mazeInstance.getColumns(); c++) {
                cell start = mazeInstance.get(r, c);
                if (!visited.contains(start)) {
                    if (detectCycleDfs(start, null, visited)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean detectCycleDfs(cell current, cell parent, Set<cell> visited) {
        visited.add(current);

        for (cell neighbor : current.links()) {
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
        int rows = mazeInstance.getRows();
        int cols = mazeInstance.getColumns();

    // On parcourt la grille jusqu'à l'avant-dernière ligne/colonne
        for (int r = 0; r < rows - 1; r++) {
            for (int c = 0; c < cols - 1; c++) {
                cell a = mazeInstance.get(r, c);
                cell b = mazeInstance.get(r, c + 1);     // Est de A
                cell d = mazeInstance.get(r + 1, c);     // Sud de A
                cell eCell = mazeInstance.get(r + 1, c + 1); // Sud de B / Est de D

                if (a != null && b != null && d != null && eCell != null) {
                    // Vérification du carré 2x2 interconnecté
                    if (a.isLinked(b) && a.isLinked(d) && b.isLinked(eCell) && d.isLinked(eCell)) {
                        openZones++;
                    }
                }
            }
        }
    return openZones;
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
                if (startNode != null && startNode.getDegree() == 2 && !visitedCorridorCells.contains(startNode)) {
                    
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
                            if (neighbor != null && neighbor.getDegree() == 2 && !visitedCorridorCells.contains(neighbor)) {
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

    
    public void print() {
        System.out.println("=== Maze Metrics ===");
        System.out.println("Connected component size  : " + mainConnectedComponentSize());
        System.out.println("Connected component ratio : " + String.format("%.3f", mainConnectedComponentRatio()));
        System.out.println("Dead-end count            : " + deadEndCount());
        System.out.println("Has cycles                : " + hasCycles());
        System.out.println("Open zones (2x2)         : " + countOpenZones());
        System.out.println("Average corridor length   : " + String.format("%.2f", averageCorridorLength(3)));
        System.out.println();
    }
}