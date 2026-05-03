package io.custos.node.adapters.out.persistence.jpa.nonce;

import io.custos.node.core.domain.model.UsedWalletNonce;

public final class WalletNonceJpaMapper {

    private WalletNonceJpaMapper() {}

    public static WalletNonceEntity toEntity(UsedWalletNonce domain) {
        return new WalletNonceEntity(domain.userAddress(), domain.secretId(), domain.nonce(), domain.usedAt());
    }
}
