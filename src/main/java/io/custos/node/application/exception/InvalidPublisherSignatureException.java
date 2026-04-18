package io.custos.node.application.exception;

public class InvalidPublisherSignatureException extends RuntimeException {
    public InvalidPublisherSignatureException() {
        super("Invalid publisher signature");
    }
}
