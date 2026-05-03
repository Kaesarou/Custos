package io.custos.node.adapters.out.persistence.jpa.nonce;

import io.custos.node.core.application.port.out.WalletNonceRepository;
import io.custos.node.core.domain.model.UsedWalletNonce;
import org.springframework.stereotype.Repository;

@Repository
public class JpaWalletNonceRepository implements WalletNonceRepository {

    private final SpringDataWalletNonceRepository repository;

    public JpaWalletNonceRepository(SpringDataWalletNonceRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(UsedWalletNonce walletNonce) {
        this.repository.save(WalletNonceJpaMapper.toEntity(walletNonce));
    }

    @Override
    public boolean existsByUserAddressAndSecretIdAndNonce(String userAddress, String secretId, String nonce) {
        return this.repository.existsByUserAddressAndSecretIdAndNonce(userAddress, secretId, nonce);
    }


}
