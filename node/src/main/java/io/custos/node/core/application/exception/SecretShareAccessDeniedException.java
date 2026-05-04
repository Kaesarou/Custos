package io.custos.node.core.application.exception;

public class SecretShareAccessDeniedException extends RuntimeException {
    public SecretShareAccessDeniedException(String secretId, String userAddress, String reason) {
        super("Access denied for secret %s and user %s. Reason : %s".formatted(secretId, userAddress, reason));
    }
}
