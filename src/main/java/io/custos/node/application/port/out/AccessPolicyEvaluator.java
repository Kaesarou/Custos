package io.custos.node.application.port.out;

import io.custos.node.domain.model.AccessPolicy;

public interface AccessPolicyEvaluator {
    boolean canAccess(String userAddress, AccessPolicy accessPolicy);
}
