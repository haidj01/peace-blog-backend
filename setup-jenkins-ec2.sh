#!/bin/bash

# Jenkins EC2 Setup Script
# AWS EC2 Ubuntu/Amazon Linux 인스턴스에 Jenkins 설치

set -e

echo "=========================================="
echo "Jenkins EC2 Setup Starting..."
echo "=========================================="

# 시스템 업데이트
echo "Step 1: Updating system packages..."
sudo apt-get update -y
sudo apt-get upgrade -y

# Docker 설치
echo "Step 2: Installing Docker..."
sudo apt-get install -y \
    apt-transport-https \
    ca-certificates \
    curl \
    gnupg \
    lsb-release

# Docker GPG 키 추가
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg

# Docker 저장소 추가
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu \
  $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

# Docker 설치
sudo apt-get update -y
sudo apt-get install -y docker-ce docker-ce-cli containerd.io

# Docker 서비스 시작
sudo systemctl start docker
sudo systemctl enable docker

# 현재 사용자를 docker 그룹에 추가
sudo usermod -aG docker $USER

echo "Step 3: Installing Docker Compose..."
# Docker Compose 설치
DOCKER_COMPOSE_VERSION="2.24.5"
sudo curl -L "https://github.com/docker/compose/releases/download/v${DOCKER_COMPOSE_VERSION}/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# 설치 확인
docker --version
docker-compose --version

# Git 설치
echo "Step 4: Installing Git..."
sudo apt-get install -y git

# 작업 디렉토리 생성
echo "Step 5: Creating Jenkins directory..."
mkdir -p ~/jenkins
cd ~/jenkins

# Jenkins 디렉토리 권한 설정
mkdir -p jenkins-data
sudo chown -R 1000:1000 jenkins-data

echo "=========================================="
echo "Setup completed successfully!"
echo "=========================================="
echo ""
echo "Next steps:"
echo "1. Upload docker-compose.jenkins.yml to ~/jenkins/"
echo "2. Run: docker-compose -f docker-compose.jenkins.yml up -d"
echo "3. Get initial admin password: docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword"
echo "4. Access Jenkins: http://<EC2-PUBLIC-IP>:8080"
echo ""
echo "Note: Make sure EC2 Security Group allows inbound traffic on:"
echo "  - Port 8080 (Jenkins UI)"
echo "  - Port 50000 (Jenkins agents)"
echo ""
