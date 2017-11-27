package pl.edu.pollub.battleCraft.webLayer.controllers.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.pollub.battleCraft.serviceLayer.services.security.AuthorityRecognizer;
import pl.edu.pollub.battleCraft.serviceLayer.services.security.ChangePasswordService;
import pl.edu.pollub.battleCraft.serviceLayer.services.security.utils.JWTTokenUtils;
import pl.edu.pollub.battleCraft.serviceLayer.services.security.data.User;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Security.AuthRequestDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Security.ChangePasswordDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.security.AuthResponseDTO;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("auth")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;

    private final JWTTokenUtils tokenUtils;

    private final UserDetailsService userDetailsService;

    private final AuthorityRecognizer roleRecognizer;

    private final ChangePasswordService changePasswordService;

    @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager, JWTTokenUtils tokenUtils, UserDetailsService userDetailsService, AuthorityRecognizer roleRecognizer, ChangePasswordService changePasswordService) {
        this.authenticationManager = authenticationManager;
        this.tokenUtils = tokenUtils;
        this.userDetailsService = userDetailsService;
        this.roleRecognizer = roleRecognizer;
        this.changePasswordService = changePasswordService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> authenticationRequest(@RequestBody AuthRequestDTO authenticationRequest)
            throws AuthenticationException {

        // Perform the authentication
        Authentication authentication = this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getUsername(),
                        authenticationRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Reload password post-authentication so we can generate token
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        String token = this.tokenUtils.generateToken(userDetails);

        // Return the token
        return ResponseEntity.ok(new AuthResponseDTO(token,roleRecognizer.getCurrentUserRoleFromUserDetails(userDetails)));
    }

    @RequestMapping(value = "refresh", method = RequestMethod.GET)
    public ResponseEntity<?> authenticationRequest(HttpServletRequest request) {
        String token = request.getHeader("X-Auth-Token");
        String username = this.tokenUtils.getUsernameFromToken(token);
        User user = (User) this.userDetailsService.loadUserByUsername(username);
        if (this.tokenUtils.canTokenBeRefreshed(token, user.getLastPasswordReset())) {
            String refreshedToken = this.tokenUtils.refreshToken(token);
            return ResponseEntity.ok(new AuthResponseDTO(refreshedToken,roleRecognizer.getCurrentUserRoleFromUserDetails(user)));
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ORGANIZER','ROLE_ADMIN','ROLE_ACCEPTED')")
    @RequestMapping(value = "change/password", method = RequestMethod.GET)
    public void changePassword(ChangePasswordDTO changePasswordDTO) {
        changePasswordService.changePassword(changePasswordDTO);
    }
}
