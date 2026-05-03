package io.custos.node.adapters.out.security;

import io.custos.node.core.application.port.in.command.StoreSecretShareCommand;
import io.custos.node.core.application.port.out.PublisherSignatureVerifier;
import org.springframework.stereotype.Service;

@Service
public class AcceptAllPublisherSignatureVerifier implements PublisherSignatureVerifier {

    @Override
    public boolean isValid(StoreSecretShareCommand command) {
        // TODO replace with actual publisher signature verification
        return command.publisherSignature() != null && !command.publisherSignature().isBlank();
    }
}
