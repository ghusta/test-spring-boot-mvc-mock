package fr.husta.test.testspringbootmvcmock.api;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletPath;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = {PublicResource.class})
@Slf4j
class PublicResourceTest {

    @Autowired
    private Environment env;

    @Autowired
    private WebMvcProperties webMvcProperties;

    @Autowired
    private WebApplicationContext webApplicationContext;

    //     @Autowired
    private MockMvc mockMvc;

    /**
     * Should be configured by {@link org.springframework.boot.test.autoconfigure.web.servlet.MockMvcAutoConfiguration}
     */
    @Autowired
    private DispatcherServletPath dispatcherServletPath;

    private WebMvcProperties.Servlet webMvcServlet;

    @BeforeEach
    void setUp() {
        String propSpringMvcServletPath = env.getProperty("spring.mvc.servlet.path", "<MISSING>");
        log.debug("Property 'spring.mvc.servlet.path' = {}", propSpringMvcServletPath);
        log.debug("Auto-configuration              : Path = {}",
                dispatcherServletPath.getPath());
        log.debug("Auto-config, computed from Path : Prefix = {}; ServletUrlMapping = {}",
                dispatcherServletPath.getPrefix(), dispatcherServletPath.getServletUrlMapping());

        webMvcServlet = webMvcProperties.getServlet();

        // See answer : https://stackoverflow.com/a/32911575/1677594
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .defaultRequest(get("/").servletPath(dispatcherServletPath.getPath()))
                .apply(springSecurity())
                .build();
    }

    @Test
    void shouldGetGreetingsReturnOk() throws Exception {
        String springSecurityPattern = webMvcServlet.getPath("/public/**");
        log.debug("Pattern permitAll = {}", springSecurityPattern);
        String processedPath = webMvcServlet.getPath("/public/greetings");
        log.debug("URL = {}", processedPath);

        MvcResult mvcResult = mockMvc.perform(get(processedPath).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value("Hello !"))
                .andReturn();

        // Just to use AssertJ
        assertThat(mvcResult).isNotNull();
    }

    @Test
    void shouldGetGreetingsReturnOk_usingServletPath() throws Exception {
        if (webMvcServlet.getPath().equals("/")) {
            fail("NOT AVAILABLE");
        }

        // Try .servletPath("/prefix") (https://docs.spring.io/spring/docs/current/spring-framework-reference/testing.html#spring-mvc-test-server-performing-requests)
        // in that case, using servletPath() is superfluous
        mockMvc.perform(
                get(webMvcServlet.getPath("/public/greetings")).servletPath(webMvcServlet.getPath()).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value("Hello !"));
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
    @WithMockUser(username = "spring", roles = {"USER", "ADMIN"})
    void shouldGetGreetingsReturnOkBeingAuthenticated() throws Exception {
        mockMvc.perform(get(webMvcServlet.getPath("/public/greetings")).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(authenticated().withUsername("spring"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Hello !"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldGetUnknownUrlReturnNotFound() throws Exception {
        mockMvc.perform(get(webMvcServlet.getPath("/public/greetingxxxxxxxxxxxxx")))
                .andExpect(status().isNotFound());
    }

}