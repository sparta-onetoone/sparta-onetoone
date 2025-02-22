package com.eureka.spartaonetoone.user.infrastructure.security;

import com.eureka.spartaonetoone.user.domain.MockUser;
import com.eureka.spartaonetoone.user.domain.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithSecurityContextFactoryImpl implements WithSecurityContextFactory<MockUser> {

    @Override
    public SecurityContext createSecurityContext(MockUser mockUser) {
        User user = User.admin();
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(usernamePasswordAuthenticationToken);
        return context;
    }
}
