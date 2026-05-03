# Custos Test Chain

Petit environnement Docker prêt à l'emploi pour tester l'intégration blockchain du noeud Custos.

Le projet est volontairement agnostique de Liber : il déploie uniquement des contrats génériques permettant de tester des `AccessPolicy` on-chain.

## Contenu

- Blockchain locale Hardhat exposée sur `http://localhost:8545`
- Contrat ERC721 minimal : `TestERC721`
- Contrat ERC1155 minimal : `TestERC1155`
- Contrat ERC20 minimal : `TestERC20`
- Script de déploiement automatique au démarrage
- Seed automatique de quelques comptes avec tokens/NFT
- Export des adresses dans `deployments/deployments.json`

## Lancer

```bash
docker compose up --build
```

À la fin du boot, le conteneur affiche les adresses des contrats et écrit le fichier :

```txt
deployments/deployments.json
```

## Infos RPC

Depuis ta machine :

```txt
RPC_URL=http://localhost:8545
CHAIN_ID=31337
```

Depuis un autre conteneur Docker sur le même réseau compose :

```txt
RPC_URL=http://custos-test-chain:8545
CHAIN_ID=31337
```

## Policies d'exemple

Le fichier `deployments/deployments.json` contient des exemples de policies :

```json
{
  "type": "EVM_ERC1155_BALANCE",
  "chainId": 31337,
  "contract": "0x...",
  "holder": "0x...",
  "tokenId": "1",
  "minAmount": "1"
}
```

```json
{
  "type": "EVM_ERC721_OWNERSHIP",
  "chainId": 31337,
  "contract": "0x...",
  "holder": "0x...",
  "tokenId": "1"
}
```

```json
{
  "type": "EVM_ERC20_BALANCE",
  "chainId": 31337,
  "contract": "0x...",
  "holder": "0x...",
  "minAmountWei": "100000000000000000000"
}
```

## Tester les lectures on-chain

Dans un second terminal :

```bash
docker exec -it custos-test-chain npm run test:policy
```

Ce script vérifie quelques conditions simples avec des appels `balanceOf` / `ownerOf`.

## Règle d'architecture conservée

```txt
Application métier ---> Custos
Custos ------------X-> Application métier
```

Custos ne connaît pas Liber. Il évalue uniquement des policies génériques basées sur des contrats et standards blockchain.
