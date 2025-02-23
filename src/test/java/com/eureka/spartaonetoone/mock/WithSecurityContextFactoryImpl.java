package com.eureka.spartaonetoone.mock;

import com.eureka.spartaonetoone.user.domain.User;
import com.eureka.spartaonetoone.user.infrastructure.security.UserDetailsImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithSecurityContextFactoryImpl implements WithSecurityContextFactory<MockUser> {

	@Override
	public SecurityContext createSecurityContext(MockUser mockUser) {
		//        User user = User.admin();

		User user = User.create(mockUser.username(), mockUser.email(), mockUser.password(), "test", "1234", mockUser.role());
		UserDetailsImpl userDetails = new UserDetailsImpl(user);
		UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
			new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(usernamePasswordAuthenticationToken);
		return context;
	}
}









