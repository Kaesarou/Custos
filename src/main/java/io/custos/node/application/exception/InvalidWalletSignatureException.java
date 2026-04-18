package io.custos.node.application.exception;

public class InvalidWalletSignatureException extends RuntimeException {
    public InvalidWalletSignatureException() {
        super("Invalid wallet signature");
    }
}
