#!/bin/sh
set -e

mkdir -p deployments

printf "\n[Custos Test Chain] Starting local Hardhat blockchain...\n"
npx hardhat node --hostname 0.0.0.0 > /tmp/hardhat-node.log 2>&1 &

printf "[Custos Test Chain] Waiting for JSON-RPC endpoint...\n"
for i in $(seq 1 30); do
  if wget -qO- --post-data='{"jsonrpc":"2.0","method":"eth_chainId","params":[],"id":1}' \
    --header='Content-Type: application/json' http://127.0.0.1:8545 >/dev/null 2>&1; then
    break
  fi
  sleep 1
  if [ "$i" = "30" ]; then
    echo "Hardhat node did not start in time. Logs:"
    cat /tmp/hardhat-node.log
    exit 1
  fi
done

printf "[Custos Test Chain] Compiling contracts...\n"
npx hardhat compile

printf "[Custos Test Chain] Deploying generic test contracts and seed data...\n"
npx hardhat run scripts/deploy.ts --network localhost

printf "\n[Custos Test Chain] Ready. RPC URL: http://localhost:8545\n"
printf "[Custos Test Chain] Deployment file: ./deployments/deployments.json\n\n"
cat deployments/deployments.json
printf "\n\n[Custos Test Chain] Blockchain logs:\n"
tail -f /tmp/hardhat-node.log
