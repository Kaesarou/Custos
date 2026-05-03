package io.custos.node.core.application.port.out;

public interface WalletSignatureVerifier {
    void verifyRetrieveSecretSignature(
            String secretId,
            String userAddress,
            String nonce,
            String walletSignature
    );
}
