package io.custos.node.core.application.port.out;

public interface NodeSignatureService {
    String sign(String payload);
}
