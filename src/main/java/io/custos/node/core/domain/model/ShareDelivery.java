package io.custos.node.core.domain.model;

import java.time.Instant;

public record ShareDelivery(
        String secretId,
        String nodeId,
        String protectedShare,
        String nodeSignature,
        Instant deliveredAt
) {}
