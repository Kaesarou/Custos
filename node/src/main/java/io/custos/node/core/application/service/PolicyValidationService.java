package io.custos.node.core.application.service;


import io.custos.node.core.application.port.out.AccessPolicyValidator;
import io.custos.node.core.domain.PolicyValidationResult;
import io.custos.node.core.domain.model.AccessPolicy;
import io.custos.node.core.domain.model.PolicyType;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class PolicyValidationService {

    private final Map<PolicyType, AccessPolicyValidator> validators = new EnumMap<>(PolicyType.class);

    public PolicyValidationService(List<AccessPolicyValidator> validators) {
        for (AccessPolicyValidator validator : validators) {
            this.validators.put(validator.supportedType(), validator);
        }
    }

    public PolicyValidationResult validate(AccessPolicy policy, String walletAddress) {
        if (policy == null || policy.type() == null) {
            return PolicyValidationResult.invalid("INVALID_POLICY");
        }

        AccessPolicyValidator validator = validators.get(policy.type());

        if (validator == null) {
            return PolicyValidationResult.invalid("UNSUPPORTED_POLICY_TYPE");
        }

        return validator.validate(policy, walletAddress);
    }
}