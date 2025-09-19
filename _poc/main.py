# ursula/main.py
import os
import json
import threading
import time
from typing import Optional
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from pathlib import Path
from hashlib import sha256

# ---- Try to import pyUmbral primitives (API may vary by package/version) ----
try:
    # Typical pyUmbral-style imports used in many examples / PoC
    from umbral import (
        SecretKey,
        Signer,
        encrypt,
        decrypt_original,
        generate_kfrags,
        reencrypt,
        decrypt_reencrypted,
        CapsuleFrag,
    )
    # Capsule and KFrag classes (may be available under different modules in some versions)
    try:
        from umbral import Capsule, KFrag
    except Exception:
        # fallback import paths
        from umbral.capsule import Capsule  # type: ignore
        from umbral.kfrag import KFrag  # type: ignore
except Exception as e:
    raise RuntimeError(
        "Impossible d'importer pyUmbral (umbral). Assure-toi d'avoir 'pyumbral' installé. Error: "
        + str(e)
    )

DATA_DIR = Path(os.getenv("DATA_DIR", "/data"))
DATA_DIR.mkdir(parents=True, exist_ok=True)
STORAGE_FILE = DATA_DIR / "store.json"

# minimal persistent store (JSON) to keep PoC simple
_store_lock = threading.Lock()
if not STORAGE_FILE.exists():
    STORAGE_FILE.write_text(json.dumps({"capsules": {}, "kfrags": {}, "meta": {}, "pending": {}}))

def _load_store():
    with _store_lock:
        with open(STORAGE_FILE, "r") as f:
            return json.load(f)

def _save_store(s):
    with _store_lock:
        with open(STORAGE_FILE, "w") as f:
            json.dump(s, f, indent=2)

# Convert bytes <-> hex helpers
def b2h(b: bytes) -> str:
    return b.hex()

def h2b(h: str) -> bytes:
    return bytes.fromhex(h)

# FastAPI app
app = FastAPI(title="Ursula PoC (Ursula+Watcher)")

# -------------------------
# Models
# -------------------------
class AddDelegationIn(BaseModel):
    tokenId: int
    capsule_hex: str              # bytes(capsule).hex()
    ciphertext_hex: str           # bytes(ciphertext_key).hex()
    kfrag_hex: Optional[str] = None   # bytes(kfrag).hex() — one kfrag for this node
    delegating_pub_hex: Optional[str] = None
    signing_pub_hex: Optional[str] = None

class ReencryptRequest(BaseModel):
    tokenId: int
    # for the PoC we won't strictly enforce on-chain owner checks,
    # but you may pass owner_address if you want the node to check on-chain later
    owner_address: Optional[str] = None
    # capsule/ciphertext can be pulled from stored capsule if present on node.
    capsule_hex: Optional[str] = None
    ciphertext_hex: Optional[str] = None

class SetMasterKeyIn(BaseModel):
    # WARNING: insecure for production; for PoC only
    delegating_priv_hex: str   # bytes of delegating SecretKey
    signing_priv_hex: str      # bytes of signing SecretKey

# -------------------------
# Storage helpers (in-memory objects)
# -------------------------
# For PoC we keep in-memory reconstructed objects (KFrag) for quick reencrypt.
# On restart nodes will lose the in-memory kfrag objects; but persistent bytes remain in JSON store.
_INMEMORY_KFRAGS = {}  # tokenId -> list of KFrag objects (in-memory)
# master key (unsafe) kept in memory if set
_MASTER_KEYS = {}  # 'delegating_priv' & 'signing_priv' stored as bytes -> SecretKey objects

def store_capsule_data(tokenId: int, capsule_hex: str, ciphertext_hex: str, delegating_pub_hex=None, signing_pub_hex=None):
    s = _load_store()
    s["capsules"][str(tokenId)] = {
        "capsule_hex": capsule_hex,
        "ciphertext_hex": ciphertext_hex,
        "delegating_pub_hex": delegating_pub_hex,
        "signing_pub_hex": signing_pub_hex,
    }
    _save_store(s)

