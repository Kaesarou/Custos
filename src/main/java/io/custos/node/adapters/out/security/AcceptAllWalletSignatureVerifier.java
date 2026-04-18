package io.custos.node.adapters.out.security;

import io.custos.node.application.port.in.command.RequestShareCommand;
import io.custos.node.application.port.out.WalletSignatureVerifier;

public class AcceptAllWalletSignatureVerifier implements WalletSignatureVerifier {

    @Override
    public boolean isValid(RequestShareCommand command) {
        // TODO replace with EVM wallet signature verification
        return command.walletSignature() != null && !command.walletSignature().isBlank();
    }
}
