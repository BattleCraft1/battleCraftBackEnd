package pl.edu.pollub.battleCraft.webLayer.controllers.restControllers.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.edu.pollub.battleCraft.serviceLayer.services.resources.interfaces.GameResourcesService;

import java.io.IOException;

@RestController
public class GameResourcesController {

    private final GameResourcesService gameResourcesService;

    @Autowired
    public GameResourcesController(GameResourcesService gameResourcesService) {
        this.gameResourcesService = gameResourcesService;
    }

    @PostMapping(value = "/upload/game/rules")
    public void uploadGameRules(@RequestParam("avatar") MultipartFile file, @RequestParam(value = "username") String gameName){
        gameResourcesService.saveGameRules(gameName,file);
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
