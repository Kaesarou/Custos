package io.custos.node.application.port.in.command;

public record RequestShareCommand(
        String secretId,
        String userAddress,
        String walletSignature,
        String readerPublicKey,
        String nonce
) {}
