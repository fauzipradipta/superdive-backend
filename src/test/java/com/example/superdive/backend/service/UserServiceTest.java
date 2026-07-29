package com.example.superdive.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

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

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private JwtService jwtService;

	@Mock
	private CustomUserDetailsService userDetailsService;

	private UserService userService;

	@BeforeEach
	void setUp() {
		userService = new UserService(userRepository, passwordEncoder, jwtService, userDetailsService);
	}

	// ---- register / signup ----

	@Test
	void register_savesNewUser_whenEmailNotTaken() throws UserAlreadyExistException {
		UserRequestDTO request = UserRequestDTO.builder()
				.firstname("Jane")
				.lastname("Doe")
				.email("jane.doe@example.com")
				.password("plainPassword")
				.build();

		when(userRepository.existsByEmail("jane.doe@example.com")).thenReturn(false);
		when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
		when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
			User saved = invocation.getArgument(0);
			saved.setId(1L);
			return saved;
		});

		UserResponseDTO response = userService.register(request);

		assertThat(response.getId()).isEqualTo(1L);
		assertThat(response.getFirstname()).isEqualTo("Jane");
		assertThat(response.getLastname()).isEqualTo("Doe");
		assertThat(response.getEmail()).isEqualTo("jane.doe@example.com");

		verify(userRepository).save(any(User.class));
	}

	@Test
	void register_encodesPasswordBeforeSaving() throws UserAlreadyExistException {
		UserRequestDTO request = UserRequestDTO.builder()
				.firstname("Jane")
				.lastname("Doe")
				.email("jane.doe@example.com")
				.password("plainPassword")
				.build();

		when(userRepository.existsByEmail(anyString())).thenReturn(false);
		when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
		when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

		userService.register(request);

		verify(passwordEncoder).encode("plainPassword");
	}

	@Test
	void register_throwsUserAlreadyExistException_whenEmailAlreadyRegistered() {
		UserRequestDTO request = UserRequestDTO.builder()
				.firstname("Jane")
				.lastname("Doe")
				.email("jane.doe@example.com")
				.password("plainPassword")
				.build();

		when(userRepository.existsByEmail("jane.doe@example.com")).thenReturn(true);

		assertThatThrownBy(() -> userService.register(request))
				.isInstanceOf(UserAlreadyExistException.class)
				.hasMessageContaining("jane.doe@example.com");

		verify(userRepository, never()).save(any(User.class));
	}

	// ---- login ----

	@Test
	void login_returnsTokenAndUser_whenCredentialsAreValid() throws InvalidCredentialException {
		LoginRequestDTO request = new LoginRequestDTO("jane.doe@example.com", "plainPassword");

		User user = new User();
		user.setId(1L);
		user.setFirstname("Jane");
		user.setLastname("Doe");
		user.setEmail("jane.doe@example.com");
		user.setPassword("encodedPassword");

		UserDetails userDetails = org.springframework.security.core.userdetails.User
				.withUsername("jane.doe@example.com")
				.password("encodedPassword")
				.authorities(Collections.emptyList())
				.build();

		when(userRepository.findByEmail("jane.doe@example.com")).thenReturn(Optional.of(user));
		when(passwordEncoder.matches("plainPassword", "encodedPassword")).thenReturn(true);
		when(userDetailsService.loadUserByUsername("jane.doe@example.com")).thenReturn(userDetails);
		when(jwtService.generateToken(userDetails)).thenReturn("jwt-token");

		AuthResponseDTO response = userService.login(request);

		assertThat(response.getToken()).isEqualTo("jwt-token");
		assertThat(response.getType()).isEqualTo("Bearer");
		assertThat(response.getUser().getEmail()).isEqualTo("jane.doe@example.com");
		assertThat(response.getUser().getFirstname()).isEqualTo("Jane");
	}

	@Test
	void login_throwsInvalidCredentialException_whenEmailNotFound() {
		LoginRequestDTO request = new LoginRequestDTO("missing@example.com", "plainPassword");

		when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

		assertThatThrownBy(() -> userService.login(request))
				.isInstanceOf(InvalidCredentialException.class)
				.hasMessageContaining("Invalid email or password");
	}

	@Test
	void login_throwsInvalidCredentialException_whenPasswordDoesNotMatch() {
		LoginRequestDTO request = new LoginRequestDTO("jane.doe@example.com", "wrongPassword");

		User user = new User();
		user.setEmail("jane.doe@example.com");
		user.setPassword("encodedPassword");

		when(userRepository.findByEmail("jane.doe@example.com")).thenReturn(Optional.of(user));
		when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

		assertThatThrownBy(() -> userService.login(request))
				.isInstanceOf(InvalidCredentialException.class)
				.hasMessageContaining("Invalid email or password");
	}
}
