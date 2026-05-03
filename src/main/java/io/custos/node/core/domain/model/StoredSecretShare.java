package io.custos.node.core.domain.model;

import java.time.Instant;
import java.util.Objects;

public record StoredSecretShare(
        String secretId,
        String encryptedShare,
        AccessPolicy accessPolicy,
        String publisherAddress,
        Instant createdAt
) {
    public StoredSecretShare {
        Objects.requireNonNull(secretId, "secretId is required");
        Objects.requireNonNull(encryptedShare, "encryptedShare is required");
        Objects.requireNonNull(accessPolicy, "accessPolicy is required");
        Objects.requireNonNull(publisherAddress, "publisherAddress is required");
        Objects.requireNonNull(createdAt, "createdAt is required");
    }
}
