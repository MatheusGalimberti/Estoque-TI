package br.com.cnec.estoqueti.exception;

public class ApiError extends RuntimeException {
    public ApiError(String message) {
        super(message);
    }
}
