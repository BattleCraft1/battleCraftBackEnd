package pl.edu.pollub.battleCraft.webLayer.controllers.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.edu.pollub.battleCraft.serviceLayer.services.resources.UserAccountResourcesService;

import java.io.IOException;

@RestController
public class UserResourcesController {

    private final UserAccountResourcesService userAccountResourcesService;

    @Autowired
    public UserResourcesController(UserAccountResourcesService userAccountResourcesService) {
        this.userAccountResourcesService = userAccountResourcesService;
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_ORGANIZER','ROLE_ACCEPTED')")
    @PostMapping(value = "/upload/user/avatar")
    public void uploadUserAvatar(@RequestParam("avatar") MultipartFile file, @RequestParam(value = "username") String username) throws IOException {
        userAccountResourcesService.saveUserAvatar(username,file);
    }

    @GetMapping("/get/user/avatar")
    public ResponseEntity<byte[]> getUserAvatarWeb(@RequestParam(value = "username") String name) throws IOException {
        System.out.println("try to get avatar");
        byte[] image = userAccountResourcesService.getUserAvatar(name);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);
    }
}
