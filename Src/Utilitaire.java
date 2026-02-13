package Src;

import java.util.ArrayList;

public class Utilitaire {

    // Construit un objet Labirinthe STRICTEMENT équivalent au Maze actuel.
    // On ne regénère PAS un nouveau labyrinthe : on copie simplement
    // la structure déjà encodée dans les Noeud (voisins) vers un Graphe.
    public static Labirinthe genererLabirinthe(Grille grille) {
        int rows = grille.getRows();
        int columns = grille.getColumns();

        Graphe graph = new Graphe();

        // 1) Ajouter au graphe tous les noeuds qui correspondent à des cases de passage
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                Noeud n = grille.get(r, c);
                if (!n.getVoisins().isEmpty()) {
                    graph.ajouterNoeud(n);
                }
            }
        }

        // 2) Ajouter les arretes du graphe en fonction des liaisons entre Noeud
        //    IMPORTANT : on ne modifie PAS les voisins des Noeud ici, on
        //    se contente de créer les objets Arrete dans le Graphe.
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                Noeud n = grille.get(r, c);
                // on ne crée des arretes qu'entre cases de passage (noeuds qui ont des voisins)
                if (n.getVoisins().isEmpty()) continue;

                // pour éviter les doublons, on ne regarde que l'est et le sud
                Noeud east = grille.get(r, c + 1);
                if (east != null && !east.getVoisins().isEmpty() && isLinked(n, east)) {
                    graph.getListeDesArretes().add(new Arrete(n, east));
                }

                Noeud south = grille.get(r + 1, c);
                if (south != null && !south.getVoisins().isEmpty() && isLinked(n, south)) {
                    graph.getListeDesArretes().add(new Arrete(n, south));
                }
            }
        }

        return new Labirinthe(graph, grille);
    }

    // Renvoie vrai si deux Noeud sont déjà liés (via leur liste de voisins)
    private static boolean isLinked(Noeud a, Noeud b) {
        if (a == null || b == null) return false;
        return a.getVoisins().contains(b);
    }
}