# AWS EC2에 Jenkins 서버 구축 가이드

이 가이드는 AWS EC2 인스턴스에 Docker를 사용하여 Jenkins를 설치하는 방법을 설명합니다.

## 1. AWS EC2 인스턴스 생성

### 인스턴스 사양 권장사항
- **AMI**: Ubuntu Server 22.04 LTS
- **인스턴스 타입**:
  - 개발/테스트: `t3.medium` (2 vCPU, 4GB RAM)
  - 프로덕션: `t3.large` 이상 (2 vCPU, 8GB RAM)
- **스토리지**: 30GB 이상 (GP3 권장)
- **키 페어**: 새로 생성하거나 기존 키 사용

### 보안 그룹 설정

다음 인바운드 규칙을 추가하세요:

| 타입 | 프로토콜 | 포트 범위 | 소스 | 설명 |
|------|----------|-----------|------|------|
| SSH | TCP | 22 | My IP | SSH 접속 |
| Custom TCP | TCP | 8080 | 0.0.0.0/0 | Jenkins 웹 UI |
| Custom TCP | TCP | 50000 | 0.0.0.0/0 | Jenkins 에이전트 |

> **보안 권장사항**: 프로덕션 환경에서는 Jenkins UI(8080)를 특정 IP로 제한하거나 ALB + HTTPS를 사용하세요.

## 2. EC2 인스턴스에 연결

```bash
# 키 파일 권한 설정
chmod 400 your-key.pem

# SSH 접속
ssh -i your-key.pem ubuntu@<EC2-PUBLIC-IP>
```

## 3. Jenkins 설치

### 방법 1: 자동 설치 스크립트 사용

```bash
# 설치 스크립트 다운로드 (로컬에서 업로드 또는 직접 생성)
curl -O https://your-repo/setup-jenkins-ec2.sh

# 실행 권한 부여
chmod +x setup-jenkins-ec2.sh

# 스크립트 실행
./setup-jenkins-ec2.sh

# 그룹 변경사항 적용을 위해 재로그인
exit
ssh -i your-key.pem ubuntu@<EC2-PUBLIC-IP>
```

### 방법 2: 수동 설치

자세한 내용은 `setup-jenkins-ec2.sh` 파일 참조

## 4. Jenkins 실행

```bash
# Jenkins 디렉토리로 이동
cd ~/jenkins

# docker-compose.jenkins.yml 파일 업로드
# (로컬에서 scp로 업로드하거나 직접 생성)

# Jenkins 컨테이너 시작
docker-compose -f docker-compose.jenkins.yml up -d

# 로그 확인
docker-compose -f docker-compose.jenkins.yml logs -f jenkins
```

## 5. Jenkins 초기 설정

### 초기 관리자 비밀번호 확인

```bash
docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
```

### 웹 브라우저로 접속

1. 브라우저에서 `http://<EC2-PUBLIC-IP>:8080` 접속
2. 초기 관리자 비밀번호 입력
3. "Install suggested plugins" 선택
4. 관리자 계정 생성

## 6. Jenkins 플러그인 설치

다음 플러그인들을 설치하세요:

1. **필수 플러그인**
   - Git
   - Pipeline
   - Docker Pipeline
   - Gradle
   - Blue Ocean (선택사항)

2. **Jenkins 관리** → **Plugins** → **Available plugins**에서 검색 및 설치

## 7. Jenkins 도구 설정

### JDK 설정
1. **Jenkins 관리** → **Tools** → **JDK installations**
2. "Add JDK" 클릭
3. Name: `JDK 17`
4. "Install automatically" 체크
5. "Add Installer" → "Install from adoptium.net" 선택

### Gradle 설정
1. **Jenkins 관리** → **Tools** → **Gradle installations**
2. "Add Gradle" 클릭
3. Name: `Gradle 8.5`
4. "Install automatically" 체크
5. Version: `8.5` 선택

## 8. GitHub 연동 설정

### GitHub Credentials 추가

1. **Jenkins 관리** → **Credentials** → **System** → **Global credentials**
2. "Add Credentials" 클릭
3. Kind: `Username with password` 또는 `SSH Username with private key`
4. Username: GitHub 사용자명
5. Password/Private Key: GitHub Personal Access Token 또는 SSH 키
6. ID: `git-credentials`

### GitHub Personal Access Token 생성
1. GitHub → Settings → Developer settings → Personal access tokens
2. "Generate new token (classic)" 클릭
3. 권한 선택: `repo`, `admin:repo_hook`
4. 토큰 복사 후 Jenkins에 등록

