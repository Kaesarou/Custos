package io.custos.node.application.port.in.command;

import io.custos.node.domain.model.AccessPolicy;

public record RegisterSecretShareCommand(
        String secretId,
        String encryptedShare,
        AccessPolicy accessPolicy,
        String publisherAddress,
        String publisherSignature
) {}
