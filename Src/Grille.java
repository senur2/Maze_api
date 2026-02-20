    package Src;

    import java.util.ArrayList;
    import java.util.HashSet;
    import java.util.LinkedList;
    import java.util.List;
    import java.util.Queue;
    import java.util.Random;
    import java.util.Set;


    import javax.imageio.ImageIO;

    import java.awt.BasicStroke;
    import java.awt.Color;
    import java.awt.Graphics2D;
    import java.awt.image.BufferedImage;
    import java.io.File;

    // Générateur de labyrinthe basé sur l'algorithme Sidewinder,
    // adapté pour utiliser les Noeud de Proto (et non plus Cell de Proto2).
    public class Grille {

        private final int rows;//a supprimer
        private final int columns;//a suprimer
        // la structure du labyrinthe est portée uniquement par la grille de Noeud et leurs lien
        private final Noeud[][] grid; // grille interne de Noeud
        private final Random random = new Random();

        public Grille(int rows, int columns) {
            this.rows = rows;
            this.columns = columns;
            this.grid = prepareGrid();
            configureCells();
        }

        public void renderMaze(int cellSize) {
            int imgWidth = this.columns * cellSize;
            int imgHeight = this.rows * cellSize;

            // Création de l'image
            BufferedImage img = new BufferedImage(imgWidth + 1, imgHeight + 1, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = img.createGraphics();

            // Fond blanc
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, imgWidth + 1, imgHeight + 1);

            // Murs noirs
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(1));

            // Itération sur chaque cellule (Noeud) pour dessiner les murs
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < columns; c++) {
                    Noeud cell = grid[r][c];

                    int x1 = c * cellSize;
                    int y1 = r * cellSize;
                    int x2 = (c + 1) * cellSize;
                    int y2 = (r + 1) * cellSize;

                    Noeud north = get(r - 1, c);
                    Noeud south = get(r + 1, c);
                    Noeud west  = get(r, c - 1);
                    Noeud east  = get(r, c + 1);

                    // Si pas de voisin au nord, on trace le mur extérieur haut
                    if (north == null) {
                        g2d.drawLine(x1, y1, x2, y1);
                    }
                    // Si pas de voisin à l'ouest, on trace le mur extérieur gauche
                    if (west == null) {
                        g2d.drawLine(x1, y1, x1, y2);
                    }

                    // On trace le mur à l'EST si la cellule n'est pas liée à son voisin de droite
                    if (!isLinked(cell, east)) {
                        g2d.drawLine(x2, y1, x2, y2);
                    }
                    // On trace le mur au SUD si la cellule n'est pas liée à son voisin du bas
                    if (!isLinked(cell, south)) {
                        g2d.drawLine(x1, y2, x2, y2);
                    }
                }
            }

            g2d.dispose();

            try {
                ImageIO.write(img, "png", new File("maze.png"));
                System.out.println("Labyrinthe généré sous : maze.png");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Prépare une grille de Noeud
        protected Noeud[][] prepareGrid() {
            Noeud[][] cells = new Noeud[rows][columns];
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < columns; c++) {
                    cells[r][c] = new Noeud(0, r, c); // contenu initial à 0, avec coordonnées (r,c)
                }
            }
            return cells;
        }

        // Configure les voisins potentiels (N,E,S,O) pour chaque Noeud
        protected void configureCells() {
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < columns; c++) {
                    Noeud cell = grid[r][c];

                    if (r - 1 >= 0) cell.setVoisinPotentiel(0, grid[r - 1][c]); // nord
                    if (c + 1 < columns) cell.setVoisinPotentiel(1, grid[r][c + 1]); // est
                    if (r + 1 < rows) cell.setVoisinPotentiel(2, grid[r + 1][c]); // sud
                    if (c - 1 >= 0) cell.setVoisinPotentiel(3, grid[r][c - 1]); // ouest
                }
            }
        }

        public Noeud get(int row, int column) {
            if (row < 0 || row >= rows) return null;
            if (column < 0 || column >= columns) return null;
            return grid[row][column];
        }

        public Noeud randomCell() {
            int row = random.nextInt(rows);
            int col = random.nextInt(columns);
            return get(row, col);
        }

        public int size() {
            return rows * columns;
        }

        public int getRows() {
            return rows;
        }

        public int getColumns() {
            return columns;
        }