def store_kfrag_bytes(tokenId: int, kfrag_hex: str):
    s = _load_store()
    kf_list = s["kfrags"].get(str(tokenId), [])
    kf_list.append(kfrag_hex)
    s["kfrags"][str(tokenId)] = kf_list
    _save_store(s)
    # try to reconstruct into memory if possible
    try:
        b = h2b(kfrag_hex)
        # attempt several ways to reconstruct a KFrag
        kfrag_obj = None
        if hasattr(KFrag, "from_bytes"):
            kfrag_obj = KFrag.from_bytes(b)
        elif hasattr(KFrag, "deserialize"):
            kfrag_obj = KFrag.deserialize(b)
        else:
            # try generic constructor fallback (may fail on some versions)
            kfrag_obj = KFrag(b)
        _INMEMORY_KFRAGS.setdefault(str(tokenId), []).append(kfrag_obj)
    except Exception as e:
        # We still saved the raw bytes; reconstruction may be required later.
        print("[warn] could not reconstruct kfrag in-memory:", e)

def get_capsule_record(tokenId: int):
    s = _load_store()
    return s["capsules"].get(str(tokenId))

def get_kfrag_objects(tokenId: int):
    # return in-memory objects if available, else try reconstructing from store
    token_key = str(tokenId)
    if token_key in _INMEMORY_KFRAGS:
        return _INMEMORY_KFRAGS[token_key]
    s = _load_store()
    hexes = s["kfrags"].get(token_key, [])
    objs = []
    for h in hexes:
        try:
            b = h2b(h)
            if hasattr(KFrag, "from_bytes"):
                kfrag_obj = KFrag.from_bytes(b)
            elif hasattr(KFrag, "deserialize"):
                kfrag_obj = KFrag.deserialize(b)
            else:
                kfrag_obj = KFrag(b)
            objs.append(kfrag_obj)
        except Exception as e:
            print("[warn] cannot reconstruct kfrag from hex:", e)
    if objs:
        _INMEMORY_KFRAGS[token_key] = objs
    return objs

# -------------------------
# Endpoints
# -------------------------
@app.post("/add_delegation")
async def add_delegation(body: AddDelegationIn):
    """
    Store capsule and a single kfrag for tokenId.
    The publisher is expected to call this on *each* node, distributing one kfrag per node.
    """
    # store capsule/ciphertext
    store_capsule_data(body.tokenId, body.capsule_hex, body.ciphertext_hex, body.delegating_pub_hex, body.signing_pub_hex)
    # store kfrag bytes (if present)
    if body.kfrag_hex:
        store_kfrag_bytes(body.tokenId, body.kfrag_hex)
    return {"ok": True, "tokenId": body.tokenId, "stored_kfrag": bool(body.kfrag_hex)}

@app.post("/store_capsule")
async def store_capsule(body: AddDelegationIn):
    """Store only capsule metadata (publisher may call separately)."""
    store_capsule_data(body.tokenId, body.capsule_hex, body.ciphertext_hex, body.delegating_pub_hex, body.signing_pub_hex)
    return {"ok": True}

@app.post("/set_master_key")
async def set_master_key(body: SetMasterKeyIn):
    """
    WARNING: insecure for production. For PoC only.
    Allows the node itself to generate kfrags on Transfer events if configured.
    """
    try:
        b1 = h2b(body.delegating_priv_hex)
        b2 = h2b(body.signing_priv_hex)
        # attempt reconstruction of SecretKey
        if hasattr(SecretKey, "from_bytes"):
            deleg_sk = SecretKey.from_bytes(b1)
            signing_sk = SecretKey.from_bytes(b2)
        else:
            # fallback: try constructing directly (pyUmbral varies)
            deleg_sk = SecretKey(b1)
            signing_sk = SecretKey(b2)
        _MASTER_KEYS["delegating_priv"] = deleg_sk
        _MASTER_KEYS["signing_priv"] = signing_sk
        return {"ok": True}
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Could not set master key: {e}")

