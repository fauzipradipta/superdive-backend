package com.example.superdive.backend.exception;

public class UserAlreadyExistException extends Exception {
	public UserAlreadyExistException(String msg) {
		super(msg);
	}
}
