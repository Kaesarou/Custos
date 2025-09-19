from flask import Flask, request, jsonify

def create_app(node):
    app = Flask(__name__)

    @app.route("/health")
    def health():
        return {"status": "ok", "node_id": node.id}

    @app.route("/cfrag", methods=["POST"])
    def get_cfrag():
        """
        Reçoit : { "capsule": ..., "bob_pubkey": ... }
        Retourne un cfrag produit par ce nœud
        """
        data = request.json
        capsule = data.get("capsule")
        bob_pubkey = data.get("bob_pubkey")

        if not capsule or not bob_pubkey:
            return jsonify({"error": "Missing capsule or bob_pubkey"}), 400

        cfrag = node.reencrypt(capsule, bob_pubkey)
        return jsonify({"cfrag": cfrag})

    return app
