package io.custos.node.domain.model;

import java.util.Objects;

public record AccessPolicy(
        PolicyType type,
        long chainId,
        String validatorContract,
        String policyData
) {
    public AccessPolicy {

        Objects.requireNonNull(type, "type is required");
        Objects.requireNonNull(validatorContract, "validatorContract is required");
        Objects.requireNonNull(policyData, "policyData is required");
    }
}
