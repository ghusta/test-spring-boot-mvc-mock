package fr.husta.test.testspringbootmvcmock.support;

import org.springframework.security.test.context.support.WithMockUser;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Test Meta Annotations.
 * <br/>
 * See : https://docs.spring.io/spring-security/site/docs/5.2.x/reference/html/test.html#test-method-meta-annotations
 */
@Retention(RetentionPolicy.RUNTIME)
@WithMockUser(value = "bob", roles = "ADMIN")
public @interface WithMockUserRoleAdmin {
}
