package io.custos.node.application.port.out;

public interface NodeSignatureService {
    String sign(String payload);
}
