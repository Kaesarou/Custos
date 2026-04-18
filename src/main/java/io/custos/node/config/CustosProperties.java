package io.custos.node.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "custos")
public record CustosProperties(
        String nodeId
) {}
