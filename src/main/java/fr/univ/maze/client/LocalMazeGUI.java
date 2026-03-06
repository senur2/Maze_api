package fr.univ.maze.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.univ.maze.dto.MazeDTO;

public class LocalMazeGUI {

    public static void main(String[] args) {
        String apiUrl = "http://localhost:8080/api/maze?width=28&height=31";

        try {
            System.out.println("Demande du labyrinthe au serveur cloud...");

            // 2. Création du client HTTP et de la requête
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .GET()
                    .build();

            // 3. Envoi de la requête et récupération de la réponse JSON
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String jsonResult = response.body();
                System.out.println("Labyrinthe reçu ! Taille du JSON : " + jsonResult.length() + " caractères.");
                ObjectMapper mapper = new ObjectMapper();
                MazeDTO mazeData = mapper.readValue(jsonResult, MazeDTO.class);
                // 4. Affichage du labyrinthe à l'aide de MazeRenderer
                MazeRenderer.renderMazeFromJson(mazeData, 20, "maze_output.png");
                System.out.println("Labyrinthe rendu et sauvegardé sous 'maze_output.png'.");

            } else {
                System.err.println("Erreur du serveur : " + response.statusCode());
            }

        } catch (Exception e) {
            System.err.println("Impossible de contacter le serveur : " + e.getMessage());
        }
    }
}