pipeline {
    agent any

    tools {
        gradle 'Gradle 8.5'
        jdk 'JDK 17'
    }

    environment {
        // Docker 이미지 정보
        DOCKER_IMAGE = 'peace-blog-backend'
        DOCKER_REGISTRY = 'your-registry'

        // 애플리케이션 설정
        APP_NAME = 'peace-blog-backend'

        // 환경별 프로파일
        PROFILE = "${env.BRANCH_NAME == 'main' ? 'prod' : 'dev'}"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
                script {
                    // Git 커밋 정보 가져오기
                    env.GIT_COMMIT_MSG = sh(script: 'git log -1 --pretty=%B', returnStdout: true).trim()
                    env.GIT_COMMIT_HASH = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
                }
            }
        }

        stage('Print Version') {
            steps {
                script {
                    echo "Building branch: ${env.BRANCH_NAME}"
                    echo "Git commit: ${env.GIT_COMMIT_HASH}"
                    echo "Commit message: ${env.GIT_COMMIT_MSG}"

                    // Nebula Release 플러그인으로 버전 확인
                    sh './gradlew printVersion'
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    echo "Building with profile: ${PROFILE}"
                    sh './gradlew clean build -x test'
                }
            }
        }

        stage('Test') {
            steps {
                script {
                    sh './gradlew test'
                }
            }
            post {
                always {
                    junit '**/build/test-results/test/*.xml'
                }
            }
        }

        stage('Code Quality') {
            steps {
                script {
                    // 코드 품질 검사 (필요시 활성화)
                    // sh './gradlew sonarqube'
                    echo 'Code quality check skipped'
                }
            }
        }

        stage('Build Docker Image') {
            when {
                anyOf {
                    branch 'main'
                    branch 'develop'
                }
            }
            steps {
                script {
                    def version = sh(script: './gradlew printVersion -q', returnStdout: true).trim()
                    env.APP_VERSION = version

                    echo "Building Docker image: ${DOCKER_IMAGE}:${APP_VERSION}"
                    sh """
                        docker build \
                            -t ${DOCKER_IMAGE}:${APP_VERSION} \
                            -t ${DOCKER_IMAGE}:latest \
                            .
                    """
                }
            }
        }

        stage('Push Docker Image') {
            when {
                anyOf {
                    branch 'main'
                    branch 'develop'
                }
            }
            steps {
                script {
                    // Docker 레지스트리에 푸시 (필요시 활성화)
                    // withCredentials([usernamePassword(credentialsId: 'docker-credentials', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    //     sh "echo ${DOCKER_PASS} | docker login -u ${DOCKER_USER} --password-stdin ${DOCKER_REGISTRY}"
                    //     sh "docker push ${DOCKER_REGISTRY}/${DOCKER_IMAGE}:${APP_VERSION}"
                    //     sh "docker push ${DOCKER_REGISTRY}/${DOCKER_IMAGE}:latest"
                    // }
                    echo 'Docker push skipped'
                }
            }
        }

        stage('Deploy to Dev') {
            when {
                branch 'develop'
            }
            steps {
                script {
                    echo 'Deploying to development environment'
                    // 개발 환경 배포 스크립트
                    // sh './deploy-dev.sh'
                }
            }
        }

        stage('Deploy to Production') {
            when {
                branch 'main'
            }
            steps {
                script {
                    echo 'Deploying to production environment'
                    // 프로덕션 배포 스크립트
                    // sh './deploy-prod.sh'
                }
            }
        }

        stage('Release') {
            when {
                allOf {
                    branch 'main'
                    expression { params.RELEASE_TYPE != null }
                }
            }
            steps {
                script {
                    echo "Creating release with scope: ${params.RELEASE_TYPE}"
                    withCredentials([usernamePassword(credentialsId: 'git-credentials', usernameVariable: 'GIT_USER', passwordVariable: 'GIT_PASS')]) {
                        sh """
                            git config user.email "jenkins@peace-blog.com"
                            git config user.name "Jenkins CI"
                            ./gradlew final -Prelease.scope=${params.RELEASE_TYPE}
                        """
                    }
                }
            }
        }
    }

    post {
        success {
            echo 'Pipeline succeeded!'
            // 성공 시 알림 (Slack, Email 등)
        }
        failure {
            echo 'Pipeline failed!'
            // 실패 시 알림
        }
        always {
            // 빌드 아티팩트 보관
            archiveArtifacts artifacts: '**/build/libs/*.jar', allowEmptyArchive: true

            // 워크스페이스 정리
            cleanWs()
        }
    }
}

// 파라미터 정의 (릴리스 타입 선택용)
properties([
    parameters([
        choice(
            name: 'RELEASE_TYPE',
            choices: ['', 'patch', 'minor', 'major'],
            description: 'Release type for version bump (leave empty for regular build)'
        )
    ])
])
