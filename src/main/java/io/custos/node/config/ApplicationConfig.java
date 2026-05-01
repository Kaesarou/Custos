package io.custos.node.config;

import io.custos.node.adapters.out.persistence.jpa.JpaSecretShareRepository;
import io.custos.node.adapters.out.persistence.jpa.SpringDataSecretShareRepository;
import io.custos.node.adapters.out.security.AcceptAllPublisherSignatureVerifier;
import io.custos.node.adapters.out.security.AcceptAllWalletSignatureVerifier;
import io.custos.node.adapters.out.security.Base64ShareProtectionService;
import io.custos.node.adapters.out.security.LocalNodeSignatureService;
import io.custos.node.application.port.in.RegisterSecretShareService;
import io.custos.node.application.port.in.RequestShareService;
import io.custos.node.application.port.out.*;
import io.custos.node.application.service.ChainRpcResolver;
import io.custos.node.application.service.PolicyValidationService;
import io.custos.node.application.service.RegisterSecretShareServiceImpl;
import io.custos.node.application.service.RequestShareServiceImpl;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

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
    PolicyValidationService policyValidationService(List<AccessPolicyValidator> accessPolicyValidators) {
        return new PolicyValidationService(accessPolicyValidators);
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
    RegisterSecretShareService registerSecretShareUseCase(
            SecretShareRepository repository,
            PublisherSignatureVerifier publisherSignatureVerifier
    ) {
        return new RegisterSecretShareServiceImpl(repository, publisherSignatureVerifier);
    }

    @Bean
    RequestShareService requestShareUseCase(
            CustosProperties custosProperties,
            SecretShareRepository repository,
            WalletSignatureVerifier walletSignatureVerifier,
            PolicyValidationService policyValidationService,
            ShareProtectionService shareProtectionService,
            NodeSignatureService nodeSignatureService
    ) {
        return new RequestShareServiceImpl(
                custosProperties.nodeId(),
                repository,
                walletSignatureVerifier,
                policyValidationService,
                shareProtectionService,
                nodeSignatureService
        );
    }

    @Bean
    ChainRpcResolver chainRpcResolver(CustosProperties custosProperties) {
        return new ChainRpcResolver(custosProperties);
    }
}
