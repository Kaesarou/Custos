package io.custos.node.config;

import io.custos.node.adapters.out.blockchain.StubAccessPolicyEvaluator;
import io.custos.node.adapters.out.persistence.jpa.JpaSecretShareRepository;
import io.custos.node.adapters.out.persistence.jpa.SpringDataSecretShareRepository;
import io.custos.node.adapters.out.security.AcceptAllPublisherSignatureVerifier;
import io.custos.node.adapters.out.security.AcceptAllWalletSignatureVerifier;
import io.custos.node.adapters.out.security.Base64ShareProtectionService;
import io.custos.node.adapters.out.security.LocalNodeSignatureService;
import io.custos.node.application.port.in.RegisterSecretShareUseCase;
import io.custos.node.application.port.in.RequestShareUseCase;
import io.custos.node.application.port.out.*;
import io.custos.node.application.service.RegisterSecretShareService;
import io.custos.node.application.service.RequestShareService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(CustosProperties.class)
public class ApplicationConfig {

    @Bean
    SecretShareRepository secretShareRepository(
            SpringDataSecretShareRepository springDataSecretShareRepository
    ) {
        return new JpaSecretShareRepository(springDataSecretShareRepository);
    }

    @Bean
    PublisherSignatureVerifier publisherSignatureVerifier() {
        return new AcceptAllPublisherSignatureVerifier();
    }

    @Bean
    WalletSignatureVerifier walletSignatureVerifier() {
        return new AcceptAllWalletSignatureVerifier();
    }

    @Bean
    AccessPolicyEvaluator accessPolicyEvaluator() {
        return new StubAccessPolicyEvaluator();
    }

    @Bean
    ShareProtectionService shareProtectionService() {
        return new Base64ShareProtectionService();
    }

    @Bean
    NodeSignatureService nodeSignatureService() {
        return new LocalNodeSignatureService();
    }

    @Bean
    RegisterSecretShareUseCase registerSecretShareUseCase(
            SecretShareRepository repository,
            PublisherSignatureVerifier publisherSignatureVerifier
    ) {
        return new RegisterSecretShareService(repository, publisherSignatureVerifier);
    }

    @Bean
    RequestShareUseCase requestShareUseCase(
            CustosProperties custosProperties,
            SecretShareRepository repository,
            WalletSignatureVerifier walletSignatureVerifier,
            AccessPolicyEvaluator accessPolicyEvaluator,
            ShareProtectionService shareProtectionService,
            NodeSignatureService nodeSignatureService
    ) {
        return new RequestShareService(
                custosProperties.nodeId(),
                repository,
                walletSignatureVerifier,
                accessPolicyEvaluator,
                shareProtectionService,
                nodeSignatureService
        );
    }
}
