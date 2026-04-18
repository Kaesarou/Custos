package io.custos.node.application.service;

import io.custos.node.application.exception.InvalidWalletSignatureException;
import io.custos.node.application.exception.SecretAccessDeniedException;
import io.custos.node.application.exception.SecretNotFoundException;
import io.custos.node.application.port.in.RequestShareUseCase;
import io.custos.node.application.port.in.command.RequestShareCommand;
import io.custos.node.application.port.out.AccessPolicyEvaluator;
import io.custos.node.application.port.out.NodeSignatureService;
import io.custos.node.application.port.out.SecretShareRepository;
import io.custos.node.application.port.out.ShareProtectionService;
import io.custos.node.application.port.out.WalletSignatureVerifier;
import io.custos.node.domain.model.ShareDelivery;
import io.custos.node.domain.model.StoredSecretShare;

import java.time.Instant;

public class RequestShareService implements RequestShareUseCase {

    private final String nodeId;
    private final SecretShareRepository repository;
    private final WalletSignatureVerifier walletSignatureVerifier;
    private final AccessPolicyEvaluator accessPolicyEvaluator;
    private final ShareProtectionService shareProtectionService;
    private final NodeSignatureService nodeSignatureService;

    public RequestShareService(
            String nodeId,
            SecretShareRepository repository,
            WalletSignatureVerifier walletSignatureVerifier,
            AccessPolicyEvaluator accessPolicyEvaluator,
            ShareProtectionService shareProtectionService,
            NodeSignatureService nodeSignatureService
    ) {
        this.nodeId = nodeId;
        this.repository = repository;
        this.walletSignatureVerifier = walletSignatureVerifier;
        this.accessPolicyEvaluator = accessPolicyEvaluator;
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

        boolean canAccess = accessPolicyEvaluator.canAccess(command.userAddress(), stored.accessPolicy());
        if (!canAccess) {
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
