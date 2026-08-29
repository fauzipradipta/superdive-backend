#!/usr/bin/env bash
# Deploy one environment on the Hostinger VPS.
#
#   ./deploy/deploy.sh dev     -> pulls `development`, rebuilds, restarts
#   ./deploy/deploy.sh prod    -> pulls `master`, rebuilds, restarts
#
# Expects the repo checked out at /opt/superdive/<env> with a filled-in
# .env.<env> sitting next to docker-compose.yml.
set -euo pipefail

ENV_NAME="${1:-}"
case "$ENV_NAME" in
	dev)  BRANCH=development ;;
	prod) BRANCH=master ;;
	*)    echo "usage: $0 {dev|prod}" >&2; exit 1 ;;
esac

cd "$(dirname "$0")/.."
ENV_FILE=".env.${ENV_NAME}"
[ -f "$ENV_FILE" ] || { echo "missing $ENV_FILE - copy .env.example and fill it in" >&2; exit 1; }

echo "==> $ENV_NAME: pulling $BRANCH"
git fetch --prune origin
git checkout "$BRANCH"
git reset --hard "origin/$BRANCH"

echo "==> $ENV_NAME: building and restarting"
docker compose --env-file "$ENV_FILE" up -d --build

# Drop the image layers the rebuild just orphaned; a small VPS disk fills up fast.
docker image prune -f

echo "==> $ENV_NAME: containers"
docker compose --env-file "$ENV_FILE" ps
