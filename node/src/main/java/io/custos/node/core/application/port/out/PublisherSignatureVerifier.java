package io.custos.node.core.application.port.out;

import io.custos.node.core.application.port.in.command.RegisterSecretShareCommand;

public interface PublisherSignatureVerifier {
    boolean isValid(RegisterSecretShareCommand command);
}
