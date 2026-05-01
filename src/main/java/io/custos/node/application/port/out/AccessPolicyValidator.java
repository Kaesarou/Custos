package io.custos.node.application.port.out;

import io.custos.node.domain.PolicyValidationResult;
import io.custos.node.domain.model.AccessPolicy;
import io.custos.node.domain.model.PolicyType;

public interface AccessPolicyValidator {

    PolicyType supportedType();

    PolicyValidationResult validate(AccessPolicy policy, String walletAddress);
}