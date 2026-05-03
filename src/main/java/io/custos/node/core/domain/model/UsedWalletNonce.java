package io.custos.node.core.domain.model;

import java.time.Instant;
import java.util.Objects;

public record UsedWalletNonce(
        String userAddress,
        String secretId,
        String nonce,
        Instant usedAt
) {
    public UsedWalletNonce {
        Objects.requireNonNull(userAddress, "userAddress is required");
        Objects.requireNonNull(secretId, "secretId is required");
        Objects.requireNonNull(nonce, "nonce is required");
        Objects.requireNonNull(usedAt, "usedAt is required");
    }

    public static UsedWalletNonce of(String userAddress, String secretId, String nonce) {
        return new UsedWalletNonce(userAddress, secretId, nonce, Instant.now());
    }
}
