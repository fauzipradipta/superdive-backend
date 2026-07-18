package com.example.superdive.backend.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.superdive.backend.dto.Request.LoginRequestDTO;
import com.example.superdive.backend.dto.Request.UserRequestDTO;
import com.example.superdive.backend.dto.Response.AuthResponseDTO;
import com.example.superdive.backend.dto.Response.UserResponseDTO;
import com.example.superdive.backend.entity.User;
import com.example.superdive.backend.exception.InvalidCredentialException;
import com.example.superdive.backend.exception.UserAlreadyExistException;
import com.example.superdive.backend.repository.UserRepository;
import com.example.superdive.backend.security.CustomUserDetailsService;
import com.example.superdive.backend.security.JwtService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final CustomUserDetailsService userDetailsService;

	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
			JwtService jwtService, CustomUserDetailsService userDetailsService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
		this.userDetailsService = userDetailsService;
	}

	public UserResponseDTO register(UserRequestDTO request) throws UserAlreadyExistException {
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new UserAlreadyExistException("Email " + request.getEmail() + " is already registered");
		}

		User user = new User();
		user.setFirstname(request.getFirstname());
		user.setLastname(request.getLastname());
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword()));

		User saved = userRepository.save(user);
		return toResponse(saved);
	}

	public AuthResponseDTO login(LoginRequestDTO request) throws InvalidCredentialException {
		User user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new InvalidCredentialException("Invalid email or password"));

		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new InvalidCredentialException("Invalid email or password");
		}

		UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
		String token = jwtService.generateToken(userDetails);

		return AuthResponseDTO.builder()
				.token(token)
				.type("Bearer")
				.user(toResponse(user))
				.build();
	}

	private UserResponseDTO toResponse(User user) {
		return UserResponseDTO.builder()
				.id(user.getId())
				.firstname(user.getFirstname())
				.lastname(user.getLastname())
				.email(user.getEmail())
				.build();
	}
}
