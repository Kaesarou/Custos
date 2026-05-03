package io.custos.node.core.application.service;

public final class WalletSignatureMessageBuilder {

    private WalletSignatureMessageBuilder() {
    }

    public static String buildRetrieveSecretMessage(
            String secretId,
            String userAddress,
            String nonce
    ) {
        return """
                Custos retrieve secret
                secretId: %s
                userAddress: %s
                nonce: %s""".formatted(secretId, userAddress, nonce);
    }
}