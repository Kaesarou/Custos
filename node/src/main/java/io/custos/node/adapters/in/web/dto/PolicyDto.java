package io.custos.node.adapters.in.web.dto;

import jakarta.validation.constraints.NotBlank;

public record PolicyDto(
        @NotBlank String type,
        long chainId,
        @NotBlank String validatorContract,
        @NotBlank String policyData
) {
}
