package io.custos.node.adapters.in.web;

import io.custos.node.adapters.in.web.dto.*;
import io.custos.node.core.application.port.in.RegisterSecretShareService;
import io.custos.node.core.application.port.in.RequestShareService;
import io.custos.node.core.application.port.in.command.RegisterSecretShareCommand;
import io.custos.node.core.application.port.in.command.RequestShareCommand;
import io.custos.node.core.domain.model.AccessPolicy;
import io.custos.node.core.domain.model.PolicyType;
import io.custos.node.core.domain.model.ShareDelivery;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${custos.api.base-path:/api/v1}/secrets")
public class SecretShareController {

    private final RegisterSecretShareService registerSecretShareService;
    private final RequestShareService requestShareService;

    public SecretShareController(
            RegisterSecretShareService registerSecretShareService,
            RequestShareService requestShareService
    ) {
        this.registerSecretShareService = registerSecretShareService;
        this.requestShareService = requestShareService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterSecretShareResponse register(@Valid @RequestBody RegisterSecretShareRequest request) {
        registerSecretShareService.register(new RegisterSecretShareCommand(
                request.secretId(),
                request.encryptedShare(),
                toDomainPolicy(request.policy()),
                request.publisherAddress(),
                request.publisherSignature()
        ));
        return new RegisterSecretShareResponse("OK");
    }

    @PostMapping("/request-share")
    public RequestShareResponse requestShare(@Valid @RequestBody RequestShareRequest request) {
        ShareDelivery delivery = requestShareService.requestShare(new RequestShareCommand(
                request.secretId(),
                request.userAddress(),
                request.walletSignature(),
                request.readerPublicKey(),
                request.nonce()
        ));

        return new RequestShareResponse(
                delivery.secretId(),
                delivery.nodeId(),
                delivery.protectedShare(),
                delivery.nodeSignature(),
                delivery.deliveredAt().toString()
        );
    }

    private AccessPolicy toDomainPolicy(PolicyDto policyDto) {
        return new AccessPolicy(
                PolicyType.valueOf(policyDto.type()),
                policyDto.chainId(),
                policyDto.validatorContract(),
                policyDto.policyData()
        );
    }
}
