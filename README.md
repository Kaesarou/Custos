# Custos

Custos est un projet expérimental de **réseau décentralisé de proxy re-encryption** basé sur [pyUmbral](https://github.com/nucypher/pyUmbral).  

Chaque nœud Custos (appelé "Ursula") :
- Écoute les événements blockchain (mint, transfert).
- Expose une API REST pour générer et servir des **c_frags**.
- Peut être lancé facilement via Docker et rejoindre le réseau en un clic.

## 🚀 Lancer un POC local avec 2 nœuds

```bash
docker-compose -f docker/docker-compose.yml up --build
