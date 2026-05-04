package io.custos.node.core.domain.model;

import java.time.Clock;
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

    public static UsedWalletNonce of(Clock clock, String userAddress, String secretId, String nonce) {

        return new UsedWalletNonce(normalizeAddress(userAddress), secretId, nonce, Instant.now(clock));
    }

    private static String normalizeAddress(String userAddress) {
        return userAddress.toLowerCase();
    }
}
