package Src;

public class Arrete {
    private final Noeud n1;
    private final Noeud n2;

    public Arrete(Noeud n1, Noeud n2) {
        this.n1 = n1;
        this.n2 = n2;
    }

    public Noeud getN1() {
        return n1;
    }

    public Noeud getN2() {
        return n2;
    }
}
