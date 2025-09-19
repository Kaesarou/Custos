import uuid

class CustosNode:
    """
    Représente un nœud Custos (Ursula simplifiée).
    """

    def __init__(self):
        self.id = str(uuid.uuid4())

    def start(self):
        print(f"[CustosNode] Node {self.id} started")

    def reencrypt(self, capsule, bob_pubkey):
        """
        Stub pour proxy re-encryption avec pyUmbral.
        """
        # TODO: brancher pyUmbral
        return f"cfrag_for_{bob_pubkey}_from_capsule_{capsule[:10]}..."
