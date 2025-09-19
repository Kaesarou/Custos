# client.py
"""
Bob client:
- récupère capsule + ciphertext_key (on suppose qu'au moins un node l'a stocké)
- demande cfrag à chaque node via /reencrypt
- reconstruit cfrags en objects CapsuleFrag
- appelle decrypt_reencrypted(receiving_sk=bob_sk, delegating_pk=alice_pub, capsule, verified_cfrags, ciphertext)
- déchiffre le fichier AES-GCM
"""
import requests
from umbral import CapsuleFrag, decrypt_reencrypted, SecretKey
from cryptography.hazmat.primitives.ciphers.aead import AESGCM

NODES = ["http://localhost:8001", "http://localhost:8002"]
tokenId = 42

def h2b(h: str) -> bytes:
    return bytes.fromhex(h)

def main():
    # For the PoC client, you must paste Bob's private key printed by publisher
    bob_priv_hex = input("Bob private key (hex) (from publisher output):\n").strip()
    alice_delegating_pub_hex = input("Alice delegating public hex (from publisher output):\n").strip()
    capsule_hex = input("Capsule hex (from publisher output):\n").strip()
    ciphertext_hex = input("Ciphertext_key hex (from publisher output):\n").strip()

    bob_sk = SecretKey.from_bytes(h2b(bob_priv_hex)) if hasattr(SecretKey, "from_bytes") else SecretKey(h2b(bob_priv_hex))
    # Collect cfrags from each node
    cfrag_hexes = []
    for node in NODES:
        r = requests.post(node + "/reencrypt", json={"tokenId": tokenId})
        if r.status_code != 200:
            print("Node", node, "failed:", r.text)
            continue
        data = r.json()
        cfrag_hexes.append(data["cfrag_hex"])
        print("Got cfrag from", node)

    if len(cfrag_hexes) == 0:
        print("No cfrags collected")
        return

    # reconstruct CapsuleFrag objects
    verified_cfrags = []
    for ch in cfrag_hexes:
        b = h2b(ch)
        # CapsuleFrag.from_bytes
        try:
            cf = CapsuleFrag.from_bytes(b)
        except Exception:
            # fallback try direct constructor
            cf = CapsuleFrag(b)
        # NOTE: verification step could be added here using Alice's signing pubkey
        verified_cfrags.append(cf)

    # reconstruct capsule object
    # decrypt_reencrypted will expect capsule object; try to reconstruct
    from umbral import Capsule
    try:
        capsule_obj = Capsule.from_bytes(h2b(capsule_hex))
    except Exception:
        capsule_obj = Capsule(h2b(capsule_hex))

    # finally, call decrypt_reencrypted
    try:
        alice_delegating_pub = bytes.fromhex(alice_delegating_pub_hex)
        recovered_k = decrypt_reencrypted(
            receiving_sk=bob_sk,
            delegating_pk=alice_delegating_pub,
            capsule=capsule_obj,
            verified_cfrags=verified_cfrags,
            ciphertext=h2b(ciphertext_hex),
        )
    except Exception as e:
        print("decrypt_reencrypted failed:", e)
        return

    print("Recovered K_AES (hex):", recovered_k.hex())

    # decrypt file
    from pathlib import Path
    fpath = Path("encrypted_file.bin")
    if not fpath.exists():
        print("encrypted_file.bin not found locally — please copy it from publisher environment")
        return
    data = fpath.read_bytes()
    nonce = data[:12]
    ciphertext = data[12:]
    aes = AESGCM(recovered_k)
    plain = aes.decrypt(nonce, ciphertext, associated_data=None)
    print("Recovered plaintext:", plain.decode())

if __name__ == "__main__":
    main()
