package io.custos.node.core.application.service;

import io.custos.node.core.application.port.out.WalletNonceStore;
import io.custos.node.core.domain.model.UsedWalletNonce;

import java.time.Clock;

public class WalletNonceService {

    private final Clock clock;
    private final WalletNonceStore nonceStore;

    public WalletNonceService(Clock clock, WalletNonceStore nonceStore) {
        this.clock = clock;
        this.nonceStore = nonceStore;
    }

    public void markNonceAsUsed(String userAddress, String secretId, String nonce) {
        nonceStore.markAsUsed(UsedWalletNonce.of(this.clock, userAddress, secretId, nonce));
    }
}