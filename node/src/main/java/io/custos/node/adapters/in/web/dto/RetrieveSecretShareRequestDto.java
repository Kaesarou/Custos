package io.custos.node.adapters.in.web.dto;

import jakarta.validation.constraints.NotBlank;

public record RetrieveSecretShareRequestDto(
        @NotBlank String userAddress,
        @NotBlank String walletSignature,
        @NotBlank String readerPublicKey,
        @NotBlank String nonce
) {
}
