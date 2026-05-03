package io.custos.node.core.application.port.in.command;

public record RequestShareCommand(
        String secretId,
        String userAddress,
        String walletSignature,
        String readerPublicKey,
        String nonce
) {}
