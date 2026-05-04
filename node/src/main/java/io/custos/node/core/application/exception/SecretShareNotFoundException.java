package io.custos.node.core.application.exception;

public class SecretShareNotFoundException extends RuntimeException {
    public SecretShareNotFoundException(String secretId) {
        super("Secret not found: " + secretId);
    }
}
