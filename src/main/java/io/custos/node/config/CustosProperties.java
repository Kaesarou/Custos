package io.custos.node.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "custos")
public record CustosProperties(
        String nodeId,
        Map<Long, ChainConfig>chains
) {
    public record ChainConfig(
            String rpcUrl
    ) {
    }
}
