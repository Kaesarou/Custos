package io.custos.node.core.application.port.out;

import io.custos.node.core.domain.model.UsedWalletNonce;

public interface WalletNonceStore {
    void markAsUsed(UsedWalletNonce nonce);
}
