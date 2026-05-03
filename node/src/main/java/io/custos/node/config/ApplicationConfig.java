package io.custos.node.config;

import io.custos.node.adapters.out.security.AcceptAllPublisherSignatureVerifier;
import io.custos.node.adapters.out.security.Base64ShareProtectionService;
import io.custos.node.adapters.out.security.LocalNodeSignatureService;
import io.custos.node.core.application.port.in.StoreSecretShareUseCase;
import io.custos.node.core.application.port.in.RetrieveSecretShareUseCase;
import io.custos.node.core.application.port.out.*;
import io.custos.node.core.application.service.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@EnableConfigurationProperties(CustosProperties.class)
public class ApplicationConfig {

    @Bean
    PublisherSignatureVerifier publisherSignatureVerifier() {
        return new AcceptAllPublisherSignatureVerifier();
    }

    @Bean
    PolicyValidationService policyValidationService(List<AccessPolicyValidator> accessPolicyValidators) {
        return new PolicyValidationService(accessPolicyValidators);
    }

    @Bean
    WalletNonceService walletNonceService(WalletNonceRepository walletNonceRepository) {
        return new WalletNonceService(walletNonceRepository);
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
    StoreSecretShareUseCase registerSecretShareUseCase(
            SecretShareRepository repository,
            PublisherSignatureVerifier publisherSignatureVerifier
    ) {
        return new StoreSecretShareService(repository, publisherSignatureVerifier);
    }

    @Bean
    RetrieveSecretShareUseCase requestShareUseCase(
            CustosProperties custosProperties,
            SecretShareRepository repository,
            WalletSignatureVerifier walletSignatureVerifier,
            PolicyValidationService policyValidationService,
            WalletNonceService walletNonceService,
            ShareProtectionService shareProtectionService,
            NodeSignatureService nodeSignatureService
    ) {
        return new RetrieveSecretShareService(
                custosProperties.nodeId(),
                repository,
                walletSignatureVerifier,
                policyValidationService,
                walletNonceService,
                shareProtectionService,
                nodeSignatureService
        );
    }

    @Bean
    ChainRpcResolver chainRpcResolver(CustosProperties custosProperties) {
        return new ChainRpcResolver(custosProperties);
    }
}
