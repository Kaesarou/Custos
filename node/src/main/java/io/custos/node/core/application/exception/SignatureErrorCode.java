package io.custos.node.core.application.exception;

public enum SignatureErrorCode {
    MISSING_WALLET_SIGNATURE,
    MISSING_NONCE,
    INVALID_USER_ADDRESS,
    INVALID_WALLET_SIGNATURE,
    SIGNATURE_VERIFICATION_FAILED,
    NONCE_ALREADY_USED
}