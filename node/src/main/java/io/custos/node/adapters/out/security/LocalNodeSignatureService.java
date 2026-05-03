package io.custos.node.adapters.out.security;

import io.custos.node.core.application.port.out.NodeSignatureService;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class LocalNodeSignatureService implements NodeSignatureService {

    @Override
    public String sign(String payload) {
        // TODO replace with real node private key signature
        return Base64.getEncoder().encodeToString(("signed:" + payload).getBytes(StandardCharsets.UTF_8));
    }
}
