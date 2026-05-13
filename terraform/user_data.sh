#!/bin/bash
set -e
exec > /var/log/apoflow-setup.log 2>&1

echo "===== AOFflow Setup ― $(date) ====="

# Sistema
apt-get update -y
apt-get install -y curl git ca-certificates gnupg lsb-release

# Docker
install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg \
  | gpg --dearmor -o /etc/apt/keyrings/docker.gpg
chmod a+r /etc/apt/keyrings/docker.gpg
echo "deb [arch=amd64 signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu jammy stable" \
  > /etc/apt/sources.list.d/docker.list

apt-get update -y
apt-get install -y docker-ce docker-ce-cli containerd.io docker-compose-plugin

systemctl enable --now docker
usermod -aG docker ubuntu

# Clonar repositorio
mkdir -p /opt/apoflow
git clone https://github.com/JP18090/APOFlow.git /opt/apoflow
cd /opt/apoflow

# Variaveis de ambiente (injetadas pelo Terraform templatefile)
cat > /opt/apoflow/.env << 'ENVEOF'
MONGODB_URI=mongodb://mongodb:27017/apoflow
EMAIL_ENABLED=true
MAILERSEND_TOKEN=${mailersend_token}
MAILERSEND_FROM=${mailersend_from}
JWT_SECRET=${jwt_secret}
ENVEOF

chmod 600 /opt/apoflow/.env

# Subir aplicacao
docker compose --env-file .env up -d --build

echo "===== Setup concluido ― \$(date) ====="
