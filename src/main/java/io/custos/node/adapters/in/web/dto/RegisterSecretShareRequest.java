package io.custos.node.adapters.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterSecretShareRequest(
        @NotBlank String secretId,
        @NotBlank String encryptedShare,
        @NotNull PolicyDto policy,
        @NotBlank String publisherAddress,
        @NotBlank String publisherSignature
) {}