// ==========================================
    // 1. GÉNÉRATION DE BASE (SIDEWINDER)
    // ==========================================
    public void sidewing(int odd, int e) {
        Random rand = new Random();
        
        // Parcours de toutes les lignes
        for (int r = 0; r < rows; r++) {
            ArrayList<int[]> run = new ArrayList<>(); 
            
            // CRITIQUE : On parcourt la moitié GAUCHE pour que les couloirs 
            // puissent déborder vers la droite au niveau de la ligne médiane.
            for (int c = 0; c < columns / 2; c++) {
                Noeud currentCell = get(r, c);
                run.add(new int[]{r, c});
                
                // Si on est à la bordure de la moitié gauche, on force parfois 
                // la fermeture pour éviter un couloir central géant.
                boolean isAtMidline = (c == (columns / 2) - 1);
                boolean close = (rand.nextInt(100) < odd);
                if (isAtMidline && rand.nextBoolean()) close = true;

                if (close) {
                    int nbPassages = (int) Math.ceil((double) run.size() / e);
                    for (int i = 0; i < nbPassages; i++) {
                        if (run.isEmpty()) break;
                        int[] pickedCoords = run.remove(rand.nextInt(run.size()));
                        int pr = pickedCoords[0];
                        int pc = pickedCoords[1];

                        if (pr - 1 >= 0) {
                            Noeud picked = get(pr, pc);
                            Noeud north = get(pr - 1, pc);
                            linkVertical(north, picked); 

                            // MIROIR GAUCHE/DROITE : Le Nord reste au Nord.
                            int symColumns = (columns - 1) - pc;
                            Noeud symPicked = get(pr, symColumns);
                            Noeud symNorth = get(pr - 1, symColumns);
                            if (symPicked != null && symNorth != null) {
                                linkVertical(symNorth, symPicked);
                            }
                        }
                    }
                    run.clear();
                } else {
                    Noeud east = get(r, c + 1);
                    if (east != null) {
                        linkHorizontal(currentCell, east);

                        // MIROIR GAUCHE/DROITE : L'Est devient l'Ouest
                        int symColumns = (columns - 1) - c;
                        int symEastColumns = (columns - 1) - (c + 1); 
                        Noeud symCurrent = get(r, symColumns);
                        Noeud symEast = get(r, symEastColumns);
                        
                        if (symCurrent != null && symEast != null) {
                            // symEast est géométriquement à l'Ouest de symCurrent !
                            linkHorizontal(symEast, symCurrent); 
                        }
                    }
                }
            }
        }
    }

    // ==========================================
    // 2. TRESSAGE INTELLIGENT (SMART BRAID)
    // ==========================================
    public void smartBraid() {
        Random rand = new Random();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns / 2; c++) { // Uniquement la moitié Gauche
                Noeud current = get(r, c);
                if (current == null || current.getDegree() != 1) continue;

                ArrayList<Noeud> voisinsPotentiels = current.getVoisinsPotentiels();
                ArrayList<Noeud> candidats = new ArrayList<>();
                for (Noeud v : voisinsPotentiels) {
                    if (v != null && !isLinked(current, v)) {
                        candidats.add(v);
                    }
                }

                while (!candidats.isEmpty()) {
                    int idx = rand.nextInt(candidats.size());
                    Noeud voisinChoisi = candidats.remove(idx);

                    int dir = voisinsPotentiels.indexOf(voisinChoisi);
                    int nr = voisinChoisi.getRow();
                    int nc = voisinChoisi.getCol();

                    if (completesRoom(r, c, nr, nc)) continue;

                    // Création du lien normal
                    if (dir == 0) linkVertical(voisinChoisi, current);
                    else if (dir == 2) linkVertical(current, voisinChoisi);
                    else if (dir == 1) linkHorizontal(current, voisinChoisi);
                    else if (dir == 3) linkHorizontal(voisinChoisi, current);

                    // MIROIR GAUCHE/DROITE CORRECT
                    int symCurrentColumns = (columns - 1) - c;
                    int symNeighborColumns= (columns - 1) - nc;
                    Noeud symCurrent = get(r, symCurrentColumns);
                    Noeud symNeighbor = get(nr, symNeighborColumns);

                    if (symCurrent != null && symNeighbor != null) {
                        if (dir == 0) linkVertical(symNeighbor, symCurrent);      // Nord reste Nord
                        else if (dir == 2) linkVertical(symCurrent, symNeighbor); // Sud reste Sud
                        else if (dir == 1) linkHorizontal(symNeighbor, symCurrent); // Est devient Ouest
                        else if (dir == 3) linkHorizontal(symCurrent, symNeighbor); // Ouest devient Est
                    }
                    break;
                }
            }
        }
    }

    // ==========================================
    // 3. CASSURE DES COULOIRS & EMBRANCHEMENT
    // ==========================================
    public void breakLongCorridors(int maxLength) {
        Set<Noeud> visited = new HashSet<>();

        // CORRECTION DE LA BOUCLE : r pour rows, c pour la moitié de columns
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns / 2; c++) { 
                Noeud startNode = get(r, c);

                if (startNode != null && startNode.getDegree() == 2 && !visited.contains(startNode)) {
                    List<Noeud> corridor = extractFullCorridor(startNode, visited);
                    if (corridor.size() > maxLength) {
                        breakCorridor(corridor);
                    }
                }
            }
        }
    }
        // Renvoie vrai si deux Noeud sont déjà liés (via leur liste de voisins)
        private boolean isLinked(Noeud a, Noeud b) {
            if (a == null || b == null) return false;
            return a.getVoisins().contains(b);
        }

        // Lie deux noeuds verticalement (nord/sud)
        private void linkVertical(Noeud north, Noeud south) {
            if (north == null || south == null) return;
            north.ajouterVoisin(2, south); // sud de north
            south.ajouterVoisin(0, north); // nord de south
        }

        // Lie deux noeuds horizontalement (ouest/est)
        private void linkHorizontal(Noeud west, Noeud east) {
            if (west == null || east == null) return;
            west.ajouterVoisin(1, east); // est de west
            east.ajouterVoisin(3, west); // ouest de east
        }

        // Vérifie si lier les cellules (ar,ac) et (br,bc) créerait une pièce 2x2 ouverte
        private boolean completesRoom(int ar, int ac, int br, int bc) {
            if (ac == bc) {
                // Lien vertical (Nord/Sud)
                return checkSide(ar, ac, br, bc, -1) || checkSide(ar, ac, br, bc, 1);
            } else {
                // Lien horizontal (Est/Ouest)
                return checkAboveBelow(ar, ac, br, bc, -1) || checkAboveBelow(ar, ac, br, bc, 1);
            }
        }

        private boolean checkSide(int ar, int ac, int br, int bc, int offset) {
            Noeud a = get(ar, ac);
            Noeud b = get(br, bc);
            if (a == null || b == null) return false;

            Noeud aSide = get(ar, ac + offset);
            Noeud bSide = get(br, bc + offset);
            if (aSide == null || bSide == null) return false;

            // Un carré 2x2 se forme si ces 3 liens existent déjà
            return isLinked(a, aSide) && isLinked(b, bSide) && isLinked(aSide, bSide);
        }

        private boolean checkAboveBelow(int ar, int ac, int br, int bc, int offset) {
            Noeud a = get(ar, ac);
            Noeud b = get(br, bc);
            if (a == null || b == null) return false;

            Noeud aVert = get(ar + offset, ac);
            Noeud bVert = get(br + offset, bc);
            if (aVert == null || bVert == null) return false;

            return isLinked(a, aVert) && isLinked(b, bVert) && isLinked(aVert, bVert);
        }
        
        /**
         * Extrait la liste ordonnée des nœuds constituant un couloir.
         */
        private List<Noeud> extractFullCorridor(Noeud start, Set<Noeud> visited) {
            List<Noeud> corridor = new ArrayList<>();
            Queue<Noeud> queue = new LinkedList<>();
            
            queue.offer(start);
            visited.add(start);

            while (!queue.isEmpty()) {
                Noeud current = queue.poll();
                corridor.add(current);

                for (Noeud neighbor : current.getVoisins()) {
                    if (neighbor != null && neighbor.getDegree() == 2 && !visited.contains(neighbor)) {
                        visited.add(neighbor);
                        queue.offer(neighbor);
                    }
                }
            }
            return corridor;
        }

        /**
         * Tente de briser un couloir en connectant un de ses nœuds (idéalement au milieu) à un voisin.
         */
        private void breakCorridor(List<Noeud> corridor) {
            // Pour éviter de briser aux extrémités (ce qui laisserait un couloir encore trop long),
            // on cherche un candidat en partant du milieu du couloir.
            int middleIndex = corridor.size() / 2;

            // On va tester les nœuds en s'éloignant du centre vers les bords
            for (int offset = 0; offset <= middleIndex; offset++) {
                
                // On teste d'abord le nœud à middleIndex + offset, puis middleIndex - offset
                int[] indicesToTest = (offset == 0) ? new int[]{middleIndex} : new int[]{middleIndex + offset, middleIndex - offset};

                for (int idx : indicesToTest) {
                    if (idx < 0 || idx >= corridor.size()) continue;
                    
                    Noeud target = corridor.get(idx);
                    if (attemptToBranchOut(target)) {
                        return; // Succès : le couloir est brisé, on s'arrête là pour cette séquence
                    }
                }
            }
        }

        /**
         * Tente de créer un nouveau lien depuis le nœud ciblé vers un voisin non connecté.
         * Applique la même logique de vérification (2x2) et de symétrie que smartBraid.
         * @return true si un lien a pu être créé, false sinon.
         */
        private boolean attemptToBranchOut(Noeud current) {
            int r = current.getRow(); 
            int c = current.getCol(); 

            ArrayList<Noeud> voisinsPotentiels = current.getVoisinsPotentiels();
            
            for (Noeud voisinCandidat : voisinsPotentiels) {
                if (voisinCandidat != null && !isLinked(current, voisinCandidat)) {
                    
                    int dir = voisinsPotentiels.indexOf(voisinCandidat); // 0=nord, 1=est, 2=sud, 3=ouest
                    int nr = r, nc = c;
                    
                    if (dir == 0) nr = r - 1;
                    else if (dir == 1) nc = c + 1;
                    else if (dir == 2) nr = r + 1;
                    else if (dir == 3) nc = c - 1;

                    if (nr < 0 || nr >= rows || nc < 0 || nc >= columns) continue;

                    // Vérification anti zone 2x2
                    if (completesRoom(r, c, nr, nc)) continue;

                    // Application du lien principal
                    if (dir == 0) linkVertical(voisinCandidat, current);
                    else if (dir == 2) linkVertical(current, voisinCandidat);
                    else if (dir == 1) linkHorizontal(current, voisinCandidat);
                    else if (dir == 3) linkHorizontal(voisinCandidat, current);

                    int symCurrentColumns = (columns - 1) - c;
                    int symNeighborColumns= (columns - 1) - nc;
                    Noeud symCurrent = get(r, symCurrentColumns);
                    Noeud symNeighbor = get(nr, symNeighborColumns);

                    if (symCurrent != null && symNeighbor != null) {
                        if (dir == 0) linkVertical(symCurrent, symNeighbor);
                        else if (dir == 2) linkVertical(symNeighbor, symCurrent);
                        else if (dir == 1) linkHorizontal(symCurrent, symNeighbor);
                        else if (dir == 3) linkHorizontal(symNeighbor, symCurrent);
                    }

                    return true; // Embranchement réussi
                }
            }
            return false; // Impossible de créer un lien sans enfreindre les règles
        }

        // Détruit le lien vertical entre le noeud nord et le noeud sud
    private void removeVerticalLink(Noeud north, Noeud south) {
        if (north == null || south == null) return;
        north.retirerVoisin(2); // Retire le sud pour le noeud nord
        south.retirerVoisin(0); // Retire le nord pour le noeud sud
    }

    // Détruit le lien horizontal entre le noeud ouest et le noeud est
    private void removeHorizontalLink(Noeud west, Noeud east) {
        if (west == null || east == null) return;
        west.retirerVoisin(1); // Retire l'est pour le noeud ouest
        east.retirerVoisin(3); // Retire l'ouest pour le noeud est
    }


    /**
     * Écrase la topologie générée pour imposer une salle 6x6 vide au centre,
     * entourée de murs, avec des entrées symétriques.
     */
    public void carveCentralRoom() {
        // Définition des frontières de la salle 6x6
        int startR = (rows / 2) - 3;
        int endR = startR + 3;
        
        int startC = (columns / 2) - 3;
        int endC = startC + 6;

        // ÉTAPE 1 : Vider la salle (lier toutes les cases internes entre elles)
        for (int r = startR; r < endR; r++) {
            for (int c = startC; c < endC; c++) {
                Noeud current = get(r, c);
                if (current == null) continue;

                // On lie vers l'Est, sauf si on est sur la bordure droite de la salle
                if (c < endC - 1) {
                    linkHorizontal(current, get(r, c + 1));
                }
                // On lie vers le Sud, sauf si on est sur la bordure basse de la salle
                if (r < endR - 1) {
                    linkVertical(current, get(r + 1, c));
                }
            }
        }

        // ÉTAPE 2 : Construire le mur d'enceinte (couper tous les liens vers l'extérieur)
        // Scellement des murs Nord et Sud
        for (int c = startC; c < endC; c++) {
            removeVerticalLink(get(startR - 1, c), get(startR, c)); // Scelle le Nord
            removeVerticalLink(get(endR - 1, c), get(endR, c));     // Scelle le Sud
        }
        
        // Scellement des murs Ouest et Est
        for (int r = startR; r < endR; r++) {
            removeHorizontalLink(get(r, startC - 1), get(r, startC)); // Scelle l'Ouest
            removeHorizontalLink(get(r, endC - 1), get(r, endC));     // Scelle l'Est
        }

        // ÉTAPE 3 : Percer les portes pour maintenir la connexité du graphe
        // On perce une double porte au milieu du mur Ouest et du mur Est pour la symétrie
        int midR = startR + 2; 

        // Portes Ouest (gauche)
        linkHorizontal(get(midR, startC - 1), get(midR, startC));
        linkHorizontal(get(midR + 1, startC - 1), get(midR + 1, startC));

        // Portes Est (droite)
        linkHorizontal(get(midR, endC - 1), get(midR, endC));
        linkHorizontal(get(midR + 1, endC - 1), get(midR + 1, endC));
    }

    }