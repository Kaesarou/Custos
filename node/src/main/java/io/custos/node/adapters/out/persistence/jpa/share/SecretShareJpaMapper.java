package io.custos.node.adapters.out.persistence.jpa.share;

import io.custos.node.core.domain.model.AccessPolicy;
import io.custos.node.core.domain.model.PolicyType;
import io.custos.node.core.domain.model.StoredSecretShare;

public final class SecretShareJpaMapper {

    private SecretShareJpaMapper() {
    }

    public static SecretShareEntity toEntity(StoredSecretShare domain) {
        return new SecretShareEntity(
                domain.secretId(),
                domain.encryptedShare(),
                domain.accessPolicy().type().name(),
                domain.accessPolicy().chainId(),
                domain.accessPolicy().validatorContract(),
                domain.accessPolicy().policyData(),
                domain.publisherAddress(),
                domain.createdAt()
        );
    }

    public static StoredSecretShare toDomain(SecretShareEntity entity) {
        return new StoredSecretShare(
                entity.getSecretId(),
                entity.getEncryptedShare(),
                new AccessPolicy(
                        PolicyType.valueOf(entity.getPolicyType()),
                        entity.getChainId(),
                        entity.getValidatorContract(),
                        entity.getPolicyData()
                ),
                entity.getPublisherAddress(),
                entity.getCreatedAt()
        );
    }
}