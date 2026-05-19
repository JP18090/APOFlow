#!/bin/sh
set -eu

SITE_ADDRESS="${APOFLOW_SITE_ADDRESS:-localhost}"
TLS_MODE="${APOFLOW_TLS_MODE:-internal}"
TLS_EMAIL="${TLS_EMAIL:-}"

mkdir -p /etc/caddy

if [ "$TLS_MODE" = "public" ]; then
  if [ -z "$TLS_EMAIL" ]; then
    echo "TLS_EMAIL is required when APOFLOW_TLS_MODE=public" >&2
    exit 1
  fi

  cat > /etc/caddy/Caddyfile <<EOF
{
  email ${TLS_EMAIL}
}

${SITE_ADDRESS} {
  encode zstd gzip
  reverse_proxy apoflow:8080
}
EOF
else
  cat > /etc/caddy/Caddyfile <<EOF
http://${SITE_ADDRESS}, https://${SITE_ADDRESS} {
  tls internal
  encode zstd gzip
  reverse_proxy apoflow:8080
}
EOF
fi

exec caddy run --config /etc/caddy/Caddyfile --adapter caddyfile
