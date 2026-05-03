package io.custos.node.core.application.port.in.command;

import io.custos.node.core.domain.model.AccessPolicy;

public record RegisterSecretShareCommand(
        String secretId,
        String encryptedShare,
        AccessPolicy accessPolicy,
        String publisherAddress,
        String publisherSignature
) {}
