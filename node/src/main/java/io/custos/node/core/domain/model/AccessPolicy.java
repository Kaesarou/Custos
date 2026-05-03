package io.custos.node.core.domain.model;

import java.util.Objects;

public record AccessPolicy(
        PolicyType type,
        long chainId,
        String contractAddress,
        String policyData
) {
    public AccessPolicy {

        Objects.requireNonNull(type, "type is required");
        Objects.requireNonNull(contractAddress, "contractAddress is required");
        Objects.requireNonNull(policyData, "policyData is required");
    }
}
