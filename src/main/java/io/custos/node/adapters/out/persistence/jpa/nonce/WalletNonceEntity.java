package io.custos.node.adapters.out.persistence.jpa.nonce;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(
        name = "wallet_nonce",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_wallet_nonce",
                        columnNames = {"user_address", "secret_id", "nonce"}
                )
        }
)
public class WalletNonceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_address", nullable = false, length = 42)
    private String userAddress;

    @Column(name = "secret_id", nullable = false)
    private String secretId;

    @Column(name = "nonce", nullable = false)
    private String nonce;

    @Column(name = "used_at", nullable = false)
    private Instant usedAt;

    protected WalletNonceEntity() {
    }

    public WalletNonceEntity(String userAddress, String secretId, String nonce, Instant usedAt) {
        this.userAddress = userAddress.toLowerCase();
        this.secretId = secretId;
        this.nonce = nonce;
        this.usedAt = usedAt;
    }
}