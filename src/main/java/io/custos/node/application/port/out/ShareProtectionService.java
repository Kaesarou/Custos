package io.custos.node.application.port.out;

public interface ShareProtectionService {
    String protect(String encryptedShare, String readerPublicKey);
}
