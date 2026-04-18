package io.custos.node.adapters.out.blockchain;

import io.custos.node.application.port.out.AccessPolicyEvaluator;
import io.custos.node.domain.model.AccessPolicy;

public class StubAccessPolicyEvaluator implements AccessPolicyEvaluator {

    @Override
    public boolean canAccess(String userAddress, AccessPolicy accessPolicy) {
        // TODO replace with validator contract call on Ethereum
        return userAddress != null && userAddress.startsWith("0x");
    }
}
