package br.com.fiap.easypark.exceptions;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String message) { super(message); }
}
