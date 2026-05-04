package io.custos.node.core.application.exception;

public class WalletNonceAlreadyUsedException extends RuntimeException {

    public WalletNonceAlreadyUsedException(String userAddress, String secretId, String nonce) {
        super("Wallet nonce already used for userAddress=%s and secretId=%s".formatted(userAddress, secretId));
    }
}