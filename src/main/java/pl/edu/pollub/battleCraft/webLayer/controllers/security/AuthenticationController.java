package pl.edu.pollub.battleCraft.webLayer.controllers.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.Security.AnyRoleNotFoundException;
import pl.edu.pollub.battleCraft.serviceLayer.services.security.utils.JWTTokenUtils;
import pl.edu.pollub.battleCraft.serviceLayer.services.security.data.User;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Security.AuthRequestDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.security.AuthResponseDTO;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("auth")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;

    private final JWTTokenUtils tokenUtils;

    private final UserDetailsService userDetailsService;

    @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager, JWTTokenUtils tokenUtils, UserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.tokenUtils = tokenUtils;
        this.userDetailsService = userDetailsService;
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

        String role = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).findFirst().orElseThrow(AnyRoleNotFoundException::new);

        // Return the token
        return ResponseEntity.ok(new AuthResponseDTO(token,role));
    }

    @RequestMapping(value = "refresh", method = RequestMethod.GET)
    public ResponseEntity<?> authenticationRequest(HttpServletRequest request) {
        String token = request.getHeader("X-Auth-Token");
        String username = this.tokenUtils.getUsernameFromToken(token);
        User user = (User) this.userDetailsService.loadUserByUsername(username);
        if (this.tokenUtils.canTokenBeRefreshed(token, user.getLastPasswordReset())) {
            String refreshedToken = this.tokenUtils.refreshToken(token);
            String role = user.getAuthorities().stream().map(GrantedAuthority::getAuthority).findFirst().orElseThrow(AnyRoleNotFoundException::new);
            return ResponseEntity.ok(new AuthResponseDTO(refreshedToken,role));
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
