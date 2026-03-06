# Jour 1 / 6 février:
Recherche d'algorithme de labyrinthe: Deux principal approche m'ont interessé L'approche "sidewinder" qui est unn des algorithme de labyritnhe le plus simple qui consiste a detruire des mure de maniére itérative, on le modifie legerement pour que le labyritnhe soit plus parfait en introduisant le parametre E qui detruitn en moyenne toute les E tuile le labyrinthe 
Aussi L'approche "tetris" qui consiste a poser des blocks prédéfinie a été étudié mais pas selectionner car d'autres groupe ont prit cette approche

# Jour 2 / 13 février:
Implémentation de l'algorithm sideWinder modifié, suite a des resultat décevant (beaucoup de dead end) un algorithme de braid (voir la fin du livre "maze for programmer") a été implementé qui "detruit" spécifiquement les dead end, cependant le braiding créer des probléme de zone "ouverte" l'implementation d'une 3em passe posant des mures a été donc néssecaire enfin les deux passe ont été fusionné dans l'algo smart braiding ! la conversion de l'algorithme dans les structure de donné faites par les autres membre du groupe a terminé la journée.

# Jour 3 / 20 février:
Finissement de l'algorithme avec l'implementation de la destruction des mures aprés verification de la taille de couloirs (si un couloire (definit comme un chemin unique de taille x) est trop grand il est detruit) et implementation du webservice sur render cela c'est faite en plusieur etape: creation d'un javaspring et d'un maven pour rendre le projet plus simple, creation d'un webservice avec l'application javasprint qui envoye le json en ligne, creation d'une gui local qui construit l'image a partire du json, remplacement du jso par des DTO plus compréhensible et standard et enfin creation de la dockerfil et publication sur render. 
# Jour 4 / 6 mars:
**

# Jour 5 / 13 mars:
**

# Jour 6 / 20 mars:
**
