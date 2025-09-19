from custos.api import create_app
from custos.watcher import Watcher
from custos.node import CustosNode

def main():
    # Initialise le nœud
    node = CustosNode()
    node.start()

    # Démarre le watcher blockchain (factice pour l’instant)
    watcher = Watcher(node)
    watcher.start()

    # Lance l’API REST
    app = create_app(node)
    app.run(host="0.0.0.0", port=8000)

if __name__ == "__main__":
    main()
