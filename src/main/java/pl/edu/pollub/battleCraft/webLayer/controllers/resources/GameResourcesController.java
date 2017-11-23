package pl.edu.pollub.battleCraft.webLayer.controllers.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.edu.pollub.battleCraft.serviceLayer.services.resources.GameResourcesService;

@RestController
public class GameResourcesController {

    private final GameResourcesService gameResourcesService;

    @Autowired
    public GameResourcesController(GameResourcesService gameResourcesService) {
        this.gameResourcesService = gameResourcesService;
    }

    @PostMapping(value = "/upload/game/rules")
    public void uploadGameRules(@RequestParam("gameRules") MultipartFile file, @RequestParam(value = "gameName") String gameName){
        gameResourcesService.saveGameRules(gameName,file);
    }

    @GetMapping(value = "/get/game/rules")
    public ResponseEntity<Resource> downloadFile(@RequestParam(value = "gameName") String gameName){
        Resource file = gameResourcesService.getGameRules(gameName);
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, new StringBuilder("attachment; filename=").append(file.getFilename()).toString())
                .body(file);
    }
}
