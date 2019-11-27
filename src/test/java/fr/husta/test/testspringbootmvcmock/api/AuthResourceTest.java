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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = {AuthResource.class})
@Slf4j
class AuthResourceTest {

    @Autowired
    private Environment env;

    @Autowired
    private WebMvcProperties webMvcProperties;

    @Autowired
    private WebApplicationContext webApplicationContext;

    // @Autowired
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
        AuthResourceTest.log.debug("Auto-configuration              : Path = {}", dispatcherServletPath.getPath());

        webMvcServlet = webMvcProperties.getServlet();

        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .defaultRequest(get("/").servletPath(dispatcherServletPath.getPath()))
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(username = "martin")
    void shouldGetWhoamiReturnOk() throws Exception {
        mockMvc.perform(get(webMvcServlet.getPath("/auth/whoami")).accept(MediaType.APPLICATION_JSON))
                .andDo(log())
                .andExpect(authenticated().withUsername("martin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("martin"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

}