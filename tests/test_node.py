from custos.node import CustosNode

def test_reencrypt_stub():
    node = CustosNode()
    capsule = "fake_capsule"
    bob_key = "bob_pubkey"
    cfrag = node.reencrypt(capsule, bob_key)
    assert "cfrag_for" in cfrag
