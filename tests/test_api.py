from custos.api import create_app
from custos.node import CustosNode

def test_health():
    node = CustosNode()
    app = create_app(node)
    client = app.test_client()

    resp = client.get("/health")
    assert resp.status_code == 200
    assert "status" in resp.json
