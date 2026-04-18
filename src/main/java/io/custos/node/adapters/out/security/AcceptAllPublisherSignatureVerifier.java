package io.custos.node.adapters.out.security;

import io.custos.node.application.port.in.command.RegisterSecretShareCommand;
import io.custos.node.application.port.out.PublisherSignatureVerifier;

public class AcceptAllPublisherSignatureVerifier implements PublisherSignatureVerifier {

    @Override
    public boolean isValid(RegisterSecretShareCommand command) {
        // TODO replace with actual publisher signature verification
        return command.publisherSignature() != null && !command.publisherSignature().isBlank();
    }
}
