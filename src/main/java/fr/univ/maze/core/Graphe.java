package fr.univ.maze.core;

import java.util.ArrayList;

public class Graphe { 
    private ArrayList<Noeud> listeDesNoeuds;
    private ArrayList<Arrete> listeDesArretes;

    public Graphe() {
        // valeurs par défaut : listes vides prêtes à l'emploi
        this.listeDesNoeuds = new ArrayList<>();
        this.listeDesArretes = new ArrayList<>();
    }

    public ArrayList<Noeud> getListeDesNoeuds() {
        return listeDesNoeuds;
    }

    public ArrayList<Arrete> getListeDesArretes() {
        return listeDesArretes;
    }

    public void ajouterNoeud(Noeud n) {
        if (n != null && !listeDesNoeuds.contains(n)) {
            listeDesNoeuds.add(n);
        }
    }

    public void ajouterArrete(Noeud a, Noeud b) {
        if (a == null || b == null || a == b) return;
        // arête non orientée (pour l'instant on ne gère pas la vraie direction
        // nord/est/sud/ouest ici, on stocke juste "un" lien dans la première
        // case libre de la liste des voisins)
        listeDesArretes.add(new Arrete(a, b));

        // place b dans le premier emplacement libre de a
        ArrayList<Noeud> voisinsA = a.getVoisins();
        for (int i = 0; i < 4; i++) {
            if (voisinsA.get(i) == null) {
                a.ajouterVoisin(i, b);
                break;
            }
        }

        // place a dans le premier emplacement libre de b
        ArrayList<Noeud> voisinsB = b.getVoisins();
        for (int i = 0; i < 4; i++) {
            if (voisinsB.get(i) == null) {
                b.ajouterVoisin(i, a);
                break;
            }
        }
    }

    public ArrayList<Noeud> voisins(Noeud n) {
        if (n == null) return new ArrayList<>();
        return n.getVoisins();
    }

    public ArrayList<Noeud> plusCourtChemin(Noeud start, Noeud goal) {
        ArrayList<Noeud> resultatVide = new ArrayList<>();
        if (start == null || goal == null) return resultatVide;

        // BFS using only ArrayList structures
        ArrayList<Noeud> queue = new ArrayList<>();
        int head = 0; // simulate queue with index
        ArrayList<Noeud> visited = new ArrayList<>();
        ArrayList<Noeud> children = new ArrayList<>();
        ArrayList<Noeud> parents = new ArrayList<>();

        queue.add(start);
        visited.add(start);

        boolean found = false;
        while (head < queue.size()) {
            Noeud cur = queue.get(head++);
            if (cur == goal) { found = true; break; }
            for (Noeud v : voisins(cur)) {
                if (!visited.contains(v)) {
                    visited.add(v);
                    queue.add(v);
                    children.add(v);
                    parents.add(cur);
                }
            }
        }

        if (!found) return resultatVide;

        // reconstruct path using children/parents lists
        ArrayList<Noeud> pathRev = new ArrayList<>();
        Noeud n = goal;
        while (n != null) {
            pathRev.add(n);
            int idx = children.indexOf(n);
            if (idx == -1) {
                n = null; // reached start (no parent recorded for it)
            } else {
                n = parents.get(idx);
            }
        }

        // reverse to get start -> goal without using Collections.reverse
        ArrayList<Noeud> path = new ArrayList<>();
        for (int i = pathRev.size() - 1; i >= 0; i--) {
            path.add(pathRev.get(i));
        }
        return path;
    }
}
