package io.custos.node.application.service;

import io.custos.node.application.exception.InvalidPublisherSignatureException;
import io.custos.node.application.port.in.command.RegisterSecretShareCommand;
import io.custos.node.application.port.out.PublisherSignatureVerifier;
import io.custos.node.application.port.out.SecretShareRepository;
import io.custos.node.domain.model.StoredSecretShare;

import java.time.Instant;

public class RegisterSecretShareServiceImpl implements io.custos.node.application.port.in.RegisterSecretShareService {

    private final SecretShareRepository repository;
    private final PublisherSignatureVerifier publisherSignatureVerifier;

    public RegisterSecretShareServiceImpl(
            SecretShareRepository repository,
            PublisherSignatureVerifier publisherSignatureVerifier
    ) {
        this.repository = repository;
        this.publisherSignatureVerifier = publisherSignatureVerifier;
    }

    @Override
    public void register(RegisterSecretShareCommand command) {
        if (!publisherSignatureVerifier.isValid(command)) {
            throw new InvalidPublisherSignatureException();
        }

        repository.save(new StoredSecretShare(
                command.secretId(),
                command.encryptedShare(),
                command.accessPolicy(),
                command.publisherAddress(),
                Instant.now()
        ));
    }
}
