package io.custos.node.core.application.exception;

public class InvalidWalletSignatureException extends RuntimeException {

    private final WalletSignatureErrorCode code;

    public InvalidWalletSignatureException(WalletSignatureErrorCode code, String message) {
        super(message);
        this.code = code;
    }

    public WalletSignatureErrorCode getCode() {
        return code;
    }
}
