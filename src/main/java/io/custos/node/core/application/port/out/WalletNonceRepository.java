package io.custos.node.core.application.port.out;

import io.custos.node.core.domain.model.UsedWalletNonce;

public interface WalletNonceRepository {

    void save(UsedWalletNonce walletNonce);

    boolean existsByUserAddressAndSecretIdAndNonce(
            String userAddress,
            String secretId,
            String nonce
    );
}
