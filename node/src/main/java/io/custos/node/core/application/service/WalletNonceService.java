package io.custos.node.core.application.service;

import io.custos.node.core.application.exception.InvalidWalletSignatureException;
import io.custos.node.core.application.port.out.WalletNonceRepository;
import io.custos.node.core.domain.model.UsedWalletNonce;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import static io.custos.node.core.application.exception.SignatureErrorCode.NONCE_ALREADY_USED;

@Service
public class WalletNonceService {

    private final WalletNonceRepository repository;

    public WalletNonceService(WalletNonceRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void markNonceAsUsed(String userAddress, String secretId, String nonce) {
        String normalizedAddress = userAddress.toLowerCase();

        if (repository.existsByUserAddressAndSecretIdAndNonce(
                normalizedAddress,
                secretId,
                nonce
        )) {
            throw new InvalidWalletSignatureException(
                    NONCE_ALREADY_USED,
                    "Nonce already used"
            );
        }

        repository.save(UsedWalletNonce.of(
                normalizedAddress,
                secretId,
                nonce
        ));
    }
}