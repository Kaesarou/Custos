package io.custos.node.adapters.out.persistence.jpa.walletnonce;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataWalletNonceRepository extends JpaRepository<WalletNonceEntity, Long> {
    boolean existsByUserAddressAndSecretIdAndNonce(
            String userAddress,
            String secretId,
            String nonce
    );
}
