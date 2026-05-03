package io.custos.node.core.domain;

public record EvmErc1155BalancePolicyData(
        String tokenId,
        String minBalance
) {
}