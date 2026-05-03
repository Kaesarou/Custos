package io.custos.node.adapters.out.security;

import io.custos.node.core.application.exception.InvalidWalletSignatureException;
import io.custos.node.core.application.port.out.WalletSignatureVerifier;
import io.custos.node.core.application.service.WalletSignatureMessageBuilder;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.crypto.WalletUtils;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Service
public class WalletSignatureVerifierImpl implements WalletSignatureVerifier {

    public void verifyRetrieveSecretSignature(
            String secretId,
            String userAddress,
            String nonce,
            String walletSignature
    ) {
        validateInputs(userAddress, nonce, walletSignature);

        String message = WalletSignatureMessageBuilder.buildRetrieveSecretMessage(
                secretId,
                userAddress,
                nonce
        );

        String recoveredAddress = recoverAddress(message, walletSignature);

        if (!recoveredAddress.equalsIgnoreCase(userAddress)) {
            throw new InvalidWalletSignatureException(
                    INVALID_WALLET_SIGNATURE,
                    "Wallet signature does not match user address"
            );
        }
    }

    private void validateInputs(String userAddress, String nonce, String walletSignature) {
        if (userAddress == null || !WalletUtils.isValidAddress(userAddress)) {
            throw new InvalidWalletSignatureException(
                    INVALID_USER_ADDRESS,
                    "Invalid user address"
            );
        }

        if (nonce == null || nonce.isBlank()) {
            throw new InvalidWalletSignatureException(
                    MISSING_NONCE,
                    "Nonce is required"
            );
        }

        if (walletSignature == null || walletSignature.isBlank()) {
            throw new InvalidWalletSignatureException(
                    MISSING_WALLET_SIGNATURE,
                    "Wallet signature is required"
            );
        }
    }

    private String recoverAddress(String message, String signature) {
        try {
            byte[] signatureBytes = Numeric.hexStringToByteArray(signature);

            if (signatureBytes.length != 65) {
                throw new InvalidWalletSignatureException(
                        INVALID_WALLET_SIGNATURE,
                        "Invalid signature length"
                );
            }

            byte v = signatureBytes[64];

            if (v < 27) {
                v += 27;
            }

            byte[] r = Arrays.copyOfRange(signatureBytes, 0, 32);
            byte[] s = Arrays.copyOfRange(signatureBytes, 32, 64);

            Sign.SignatureData signatureData = new Sign.SignatureData(v, r, s);

            byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
            byte[] prefixedMessageHash = Sign.getEthereumMessageHash(messageBytes);

            BigInteger publicKey = Sign.signedMessageHashToKey(
                    prefixedMessageHash,
                    signatureData
            );

            return "0x" + Keys.getAddress(publicKey);

        } catch (InvalidWalletSignatureException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidWalletSignatureException(
                    SIGNATURE_VERIFICATION_FAILED,
                    "Unable to verify wallet signature"
            );
        }
    }
}