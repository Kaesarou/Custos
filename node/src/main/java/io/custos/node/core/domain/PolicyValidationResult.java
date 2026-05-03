package io.custos.node.core.domain;

public record PolicyValidationResult(
        boolean isValid,
        String reason
) {
    public static PolicyValidationResult valid() {
        return new PolicyValidationResult(true, null);
    }

    public static PolicyValidationResult invalid(String reason) {
        return new PolicyValidationResult(false, reason);
    }
}