package io.custos.node.application.service;

import io.custos.node.application.exception.InvalidWalletSignatureException;
import io.custos.node.application.exception.SecretAccessDeniedException;
import io.custos.node.application.exception.SecretNotFoundException;
import io.custos.node.application.port.in.command.RequestShareCommand;
import io.custos.node.application.port.out.*;
import io.custos.node.domain.PolicyValidationResult;
import io.custos.node.domain.model.ShareDelivery;
import io.custos.node.domain.model.StoredSecretShare;

import java.time.Instant;

public class RequestShareServiceImpl implements io.custos.node.application.port.in.RequestShareService {

    private final String nodeId;
    private final SecretShareRepository repository;
    private final WalletSignatureVerifier walletSignatureVerifier;
    private final PolicyValidationService policyValidationService;
    private final ShareProtectionService shareProtectionService;
    private final NodeSignatureService nodeSignatureService;

    public RequestShareServiceImpl(
            String nodeId,
            SecretShareRepository repository,
            WalletSignatureVerifier walletSignatureVerifier,
             PolicyValidationService policyValidationService,
            ShareProtectionService shareProtectionService,
            NodeSignatureService nodeSignatureService
    ) {
        this.nodeId = nodeId;
        this.repository = repository;
        this.walletSignatureVerifier = walletSignatureVerifier;
        this.policyValidationService = policyValidationService;
        this.shareProtectionService = shareProtectionService;
        this.nodeSignatureService = nodeSignatureService;
    }

    @Override
    public ShareDelivery requestShare(RequestShareCommand command) {
        if (!walletSignatureVerifier.isValid(command)) {
            throw new InvalidWalletSignatureException();
        }

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
