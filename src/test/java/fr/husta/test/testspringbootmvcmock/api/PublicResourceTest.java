package fr.husta.test.testspringbootmvcmock.api;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletPath;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = {PublicResource.class})
@Slf4j
class PublicResourceTest {

    @Autowired
    private Environment env;

    @Autowired
    private MockMvc mockMvc;

    /**
     * Should be configured by {@link org.springframework.boot.test.autoconfigure.web.servlet.MockMvcAutoConfiguration}
     */
    @Autowired
    private DispatcherServletPath dispatcherServletPath;

    @BeforeEach
    void setUp() {
        String propSpringMvcServletPath = env.getProperty("spring.mvc.servlet.path", "<MISSING>");
        log.debug("Property 'spring.mvc.servlet.path' = {}", propSpringMvcServletPath);
        log.debug("Auto-configuration              : Path = {}",
                dispatcherServletPath.getPath());
        log.debug("Auto-config, computed from Path : Prefix = {}; ServletUrlMapping = {}",
                dispatcherServletPath.getPrefix(), dispatcherServletPath.getServletUrlMapping());
    }

    @Test
    void shouldGetGreetingsReturnOk() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/public/greetings"))
                .andExpect(status().isOk())
                .andReturn();

        // Just to use AssertJ
        assertThat(mvcResult).isNotNull();
    }

    @Test
    void shouldGetError500ReturnError() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/public/error-500"))
                .andExpect(status().is5xxServerError())
                .andReturn();

        // Just to use AssertJ
        assertThat(mvcResult).isNotNull();
    }

    @Test
    void shouldGetError500ThrowsReturnError() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/public/error-500-throws"))
                .andExpect(status().is5xxServerError())
                .andReturn();

        // Just to use AssertJ
        assertThat(mvcResult).isNotNull();
        assertThat(mvcResult.getResolvedException()).isNotNull();
    }

    @Test
    void shouldGetUnknownUrlReturnNotFound() throws Exception {
        mockMvc.perform(get("/public/greetingxxxxxxxxxxxxx"))
                .andExpect(status().isNotFound());
    }

}