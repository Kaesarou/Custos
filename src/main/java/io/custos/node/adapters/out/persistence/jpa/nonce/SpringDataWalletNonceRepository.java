package io.custos.node.adapters.out.persistence.jpa.nonce;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataWalletNonceRepository extends JpaRepository<WalletNonceEntity, Long> {
    boolean existsByUserAddressAndSecretIdAndNonce(
            String userAddress,
            String secretId,
            String nonce
    );
}
