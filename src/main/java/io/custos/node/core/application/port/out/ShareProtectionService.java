package io.custos.node.core.application.port.out;

public interface ShareProtectionService {
    String protect(String encryptedShare, String readerPublicKey);
}
