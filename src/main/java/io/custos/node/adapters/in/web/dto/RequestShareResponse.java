package io.custos.node.adapters.in.web.dto;

public record RequestShareResponse(
        String secretId,
        String nodeId,
        String protectedShare,
        String nodeSignature,
        String deliveredAt
) {}
