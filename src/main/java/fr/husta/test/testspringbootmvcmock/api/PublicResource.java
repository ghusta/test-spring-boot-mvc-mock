package fr.husta.test.testspringbootmvcmock.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/public")
@Slf4j
public class PublicResource {

    @GetMapping(path = "greetings", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity greetings() {
        return ResponseEntity.ok("Hello !");
    }

    @GetMapping(path = "error-500", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity error_500() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

}
