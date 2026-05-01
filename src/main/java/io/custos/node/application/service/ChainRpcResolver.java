package io.custos.node.application.service;

import io.custos.node.config.CustosProperties;

import java.util.Optional;

public class ChainRpcResolver {

    private final CustosProperties properties;

    public ChainRpcResolver(CustosProperties properties) {
        this.properties = properties;
    }

    public Optional<String> resolveRpcUrl(long chainId) {
        if (properties.chains() == null) {
            return Optional.empty();
        }

        var chainConfig = properties.chains().get(chainId);

        if (chainConfig == null || chainConfig.rpcUrl() == null || chainConfig.rpcUrl().isBlank()) {
            return Optional.empty();
        }

        return Optional.of(chainConfig.rpcUrl());
    }
}