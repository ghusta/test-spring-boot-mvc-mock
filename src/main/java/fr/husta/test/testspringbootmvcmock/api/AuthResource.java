package fr.husta.test.testspringbootmvcmock.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping(path = "/auth")
@Slf4j
public class AuthResource {

    @GetMapping(path = "whoami", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> whoami(@AuthenticationPrincipal(errorOnInvalidType = true) UserDetails userDetails) {
        String login = userDetails.getUsername();
        return ResponseEntity.ok(login);
    }

    @GetMapping(path = "is-admin", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> isAdmin(@CurrentSecurityContext(expression = "authentication") Authentication authentication) {
        boolean hasAdmin = authentication.getAuthorities()
                .stream()
                .filter(Objects::nonNull)
                .anyMatch(ga -> ga.getAuthority().equalsIgnoreCase("ROLE_ADMIN"));

        return ResponseEntity.ok(hasAdmin);
    }

}
