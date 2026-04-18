package io.custos.node.application.port.out;

import io.custos.node.application.port.in.command.RequestShareCommand;

public interface WalletSignatureVerifier {
    boolean isValid(RequestShareCommand command);
}
