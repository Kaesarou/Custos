package io.custos.node.core.application.service;

public record RetrieveSecretSignatureChallenge(
        String secretId,
        String userAddress,
        String nonce
) {
    public String message() {
        return """
            Custos retrieve secret
            secretId: %s
            userAddress: %s
            nonce: %s
            """.formatted(secretId, userAddress, nonce).stripTrailing();
    }
}