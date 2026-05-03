package io.custos.node.adapters.in.web.dto;

public record RetrieveSecretShareResponseDto(
        String secretId,
        String nodeId,
        String protectedShare,
        String nodeSignature,
        String deliveredAt
) {
}
