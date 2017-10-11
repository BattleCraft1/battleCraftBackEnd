package pl.edu.pollub.battleCraft.webLayer.controllers.restEntitiesControllers.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.pollub.battleCraft.serviceLayer.services.resources.interfaces.UserAccountResourcesService;

import java.io.IOException;

@RestController
public class UserResourcesController {

    private final UserAccountResourcesService userAccountResourcesService;

    @Autowired
    public UserResourcesController(UserAccountResourcesService userAccountResourcesService) {
        this.userAccountResourcesService = userAccountResourcesService;
    }

    @GetMapping("/get/user/{name}/avatar")
    public ResponseEntity<byte[]> getUserAvatarWeb(@PathVariable String name) throws IOException {
        byte[] image = userAccountResourcesService.getUserAvatarWeb(name);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);
    }

    @GetMapping("/get/user/{name}/avatar/mobile")
    public ResponseEntity<byte[]> getUserAvatarMobile(@PathVariable String name) throws IOException {
        byte[] image = userAccountResourcesService.getUserAvatarMobile(name);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);
    }
}