@app.get("/meta/{tokenId}")
async def get_meta(tokenId: int):
    rec = get_capsule_record(tokenId)
    if not rec:
        raise HTTPException(404, "tokenId not found")
    return rec

@app.post("/reencrypt")
async def reencrypt_endpoint(req: ReencryptRequest):
    """
    Client (Bob) requests a cfrag from this Ursula.
    Node will:
    - look up stored capsule
    - use one stored kfrag (this node's share) to re-encrypt the capsule -> cfrag
    - return cfrag bytes hex
    """
    rec = get_capsule_record(req.tokenId)
    if not rec:
        raise HTTPException(404, "No capsule for this tokenId on this node")

    # If capsule provided in request, prefer it (useful for testing)
    capsule_hex = req.capsule_hex or rec["capsule_hex"]
    ciphertext_hex = req.ciphertext_hex or rec["ciphertext_hex"]

    # reconstruct capsule object
    try:
        capsule_bytes = h2b(capsule_hex)
        if hasattr(Capsule, "from_bytes"):
            capsule_obj = Capsule.from_bytes(capsule_bytes)
        elif hasattr(Capsule, "deserialize"):
            capsule_obj = Capsule.deserialize(capsule_bytes)
        else:
            # fallback: attempt direct constructor (may fail)
            capsule_obj = Capsule(capsule_bytes)
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Cannot reconstruct capsule: {e}")

    # fetch one kfrag for this token from in-memory or store
    kfrags = get_kfrag_objects(req.tokenId)
    if not kfrags:
        raise HTTPException(status_code=404, detail="No kfrag available on this node for this tokenId")

    # choose the first kfrag (in real system each node has its own kfrag)
    kfrag = kfrags[0]

    # perform re-encryption -> produce a cfrag (capsule fragment)
    try:
        cfrag = reencrypt(capsule=capsule_obj, kfrag=kfrag)
        # return bytes(cfrag).hex()
        return {"cfrag_hex": b2h(bytes(cfrag)), "tokenId": req.tokenId}
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"reencrypt failed: {e}")

@app.get("/health")
async def health():
    return {"ok": True}

# -------------------------
# Watcher thread (optional)
# -------------------------
# For PoC we only provide a placeholder watcher that can be enabled via env vars.
# It logs that it listens for events; if you supply a real RPC_URL and CONTRACT_ADDRESS
# you can extend this to react automatically and call generate_kfrags etc.
def watcher_loop():
    RPC = os.getenv("RPC_URL")
    CONTRACT = os.getenv("CONTRACT_ADDRESS")
    POLL = int(os.getenv("WATCHER_POLL", "8"))
    if not RPC or not CONTRACT:
        print("[watcher] RPC_URL or CONTRACT_ADDRESS not set — watcher disabled")
        return
    print("[watcher] starting (PoC) — will poll logs and print Transfer events")
    try:
        from web3 import Web3
        w3 = Web3(Web3.HTTPProvider(RPC))
        # Minimal ERC721 ABI to read Transfer events
        abi = [
            {
                "anonymous": False,
                "inputs": [
                    {"indexed": True, "name": "from", "type": "address"},
                    {"indexed": True, "name": "to", "type": "address"},
                    {"indexed": True, "name": "tokenId", "type": "uint256"},
                ],
                "name": "Transfer",
                "type": "event",
            }
        ]
        contract = w3.eth.contract(address=Web3.toChecksumAddress(CONTRACT), abi=abi)
        last_block = w3.eth.block_number
        while True:
            latest = w3.eth.block_number
            if latest > last_block:
                from_block = last_block + 1
                to_block = latest
                events = contract.events.Transfer().getLogs(fromBlock=from_block, toBlock=to_block)
                for ev in events:
                    print("[watcher] Transfer event:", ev["args"])
                last_block = latest
            time.sleep(POLL)
    except Exception as e:
        print("[watcher] watcher error (disabled):", e)

# Start watcher thread after app startup
def start_watcher_background():
    t = threading.Thread(target=watcher_loop, daemon=True)
    t.start()

@app.on_event("startup")
def startup_event():
    print("[startup] Ursula node starting...")
    start_watcher_background()
