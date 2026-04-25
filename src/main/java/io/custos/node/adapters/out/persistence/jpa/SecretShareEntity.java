package io.custos.node.adapters.out.persistence.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "secret_shares")
public class SecretShareEntity {

    @Id
    @Column(name = "secret_id", nullable = false, length = 128)
    private String secretId;

    @Column(name = "encrypted_share", nullable = false, columnDefinition = "TEXT")
    private String encryptedShare;

    @Column(name = "policy_type", nullable = false)
    private String policyType;

    @Column(name = "chain_id", nullable = false)
    private long chainId;

    @Column(name = "validator_contract", nullable = false)
    private String validatorContract;

    @Column(name = "policy_data", nullable = false, columnDefinition = "TEXT")
    private String policyData;

    @Column(name = "publisher_address", nullable = false)
    private String publisherAddress;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected SecretShareEntity() {
    }

    public SecretShareEntity(
            String secretId,
            String encryptedShare,
            String policyType,
            long chainId,
            String validatorContract,
            String policyData,
            String publisherAddress,
            Instant createdAt
    ) {
        this.secretId = secretId;
        this.encryptedShare = encryptedShare;
        this.policyType = policyType;
        this.chainId = chainId;
        this.validatorContract = validatorContract;
        this.policyData = policyData;
        this.publisherAddress = publisherAddress;
        this.createdAt = createdAt;
    }

    public String getSecretId() {
        return secretId;
    }

    public String getEncryptedShare() {
        return encryptedShare;
    }

    public String getPolicyType() {
        return policyType;
    }

    public long getChainId() {
        return chainId;
    }

    public String getValidatorContract() {
        return validatorContract;
    }

    public String getPolicyData() {
        return policyData;
    }

    public String getPublisherAddress() {
        return publisherAddress;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}