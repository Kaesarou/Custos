package io.custos.node.adapters.in.web;

import io.custos.node.adapters.in.web.dto.PolicyDto;
import io.custos.node.adapters.in.web.dto.RegisterSecretShareRequest;
import io.custos.node.adapters.in.web.dto.RegisterSecretShareResponse;
import io.custos.node.adapters.in.web.dto.RequestShareRequest;
import io.custos.node.adapters.in.web.dto.RequestShareResponse;
import io.custos.node.application.port.in.RegisterSecretShareUseCase;
import io.custos.node.application.port.in.RequestShareUseCase;
import io.custos.node.application.port.in.command.RegisterSecretShareCommand;
import io.custos.node.application.port.in.command.RequestShareCommand;
import io.custos.node.domain.model.AccessPolicy;
import io.custos.node.domain.model.PolicyType;
import io.custos.node.domain.model.ShareDelivery;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${custos.api.base-path:/api/v1}/secrets")
public class SecretShareController {

    private final RegisterSecretShareUseCase registerSecretShareUseCase;
    private final RequestShareUseCase requestShareUseCase;

    public SecretShareController(
            RegisterSecretShareUseCase registerSecretShareUseCase,
            RequestShareUseCase requestShareUseCase
    ) {
        this.registerSecretShareUseCase = registerSecretShareUseCase;
        this.requestShareUseCase = requestShareUseCase;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterSecretShareResponse register(@Valid @RequestBody RegisterSecretShareRequest request) {
        registerSecretShareUseCase.register(new RegisterSecretShareCommand(
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
        ShareDelivery delivery = requestShareUseCase.requestShare(new RequestShareCommand(
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
