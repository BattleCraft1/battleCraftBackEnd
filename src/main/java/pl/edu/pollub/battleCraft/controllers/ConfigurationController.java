package pl.edu.pollub.battleCraft.controllers;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.pollub.battleCraft.services.FileService;

@RestController
public class ConfigurationController {

    private final FileService fileService;

    public ConfigurationController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping("/config/{configFileName}")
    public ResponseEntity<Resource> getConfigOfTournaments(@PathVariable String configFileName){
        Resource file = fileService.loadAsResource("config-files/"+configFileName+".json");
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+file.getFilename()+"\"")
                .body(file);
    }
}
