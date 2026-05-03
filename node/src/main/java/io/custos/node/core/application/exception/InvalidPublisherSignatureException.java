package io.custos.node.core.application.exception;

public class InvalidPublisherSignatureException extends RuntimeException {
    public InvalidPublisherSignatureException() {
        super("Invalid publisher signature");
    }
}
