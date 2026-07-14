package com.example.superdive.backend.exception;

public class CustomerAlreadyExistException extends Exception{
	public CustomerAlreadyExistException(String msg) {
		super(msg);
	}
}
