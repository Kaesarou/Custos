FROM node:22-alpine

WORKDIR /app

COPY package.json package-lock.json* ./
RUN npm install

COPY hardhat.config.ts ./
COPY entrypoint.sh ./
COPY contracts ./contracts
COPY scripts ./scripts
COPY deployments ./deployments

RUN chmod +x entrypoint.sh

EXPOSE 8545

CMD ["./entrypoint.sh"]