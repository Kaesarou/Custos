import os

class Config:
    NODE_ID = os.getenv("NODE_ID", "default-node")
    RPC_URL = os.getenv("RPC_URL", "http://host.docker.internal:8545")
    CONTRACT_ADDRESS = os.getenv("CONTRACT_ADDRESS", None)
    CONTRACT_ABI = os.getenv("CONTRACT_ABI", None)
