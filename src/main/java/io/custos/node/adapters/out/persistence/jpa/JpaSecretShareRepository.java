package io.custos.node.adapters.out.persistence.jpa;

import io.custos.node.application.port.out.SecretShareRepository;
import io.custos.node.domain.model.StoredSecretShare;

import java.util.Optional;

public class JpaSecretShareRepository implements SecretShareRepository {

    private final SpringDataSecretShareRepository repository;

    public JpaSecretShareRepository(SpringDataSecretShareRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(StoredSecretShare storedSecretShare) {
        repository.save(SecretShareJpaMapper.toEntity(storedSecretShare));
    }

    @Override
    public Optional<StoredSecretShare> findBySecretId(String secretId) {
        return repository.findById(secretId)
                .map(SecretShareJpaMapper::toDomain);
    }
}