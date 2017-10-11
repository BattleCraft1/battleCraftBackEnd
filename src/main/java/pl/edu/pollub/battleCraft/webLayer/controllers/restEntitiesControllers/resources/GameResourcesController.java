package pl.edu.pollub.battleCraft.webLayer.controllers.restEntitiesControllers.resources;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.pollub.battleCraft.serviceLayer.services.resources.interfaces.GameResourcesService;

@RestController
public class GameResourcesController {

    private final GameResourcesService gameResourcesService;

    public GameResourcesController(GameResourcesService gameResourcesService) {
        this.gameResourcesService = gameResourcesService;
    }

    @GetMapping(value = "/get/game/{gameName}/rules")
    public ResponseEntity<Resource> downloadFile(@PathVariable String gameName){
        Resource file = gameResourcesService.getGameRules(gameName);
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+file.getFilename())
                .body(file);
    }
}
