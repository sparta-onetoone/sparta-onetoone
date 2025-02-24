package com.eureka.spartaonetoone.mock;

import com.eureka.spartaonetoone.user.domain.UserRole;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithSecurityContextFactoryImpl.class)
public @interface MockUser {
    String id() default "17ff12b3-1ea7-4f1e-8d0a-ed36c82466f6";

    String username() default "mockUser";

    String password() default "mockPassword";

    String email() default "mock@email.com";

    UserRole role() default UserRole.CUSTOMER;
}
