# Custos Node

Squelette d'un nœud **Custos Protocol** en **Java Spring Boot** avec **architecture hexagonale**.

## Objectif de cette première version

Cette V0.1 pose les bases d'un nœud capable de :

- enregistrer un share pour un `secretId`
- stocker une policy d'accès
- recevoir une requête de délivrance
- vérifier (pour l'instant via un stub) si l'utilisateur peut accéder au secret
- renvoyer un share "protégé" au lecteur

> Cette version est volontairement minimaliste : elle privilégie la structure et les points d'extension.

## Architecture

```text
domain/        -> modèles et règles métier
application/   -> cas d'usage + ports
adapters/in    -> API REST
adapters/out   -> persistance / policy / crypto / blockchain
config/        -> wiring Spring
```

## Endpoints

- `POST /api/v1/secrets/register`
- `POST /api/v1/secrets/request-share`
- `GET /actuator/health`

## Lancer

```bash
mvn spring-boot:run
```

## Prochaines étapes

- persistance PostgreSQL
- chiffrement réel du share avec la clé publique éphémère du lecteur
- intégration validator contract / RPC Ethereum
- signatures des nœuds
- rewards / receipts
- resharing distribué
