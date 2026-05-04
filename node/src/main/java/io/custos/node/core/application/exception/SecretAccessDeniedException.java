package io.custos.node.core.application.exception;

public class SecretAccessDeniedException extends RuntimeException {
    public SecretAccessDeniedException(String secretId, String userAddress, String reason) {
        super("Access denied for secret %s and user %s. Reason : %s".formatted(secretId, userAddress, reason));
    }
}