## 9. Pipeline 생성

### Multibranch Pipeline 생성

1. Jenkins 대시보드 → **New Item**
2. 이름 입력: `peace-blog-backend`
3. **Multibranch Pipeline** 선택
4. **Branch Sources** → **Add source** → **Git**
5. Project Repository: `https://github.com/your-username/peace-blog-backend.git`
6. Credentials: `git-credentials` 선택
7. **Scan Multibranch Pipeline Triggers** 체크
8. Save

## 10. Docker 설정 (선택사항)

Jenkins에서 Docker 명령을 실행하려면:

```bash
# Jenkins 컨테이너에 Docker 설치
docker exec -it -u root jenkins bash

# 컨테이너 내부에서
apt-get update
apt-get install -y docker.io

# 또는 docker-compose.jenkins.yml에서 docker-dind 서비스 사용
```

## 11. 환경 변수 설정

민감한 정보는 Jenkins Credentials로 관리:

1. **Jenkins 관리** → **Credentials**
2. 다음 Credentials 추가:
   - `git-credentials`: GitHub 접근
   - `docker-credentials`: Docker Registry 접근 (필요시)
   - `db-credentials`: 데이터베이스 접근 정보

## 12. 백업 설정

### Jenkins 데이터 백업

```bash
# jenkins_home 볼륨 백업
docker run --rm -v jenkins_home:/data -v $(pwd):/backup \
  ubuntu tar czf /backup/jenkins-backup-$(date +%Y%m%d).tar.gz /data

# S3로 백업 업로드 (선택사항)
aws s3 cp jenkins-backup-*.tar.gz s3://your-backup-bucket/
```

### 자동 백업 스크립트 (cron)

```bash
# crontab 편집
crontab -e

# 매일 새벽 2시에 백업 실행
0 2 * * * /home/ubuntu/jenkins/backup-jenkins.sh
```

## 13. 모니터링 및 유지보수

### Jenkins 로그 확인

```bash
# 실시간 로그
docker-compose -f docker-compose.jenkins.yml logs -f jenkins

# 특정 라인 수만큼 로그
docker-compose -f docker-compose.jenkins.yml logs --tail 100 jenkins
```

### 컨테이너 상태 확인

```bash
docker-compose -f docker-compose.jenkins.yml ps
```

### Jenkins 재시작

```bash
docker-compose -f docker-compose.jenkins.yml restart jenkins
```

### Jenkins 업데이트

```bash
# 최신 이미지 다운로드
docker-compose -f docker-compose.jenkins.yml pull

# 컨테이너 재생성
docker-compose -f docker-compose.jenkins.yml up -d
```

## 14. 보안 강화 (프로덕션)

### HTTPS 설정 (ALB 사용)

1. AWS Application Load Balancer 생성
2. ACM으로 SSL 인증서 발급
3. ALB에서 HTTPS(443) → EC2(8080) 포워딩
4. Jenkins Security Group을 ALB에서만 접근 가능하도록 수정

### 보안 그룹 최소화

```bash
# SSH는 특정 IP만 허용
Source: Your-Office-IP/32

# Jenkins는 ALB에서만 접근
Source: ALB-Security-Group
```

## 15. 비용 최적화

### EC2 인스턴스 스케줄링

개발 환경의 경우 야간/주말에 인스턴스 중지:

```bash
# AWS Lambda + EventBridge로 자동 시작/중지 설정
# 또는 Instance Scheduler 사용
```

### Spot Instance 활용

개발 환경에서는 Spot Instance 사용으로 비용 절감 가능

## 트러블슈팅

### Jenkins가 시작되지 않는 경우

```bash
# 로그 확인
docker logs jenkins

# 디스크 공간 확인
df -h

# 메모리 확인
free -m
```

### Docker 권한 오류

```bash
# 사용자를 docker 그룹에 추가
sudo usermod -aG docker $USER

# 재로그인 필요
```

### 포트 접근 불가

1. EC2 Security Group 확인
2. EC2 인스턴스 상태 확인
3. Jenkins 컨테이너 실행 상태 확인

## 추가 리소스

- [Jenkins 공식 문서](https://www.jenkins.io/doc/)
- [Docker Hub - Jenkins](https://hub.docker.com/r/jenkins/jenkins)
- [AWS EC2 문서](https://docs.aws.amazon.com/ec2/)

## 문의 및 지원

문제가 발생하면 Jenkins 로그와 함께 이슈를 등록해주세요.
