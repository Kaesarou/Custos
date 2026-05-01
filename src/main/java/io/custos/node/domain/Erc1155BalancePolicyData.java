package io.custos.node.domain;

public record Erc1155BalancePolicyData(
        String tokenId,
        String minBalance
) {
}