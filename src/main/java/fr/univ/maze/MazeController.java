package fr.univ.maze;

import fr.univ.maze.core.MazeConfig;
import fr.univ.maze.core.MazeRunner;
import fr.univ.maze.dto.MazeDTO;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MazeController {

    @GetMapping(value = "/api/maze", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getMaze(
            @RequestParam(defaultValue = "28") int width,
            @RequestParam(defaultValue = "31") int height,
            @RequestParam(required = false) Long seed,
            @RequestParam(defaultValue = "sidewinder") String algo,
            @RequestParam(defaultValue = "45") int odd,
            @RequestParam(defaultValue = "5") int e) {

        try {
            // 1. Gérer la graine (seed)
            boolean seedSet = (seed != null);
            long actualSeed = seedSet ? seed : System.currentTimeMillis();

            // 2. Créer l'objet de configuration
            MazeConfig config = new MazeConfig(width, height, actualSeed, seedSet, algo, odd, e, null);

            // 3. DÉLÉGUER AU RUNNER (Fin du doublon !)
            MazeDTO response = MazeRunner.generateMaze(config);

            // 4. Renvoyer le résultat
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException ex) {
            // Si le Runner lève une erreur (ex: algo inconnu), on renvoie une erreur HTTP 400
            return ResponseEntity.badRequest().body("{\"error\": \"" + ex.getMessage() + "\"}");
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body("{\"error\": \"Erreur interne du serveur\"}");
        }
    }
}