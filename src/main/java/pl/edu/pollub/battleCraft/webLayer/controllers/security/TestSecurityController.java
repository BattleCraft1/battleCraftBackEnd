package pl.edu.pollub.battleCraft.webLayer.controllers.security;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("protected")
public class TestSecurityController {

    @GetMapping(value = "/admin")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> checkIsAdmin() {
        return ResponseEntity.ok("{\"success\":true}");
    }

    @GetMapping(value = "/organizer")
    @PreAuthorize("hasAnyRole('ROLE_ORGANIZER')")
    public ResponseEntity<?> checkIsOrganizer() {
        return ResponseEntity.ok("{\"success\":true}");
    }

    @GetMapping(value = "/player")
    @PreAuthorize("hasAnyRole('ROLE_ACCEPTED')")
    public ResponseEntity<?> checkIsPlayer() {
        return ResponseEntity.ok("{\"success\":true}");
    }
}
