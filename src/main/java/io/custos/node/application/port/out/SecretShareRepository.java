package io.custos.node.application.port.out;

import io.custos.node.domain.model.StoredSecretShare;

import java.util.Optional;

public interface SecretShareRepository {
    void save(StoredSecretShare storedSecretShare);
    Optional<StoredSecretShare> findBySecretId(String secretId);
}
