pipeline {
    agent any

    tools {
        gradle 'Gradle 8.5'
        jdk 'JDK 17'
    }

    environment {
        DOCKER_IMAGE = 'peace-blog-backend'
        APP_NAME = 'peace-blog-backend'
        PROFILE = "${env.BRANCH_NAME == 'main' ? 'prod' : 'dev'}"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
                script {
                    env.GIT_COMMIT_MSG = sh(script: 'git log -1 --pretty=%B', returnStdout: true).trim()
                    env.GIT_COMMIT_HASH = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
                    // Branch name 가져오기
                    env.BRANCH_NAME = sh(script: 'git rev-parse --abbrev-ref HEAD', returnStdout: true).trim()
                }
            }
        }

        stage('Print Version') {
            steps {
                script {
                    echo "Building branch: ${env.BRANCH_NAME}"
                    echo "Git commit: ${env.GIT_COMMIT_HASH}"
                    echo "Commit message: ${env.GIT_COMMIT_MSG}"

                    // Gradle version 확인
                    sh './gradlew --version'
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

        stage('Build Docker Image') {
            when {
                anyOf {
                    branch 'main'
                    branch 'develop'
                }
            }
            steps {
                script {
                    // Git commit hash를 버전으로 사용
                    env.APP_VERSION = env.GIT_COMMIT_HASH

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

        stage('Deploy to Dev') {
            when {
                branch 'develop'
            }
            steps {
                script {
                    echo 'Deploying to development environment'
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
                }
            }
        }
    }

    post {
        success {
            echo 'Pipeline succeeded!'
        }
        failure {
            echo 'Pipeline failed!'
        }
        always {
            archiveArtifacts artifacts: '**/build/libs/*.jar', allowEmptyArchive: true
            cleanWs()
        }
    }
}
