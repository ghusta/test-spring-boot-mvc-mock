package fr.husta.test.testspringbootmvcmock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private WebMvcProperties webMvcProperties;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        auth.inMemoryAuthentication()
                .passwordEncoder(encoder)
                .withUser("spring")
                .password(encoder.encode("secret"))
                .roles("USER")
                .and()
                .withUser("admin")
                .password(encoder.encode("admin"))
                .roles("USER", "ADMIN");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // will add optional prefix to request url
        WebMvcProperties.Servlet mvcServlet = webMvcProperties.getServlet();
        http
                .authorizeRequests()
                .antMatchers(mvcServlet.getPath("/public/**")).permitAll()
                .antMatchers(mvcServlet.getPath("/auth/**")).permitAll()
                .antMatchers(mvcServlet.getPath("/private/**")).authenticated()
                .anyRequest().denyAll()
                .and()
//                .anonymous().disable()
                .httpBasic();
    }

}
