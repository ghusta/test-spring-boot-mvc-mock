package fr.husta.test.testspringbootmvcmock.api;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/private")
@Slf4j
@PreAuthorize("isAuthenticated()")
public class PrivateResource {

    @GetMapping(path = "bye", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity bye() {
        return ResponseEntity.ok("Good bye !");
    }

    @GetMapping(path = "users/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<User> getUser(@PathVariable("id") Long id) {
        User user = new User(id, "test", "bob");
        return ResponseEntity.ok(user);
    }

    /**
     * See Lombok Builder + Jackson : https://www.thecuriousdev.org/lombok-builder-with-jackson/
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class User {

        private Long id;
        private String login;
        private String name;

    }

}
