package com.example.superdive.backend.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.example.superdive.backend.entity.User;
import com.example.superdive.backend.repository.UserRepository;

/**
 * Resolves the currently authenticated {@link User} from the JWT security context.
 * The JWT filter stores the user's email as the authentication name.
 */
@Component
public class AuthenticatedUserProvider {

	private final UserRepository userRepository;

	public AuthenticatedUserProvider(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public User getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null
				|| !authentication.isAuthenticated()
				|| "anonymousUser".equals(authentication.getPrincipal())) {
			throw new IllegalStateException("No authenticated user found in the security context");
		}

		String email = authentication.getName();
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("Authenticated user not found: " + email));
	}
}
