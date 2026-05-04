package io.custos.node.adapters.out.security;

import io.custos.node.core.application.port.out.ShareProtectionService;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class InsecureBase64ShareProtectionService implements ShareProtectionService {

    @Override
    public String protect(String encryptedShare, String readerPublicKey) {
        // TODO replace with real asymmetric encryption using readerPublicKey
        String payload = "pk=" + readerPublicKey + ";share=" + encryptedShare;
        return Base64.getEncoder().encodeToString(payload.getBytes(StandardCharsets.UTF_8));
    }
}
