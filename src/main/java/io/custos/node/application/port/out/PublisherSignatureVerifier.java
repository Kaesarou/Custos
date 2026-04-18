package io.custos.node.application.port.out;

import io.custos.node.application.port.in.command.RegisterSecretShareCommand;

public interface PublisherSignatureVerifier {
    boolean isValid(RegisterSecretShareCommand command);
}
