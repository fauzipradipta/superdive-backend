package com.example.superdive.backend.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.superdive.backend.dto.Request.LoginRequestDTO;
import com.example.superdive.backend.dto.Request.UserRequestDTO;
import com.example.superdive.backend.dto.Response.AuthResponseDTO;
import com.example.superdive.backend.dto.Response.UserResponseDTO;
import com.example.superdive.backend.exception.InvalidCredentialException;
import com.example.superdive.backend.exception.UserAlreadyExistException;
import com.example.superdive.backend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

	@Mock
	private UserService userService;

	private MockMvc mockMvc;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(new AuthController(userService)).build();
	}

	// ---- /api/auth/register (signup) ----

	@Test
	void register_returns201AndCreatedUser_onSuccess() throws Exception {
		UserRequestDTO request = UserRequestDTO.builder()
				.firstname("Jane")
				.lastname("Doe")
				.email("jane.doe@example.com")
				.password("plainPassword")
				.build();

		UserResponseDTO response = UserResponseDTO.builder()
				.id(1L)
				.firstname("Jane")
				.lastname("Doe")
				.email("jane.doe@example.com")
				.build();

		when(userService.register(any(UserRequestDTO.class))).thenReturn(response);

		mockMvc.perform(post("/api/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.email").value("jane.doe@example.com"));
	}

	@Test
	void register_returns409_whenEmailAlreadyRegistered() throws Exception {
		UserRequestDTO request = UserRequestDTO.builder()
				.firstname("Jane")
				.lastname("Doe")
				.email("jane.doe@example.com")
				.password("plainPassword")
				.build();

		when(userService.register(any(UserRequestDTO.class)))
				.thenThrow(new UserAlreadyExistException("Email jane.doe@example.com is already registered"));

		mockMvc.perform(post("/api/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isConflict())
				.andExpect(content().string("Email jane.doe@example.com is already registered"));
	}

	// ---- /api/auth/login ----

	@Test
	void login_returns200AndToken_onSuccess() throws Exception {
		LoginRequestDTO request = new LoginRequestDTO("jane.doe@example.com", "plainPassword");

		AuthResponseDTO response = AuthResponseDTO.builder()
				.token("jwt-token")
				.type("Bearer")
				.user(UserResponseDTO.builder()
						.id(1L)
						.firstname("Jane")
						.lastname("Doe")
						.email("jane.doe@example.com")
						.build())
				.build();

		when(userService.login(any(LoginRequestDTO.class))).thenReturn(response);

		mockMvc.perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token").value("jwt-token"))
				.andExpect(jsonPath("$.type").value("Bearer"))
				.andExpect(jsonPath("$.user.email").value("jane.doe@example.com"));
	}

	@Test
	void login_returns401_onInvalidCredentials() throws Exception {
		LoginRequestDTO request = new LoginRequestDTO("jane.doe@example.com", "wrongPassword");

		when(userService.login(any(LoginRequestDTO.class)))
				.thenThrow(new InvalidCredentialException("Invalid email or password"));

		mockMvc.perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isUnauthorized())
				.andExpect(content().string("Invalid email or password"));
	}
}
