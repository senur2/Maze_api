package Src;

import java.util.ArrayList;

public class Noeud {
    private final int row;
    private final int col;

    private int contenu;
    // liste des voisins (adjacents) 0 = nord 1 = est 2 = sud 3 = ouest
    private ArrayList<Noeud> voisins = new ArrayList<>(4);
    // voisins potentiels : noeuds au nord/est/sud/ouest, qu'ils soient liés ou pas
    private ArrayList<Noeud> voisinsPotentiels = new ArrayList<>(4);

    public Noeud(int contenu, int row, int col) {
        this.row = row;
        this.col = col;
        this.contenu = contenu;
        // initialisation : 4 emplacements vides (null) pour voisins et voisinsPotentiels
        for (int i = 0; i < 4; i++) {
            voisins.add(null);
            voisinsPotentiels.add(null);
        }
    }

    public int getContenu() {
        return contenu;
    }

    public void setContenu(int contenu) {
        this.contenu = contenu;
    }

    // direction : 0 = nord, 1 = est, 2 = sud, 3 = ouest
    public void ajouterVoisin(int direction, Noeud voisin) {
        if (direction < 0 || direction > 3) {
            return; // direction invalide, on ne fait rien
        }
        if (voisin != null && voisin != this) {
            voisins.set(direction, voisin);
        }
    }

    // retire le voisin dans la direction donnée (le remet à null)
    public void retirerVoisin(int direction) {
        if (direction < 0 || direction > 3) {
            return; // direction invalide
        }
        voisins.set(direction, null);
    }

    public ArrayList<Noeud> getVoisins() {
        return voisins;
    }

    // direction : 0 = nord, 1 = est, 2 = sud, 3 = ouest
    public void setVoisinPotentiel(int direction, Noeud voisin) {
        if (direction < 0 || direction > 3) {
            return;
        }
        voisinsPotentiels.set(direction, voisin);
    }

    public ArrayList<Noeud> getVoisinsPotentiels() {
        return voisinsPotentiels;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    // Calcule le degré d'un nœud (nombre de voisins liés)
    public int getDegree() {
        int degree = 0;
        for (Noeud v : this.getVoisins()) {
            if (v != null) degree++;
        }
        return degree;
    }

}