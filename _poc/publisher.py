# publisher.py
"""
Publisher script (PoC):
- génère Alice (delegator) keys, Bob (recipient) keys,
- crée K_AES, chiffre un petit "fichier",
- encapsule K_AES via Umbral -> capsule + ciphertext_key
- génère kfrags (shares) pour Bob (threshold=2, shares=2)
- poste pour chaque node le capsule + ciphertext + 1 kfrag (chaque node reçoit 1 share)
"""
import requests, json, sys, os
from umbral import SecretKey, Signer, encrypt, generate_kfrags, AESGCM  # if AESGCM not here, use cryptography
from cryptography.hazmat.primitives.ciphers.aead import AESGCM
import base64

# CONFIG: endpoints of two nodes
NODES = ["http://localhost:8001", "http://localhost:8002"]

def b2h(b: bytes) -> str:
    return b.hex()

def main():
    # 1) keys
    alice_sk = SecretKey.random()
    alice_pk = alice_sk.public_key()
    alice_signing_sk = SecretKey.random()
    alice_signer = Signer(alice_signing_sk)
    alice_signing_pk = alice_signing_sk.public_key()

    bob_sk = SecretKey.random()
    bob_pk = bob_sk.public_key()

    print("Alice public (bytes hex):", b2h(bytes(alice_pk)))
    print("Alice signing pub (hex):", b2h(bytes(alice_signing_pk)))
    print("Bob public (hex):", b2h(bytes(bob_pk)))

    # 2) K_AES and AES-GCM encrypt a sample plaintext
    plaintext = b"Texte secret - Chapitre 1 - PoC PRE"
    K_AES = AESGCM.generate_key(bit_length=256)
    aesgcm = AESGCM(K_AES)
    nonce = os.urandom(12)
    ciphertext_file = aesgcm.encrypt(nonce=nonce, data=plaintext, associated_data=None)
    # For PoC we store encrypted file locally
    with open("encrypted_file.bin", "wb") as f:
        f.write(nonce + ciphertext_file)

    # 3) encapsulate K_AES with Umbral under Alice's public key -> capsule + ciphertext_key
    capsule, ciphertext_key = encrypt(alice_pk, K_AES)
    print("Capsule bytes len:", len(bytes(capsule)))

    # 4) generate kfrags (threshold=2, shares=2)
    threshold = 2
    shares = 2
    kfrags = generate_kfrags(
        delegating_sk=alice_sk,
        receiving_pk=bob_pk,
        signer=alice_signer,
        threshold=threshold,
        shares=shares,
    )
    print("Generated kfrags:", len(kfrags))

    # 5) POST to nodes (one kfrag per node) + capsule + ciphertext_key + pubs
    for i, node in enumerate(NODES):
        kfrag = kfrags[i]
        payload = {
            "tokenId": 42,
            "capsule_hex": b2h(bytes(capsule)),
            "ciphertext_hex": b2h(bytes(ciphertext_key)),
            "kfrag_hex": b2h(bytes(kfrag)),
            "delegating_pub_hex": b2h(bytes(alice_pk)),
            "signing_pub_hex": b2h(bytes(alice_signing_pk)),
        }
        r = requests.post(node + "/add_delegation", json=payload)
        print(node, r.status_code, r.text)

    # last: print helpful info for client
    print("\n--- SAVE THESE (client will need them) ---")
    print("Alice delegating pub hex:", b2h(bytes(alice_pk)))
    print("Alice signing pub hex:", b2h(bytes(alice_signing_pk)))
    print("Bob private key (KEEP PRIVATE) hex:", b2h(bytes(bob_sk)))
    print("Capsule hex:", b2h(bytes(capsule)))
    print("Ciphertext_key hex:", b2h(bytes(ciphertext_key)))
    print("Encrypted file saved as encrypted_file.bin (nonce + ciphertext)")

if __name__ == "__main__":
    main()
