package io.custos.node.core.application.service;

import io.custos.node.core.application.exception.SecretAccessDeniedException;
import io.custos.node.core.application.exception.SecretNotFoundException;
import io.custos.node.core.application.port.in.RequestShareService;
import io.custos.node.core.application.port.in.command.RequestShareCommand;
import io.custos.node.core.application.port.out.NodeSignatureService;
import io.custos.node.core.application.port.out.SecretShareRepository;
import io.custos.node.core.application.port.out.ShareProtectionService;
import io.custos.node.core.application.port.out.WalletSignatureVerifier;
import io.custos.node.core.domain.PolicyValidationResult;
import io.custos.node.core.domain.model.ShareDelivery;
import io.custos.node.core.domain.model.StoredSecretShare;

import java.time.Instant;

public class RequestShareServiceImpl implements RequestShareService {

    private final String nodeId;
    private final SecretShareRepository repository;
    private final WalletSignatureVerifier walletSignatureVerifier;
    private final PolicyValidationService policyValidationService;
    private final WalletNonceService walletNonceService;
    private final ShareProtectionService shareProtectionService;
    private final NodeSignatureService nodeSignatureService;

    public RequestShareServiceImpl(
            String nodeId,
            SecretShareRepository repository,
            WalletSignatureVerifier walletSignatureVerifier,
            PolicyValidationService policyValidationService, WalletNonceService walletNonceService,
            ShareProtectionService shareProtectionService,
            NodeSignatureService nodeSignatureService
    ) {
        this.nodeId = nodeId;
        this.repository = repository;
        this.walletSignatureVerifier = walletSignatureVerifier;
        this.policyValidationService = policyValidationService;
        this.walletNonceService = walletNonceService;
        this.shareProtectionService = shareProtectionService;
        this.nodeSignatureService = nodeSignatureService;
    }

    @Override
    public ShareDelivery requestShare(RequestShareCommand command) {

        walletSignatureVerifier.verifyRetrieveSecretSignature(
                command.secretId(),
                command.userAddress(),
                command.nonce(),
                command.walletSignature()
        );

        walletNonceService.markNonceAsUsed(
                command.userAddress(),
                command.secretId(),
                command.nonce()
        );

        StoredSecretShare stored = repository.findBySecretId(command.secretId())
                .orElseThrow(() -> new SecretNotFoundException(command.secretId()));

        PolicyValidationResult policyValidationResult = policyValidationService.validate(stored.accessPolicy(), command.userAddress());
        if (!policyValidationResult.isValid()) {
            throw new SecretAccessDeniedException(command.secretId(), command.userAddress());
        }

        String protectedShare = shareProtectionService.protect(
                stored.encryptedShare(),
                command.readerPublicKey()
        );

        String payloadToSign = command.secretId() + ":" + command.userAddress() + ":" + protectedShare;
        String nodeSignature = nodeSignatureService.sign(payloadToSign);

        return new ShareDelivery(
                command.secretId(),
                nodeId,
                protectedShare,
                nodeSignature,
                Instant.now()
        );
    }
}
