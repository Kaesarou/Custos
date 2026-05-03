package io.custos.node.adapters.out.persistence.jpa.share;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataSecretShareRepository extends JpaRepository<SecretShareEntity, String> {
}