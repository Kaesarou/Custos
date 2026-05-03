package io.custos.node.core.application.port.out;

import io.custos.node.core.domain.PolicyValidationResult;
import io.custos.node.core.domain.model.AccessPolicy;
import io.custos.node.core.domain.model.PolicyType;

public interface AccessPolicyValidator {

    PolicyType supportedType();

    PolicyValidationResult validate(AccessPolicy policy, String walletAddress);
}