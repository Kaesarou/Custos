package io.custos.node.application.service;

import io.custos.node.adapters.out.blockchain.StubAccessPolicyEvaluator;
import io.custos.node.adapters.out.persistence.jpa.JpaSecretShareRepository;
import io.custos.node.adapters.out.security.AcceptAllPublisherSignatureVerifier;
import io.custos.node.adapters.out.security.AcceptAllWalletSignatureVerifier;
import io.custos.node.adapters.out.security.Base64ShareProtectionService;
import io.custos.node.adapters.out.security.LocalNodeSignatureService;
import io.custos.node.application.port.in.command.RegisterSecretShareCommand;
import io.custos.node.application.port.in.command.RequestShareCommand;
import io.custos.node.domain.model.AccessPolicy;
import io.custos.node.domain.model.PolicyType;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
class RegisterAndRequestShareFlowTest {

    @Test
    void shouldRegisterThenDeliverProtectedShare() {
        var repository = new JpaSecretShareRepository(null);

        var registerService = new RegisterSecretShareService(
                repository,
                new AcceptAllPublisherSignatureVerifier()
        );

        var requestService = new RequestShareService(
                "test-node",
                repository,
                new AcceptAllWalletSignatureVerifier(),
                new StubAccessPolicyEvaluator(),
                new Base64ShareProtectionService(),
                new LocalNodeSignatureService()
        );

        registerService.register(new RegisterSecretShareCommand(
                "secret-1",
                "share-encrypted-at-rest",
                new AccessPolicy(PolicyType.EVM_VALIDATOR_CONTRACT, 1L, "0xvalidator", "{\"nft\":42}"),
                "0xpublisher",
                "publisher-signature"
        ));

        var delivery = requestService.requestShare(new RequestShareCommand(
                "secret-1",
                "0xuser",
                "wallet-signature",
                "reader-public-key",
                "nonce-1"
        ));

        assertEquals("secret-1", delivery.secretId());
        assertEquals("test-node", delivery.nodeId());
        assertNotNull(delivery.protectedShare());
        assertNotNull(delivery.nodeSignature());
    }
}
