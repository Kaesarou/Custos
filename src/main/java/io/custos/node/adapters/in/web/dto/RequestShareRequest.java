package io.custos.node.adapters.in.web.dto;

import jakarta.validation.constraints.NotBlank;

public record RequestShareRequest(
        @NotBlank String secretId,
        @NotBlank String userAddress,
        @NotBlank String walletSignature,
        @NotBlank String readerPublicKey,
        @NotBlank String nonce
) {}
