package io.custos.node.application.exception;

public class SecretAccessDeniedException extends RuntimeException {
    public SecretAccessDeniedException(String secretId, String userAddress) {
        super("Access denied for secret %s and user %s".formatted(secretId, userAddress));
    }
}
