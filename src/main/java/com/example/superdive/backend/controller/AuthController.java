package com.example.superdive.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.superdive.backend.dto.Request.LoginRequestDTO;
import com.example.superdive.backend.dto.Request.UserRequestDTO;
import com.example.superdive.backend.dto.Response.AuthResponseDTO;
import com.example.superdive.backend.dto.Response.UserResponseDTO;
import com.example.superdive.backend.exception.InvalidCredentialException;
import com.example.superdive.backend.exception.UserAlreadyExistException;
import com.example.superdive.backend.service.UserService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

	private final UserService userService;

	public AuthController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody UserRequestDTO request) {
		try {
			UserResponseDTO user = userService.register(request);
			return new ResponseEntity<>(user, HttpStatus.CREATED);
		} catch (UserAlreadyExistException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
		}
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
		try {
			AuthResponseDTO response = userService.login(request);
			return ResponseEntity.ok(response);
		} catch (InvalidCredentialException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
		}
	}
}
