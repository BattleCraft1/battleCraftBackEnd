package pl.edu.pollub.battleCraft.webLayer.controllers.restEntitiesControllers.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.edu.pollub.battleCraft.serviceLayer.services.resources.interfaces.UserAccountResourcesService;

import java.io.IOException;

@RestController
public class UserResourcesController {

    private final UserAccountResourcesService userAccountResourcesService;

    @Autowired
    public UserResourcesController(UserAccountResourcesService userAccountResourcesService) {
        this.userAccountResourcesService = userAccountResourcesService;
    }

    @PostMapping(value = "/upload/user/avatar")
    public void uploadUserAvatar(@RequestParam("avatar") MultipartFile file, @RequestParam(value = "username") String username) throws IOException {
        userAccountResourcesService.saveUserAvatar(username,file);
    }

    @GetMapping("/get/user/{name}/avatar")
    public ResponseEntity<byte[]> getUserAvatarWeb(@PathVariable String name) throws IOException {
        byte[] image = userAccountResourcesService.getUserAvatarWeb(name);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);
    }

    @GetMapping("/get/user/{name}/big/avatar")
    public ResponseEntity<byte[]> getUserBigAvatarWeb(@PathVariable String name) throws IOException {
        byte[] image = userAccountResourcesService.getUserBigAvatar(name);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);
    }

    @GetMapping("/get/user/{name}/avatar/mobile")
    public ResponseEntity<byte[]> getUserAvatarMobile(@PathVariable String name) throws IOException {
        byte[] image = userAccountResourcesService.getUserAvatarMobile(name);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);
    }
}
