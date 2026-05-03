package io.custos.node.core.domain;

public record Erc1155BalancePolicyData(
        String tokenId,
        String minBalance
) {
}