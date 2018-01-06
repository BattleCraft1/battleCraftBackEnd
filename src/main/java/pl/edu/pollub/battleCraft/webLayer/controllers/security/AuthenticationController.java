package pl.edu.pollub.battleCraft.webLayer.controllers.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import pl.edu.pollub.battleCraft.serviceLayer.services.security.AuthorityRecognizer;
import pl.edu.pollub.battleCraft.serviceLayer.services.security.PasswordService;
import pl.edu.pollub.battleCraft.serviceLayer.services.security.utils.JWTTokenUtils;
import pl.edu.pollub.battleCraft.serviceLayer.services.security.data.User;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Security.AuthRequestDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Security.ChangePasswordDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.UserAccount.Registration.EmailDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.security.AccountResponseDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.security.AuthResponseDTO;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("auth")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;

    private final JWTTokenUtils tokenUtils;

    private final UserDetailsService userDetailsService;

    private final AuthorityRecognizer roleRecognizer;

    private final PasswordService passwordService;

    @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager, JWTTokenUtils tokenUtils, UserDetailsService userDetailsService, AuthorityRecognizer roleRecognizer, PasswordService changePasswordService) {
        this.authenticationManager = authenticationManager;
        this.tokenUtils = tokenUtils;
        this.userDetailsService = userDetailsService;
        this.roleRecognizer = roleRecognizer;
        this.passwordService = changePasswordService;
    }

    @PostMapping
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
        return ResponseEntity.ok(
                new AuthResponseDTO(token, roleRecognizer.getCurrentUserRoleFromUserDetails(userDetails), userDetails.getUsername()));
    }

    @GetMapping(value = "refresh")
    public ResponseEntity<?> authenticationRequest(HttpServletRequest request) {
        String token = request.getHeader("X-Auth-Token");
        String username = this.tokenUtils.getUsernameFromToken(token);
        User user = (User) this.userDetailsService.loadUserByUsername(username);
        if (this.tokenUtils.canTokenBeRefreshed(token)) {
            String refreshedToken = this.tokenUtils.refreshToken(token);
            return ResponseEntity.ok(
                    new AuthResponseDTO(
                            refreshedToken,
                            roleRecognizer.getCurrentUserRoleFromContext(),
                            roleRecognizer.getCurrentUserNameFromContext()));
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ORGANIZER','ROLE_ADMIN','ROLE_ACCEPTED')")
    @PostMapping(value = "change/password")
    public void changePassword(@RequestBody ChangePasswordDTO changePasswordDTO) {
        passwordService.changePassword(changePasswordDTO);
    }

    @PreAuthorize("hasAnyRole('ROLE_ORGANIZER','ROLE_ADMIN','ROLE_ACCEPTED')")
    @GetMapping(value = "account")
    public AccountResponseDTO getAccountDetails() {
        return new AccountResponseDTO(
                roleRecognizer.getCurrentUserNameFromContext(),
                roleRecognizer.getCurrentUserRoleFromContext());
    }

    @PostMapping(value = "reset/password")
    @ResponseStatus(HttpStatus.OK)
    public void resetPassword(@RequestBody EmailDTO emailDTO) {
        passwordService.resetPassword(emailDTO);
    }
}
