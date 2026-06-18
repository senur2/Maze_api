# Maze API - Spring Boot

## Description

Cette application est une API REST développée avec Spring Boot qui gère la génération et la résolution de labyrinthes. Elle est conçue pour servir de backend au projet Pacman IA/jeux, permettant de créer des mazes, de lancer des algorithmes de recherche de chemin et de persister les scores.

## Objectif

Fournir une base de services web pour l’exploration de labyrinthes et l’entraînement d’agents intelligents, avec un stockage de données flexible grâce à MongoDB et une architecture découpée en couches (contrôleur, service, repository).

## Fonctionnalités

- Génération de labyrinthes de tailles variées
- Algorithmes de recherche de chemin (A*, BFS, DFS)
- Exposition d’endpoints REST pour créer, lire et supprimer des labyrinthes
- Persistance des scores de parties
- Déploiement conteneuré via Docker

## Technologies

- **Java 17**
- **Spring Boot 3** (Spring Web, Spring Data MongoDB)
- **MongoDB** pour la persistance
- **Maven** pour la gestion du build
- **Docker** pour le packaging et le déploiement

## Installation

1. Cloner le dépôt :

```
git clone https://github.com/senur2/Maze_api.git
cd Maze_api
```

2. Sélectionner le bon profil d’exécution (dev/prod) dans `application.properties` ou via des variables d’environnement.
3. Exécuter l’application en local :

```
mvn spring-boot:run
```

ou via Docker :

```
docker build -t maze-api .
docker run -p 8080:8080 -e MONGODB_URI=... maze-api
```

## Utilisation

- L’API est accessible sur `http://localhost:8080`.
- Endpoints principaux :

  - `GET /api/mazes` – liste tous les labyrinthes
  - `POST /api/mazes` – crée un nouveau labyrinthe (paramètres: hauteur, largeur)
  - `GET /api/mazes/{id}/solve?algo=a*` – résolution du labyrinthe avec l’algorithme choisi

## Tests

Les tests unitaires et d’intégration se trouvent sous `src/test/java` et peuvent être lancés avec :

```
mvn test
```

## Architecture

Le projet suit une architecture en couches :

- **Controller** : gère les requêtes HTTP et la validation.
- **Service** : contient la logique métier (génération de labyrinthe, algorithmes de recherche de chemin).
- **Repository** : interface avec MongoDB pour la persistance.

## Contribution personnelle

J’ai participé à l’implémentation de l’API REST, à l’intégration de MongoDB via Spring Data, ainsi qu’au développement et à l’optimisation des algorithmes A* et BFS.

## Améliorations futures

- Ajout de nouveaux algorithmes de génération et de résolution de labyrinthes
- Mise en place d’une authentification JWT pour sécuriser les endpoints
- Graphique des scores via une interface web
