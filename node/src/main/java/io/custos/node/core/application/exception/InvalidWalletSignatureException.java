package io.custos.node.core.application.exception;

public class InvalidWalletSignatureException extends RuntimeException {

    private final SignatureErrorCode code;

    public InvalidWalletSignatureException(SignatureErrorCode code, String message) {
        super(message);
        this.code = code;
    }

    public SignatureErrorCode getCode() {
        return code;
    }
}
