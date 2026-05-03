package io.custos.node.core.application.exception;

public class SecretNotFoundException extends RuntimeException {
    public SecretNotFoundException(String secretId) {
        super("Secret not found: " + secretId);
    }
}
