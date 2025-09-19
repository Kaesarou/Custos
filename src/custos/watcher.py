import threading
import time

class Watcher(threading.Thread):
    """
    Écoute la blockchain pour réagir aux événements de transfert/mint.
    Pour le POC, on simule juste un poll.
    """

    def __init__(self, node):
        super().__init__(daemon=True)
        self.node = node

    def run(self):
        print("[Watcher] started, listening for blockchain events...")
        while True:
            # TODO: remplacer par vrai event listening (via Web3)
            time.sleep(10)
            print(f"[Watcher] Node {self.node.id} checking events...")
