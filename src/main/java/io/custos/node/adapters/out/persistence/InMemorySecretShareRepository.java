package io.custos.node.adapters.out.persistence;

import io.custos.node.application.port.out.SecretShareRepository;
import io.custos.node.domain.model.StoredSecretShare;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemorySecretShareRepository implements SecretShareRepository {

    private final Map<String, StoredSecretShare> store = new ConcurrentHashMap<>();

    @Override
    public void save(StoredSecretShare storedSecretShare) {
        store.put(storedSecretShare.secretId(), storedSecretShare);
    }

    @Override
    public Optional<StoredSecretShare> findBySecretId(String secretId) {
        return Optional.ofNullable(store.get(secretId));
    }
}
