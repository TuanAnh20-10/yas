pipeline {
    agent any

    environment {
        CART_DIR = 'cart'
        PRODUCT_DIR = 'product'
        MEDIA_DIR = 'media'

        CART_MODULE = 'cart'
        PRODUCT_MODULE = 'product'
        MEDIA_MODULE = 'media'

        SONAR_PROJECT_KEY = 'yas_project'
        SONAR_HOST_URL = 'https://everglade-starfish-fable.ngrok-free.dev/'
    }

    options {
        disableConcurrentBuilds()
        timestamps()
    }

    stages {
        stage('Checkout') {
            steps {
                deleteDir()
                checkout scm
                sh 'git fetch --all || true'
                sh 'chmod +x mvnw || true'
            }
        }

        stage('Verify Build Environment') {
            steps {
                sh '''
                    java -version
                    ./mvnw -version
                '''
            }
        }

        stage('Detect Changed Services') {
            steps {
                script {
                    def changedFiles = sh(
                        script: '''
                            if git rev-parse --verify origin/main >/dev/null 2>&1; then
                                git diff --name-only origin/main...HEAD || true
                            else
                                git diff --name-only HEAD~1 HEAD || true
                            fi
                        ''',
                        returnStdout: true
                    ).trim()

                    echo "Changed files:\\n${changedFiles}"

                    def runAll = false

                    if (!changedFiles) {
                        runAll = true
                    }

                    if (changedFiles.contains('Jenkinsfile') ||
                        changedFiles.contains('jenkinsfile') ||
                        changedFiles.contains('pom.xml') ||
                        changedFiles.contains('mvnw') ||
                        changedFiles.contains('.mvn/')) {
                        runAll = true
                    }

                    env.RUN_CART = (runAll || changedFiles.contains("${env.CART_DIR}/")) ? 'true' : 'false'
                    env.RUN_PRODUCT = (runAll || changedFiles.contains("${env.PRODUCT_DIR}/")) ? 'true' : 'false'
                    env.RUN_MEDIA = (runAll || changedFiles.contains("${env.MEDIA_DIR}/")) ? 'true' : 'false'

                    echo "RUN_CART=${env.RUN_CART}"
                    echo "RUN_PRODUCT=${env.RUN_PRODUCT}"
                    echo "RUN_MEDIA=${env.RUN_MEDIA}"
                }
            }
        }

        stage('Secret Scan - Gitleaks') {
            steps {
                sh '''
                    if command -v gitleaks >/dev/null 2>&1; then
                        gitleaks protect --source . -v
                    else
                        echo "ERROR: gitleaks chua duoc cai tren Jenkins node"
                        exit 1
                    fi
                '''
            }
        }

        stage('Unit Test') {
            parallel {
                stage('Test Cart') {
                    when {
                        expression { env.RUN_CART == 'true' }
                    }
                    steps {
                        ws("${env.WORKSPACE}@test-cart") {
                            checkout scm
                            sh 'chmod +x mvnw || true'
                            sh "./mvnw -f ./pom.xml -pl ${CART_MODULE} -am test"
                            junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                            archiveArtifacts artifacts: '**/target/site/**, **/target/jacoco*.exec', allowEmptyArchive: true
                        }
                    }
                }

                stage('Test Product') {
                    when {
                        expression { env.RUN_PRODUCT == 'true' }
                    }
                    steps {
                        ws("${env.WORKSPACE}@test-product") {
                            checkout scm
                            sh 'chmod +x mvnw || true'
                            sh "./mvnw -f ./pom.xml -pl ${PRODUCT_MODULE} -am test"
                            junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                            archiveArtifacts artifacts: '**/target/site/**, **/target/jacoco*.exec', allowEmptyArchive: true
                        }
                    }
                }

                stage('Test Media') {
                    when {
                        expression { env.RUN_MEDIA == 'true' }
                    }
                    steps {
                        ws("${env.WORKSPACE}@test-media") {
                            checkout scm
                            sh 'chmod +x mvnw || true'
                            sh "./mvnw -f ./pom.xml -pl ${MEDIA_MODULE} -am test"
                            junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                            archiveArtifacts artifacts: '**/target/site/**, **/target/jacoco*.exec', allowEmptyArchive: true
                        }
                    }
                }
            }
        }

        stage('Coverage Check') {
            steps {
                sh '''
                    echo "Checking JaCoCo reports..."

                    if [ "${RUN_CART}" = "true" ]; then
                      find "${WORKSPACE}@test-cart" -type f | grep -E 'jacoco.*xml|jacoco.*csv|jacoco.*html' || true
                    fi

                    if [ "${RUN_PRODUCT}" = "true" ]; then
                      find "${WORKSPACE}@test-product" -type f | grep -E 'jacoco.*xml|jacoco.*csv|jacoco.*html' || true
                    fi

                    if [ "${RUN_MEDIA}" = "true" ]; then
                      find "${WORKSPACE}@test-media" -type f | grep -E 'jacoco.*xml|jacoco.*csv|jacoco.*html' || true
                    fi
                '''
            }
        }

        stage('SonarQube Scan') {
            steps {
                withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
                    script {
                        if (env.RUN_CART == 'true') {
                            sh """
                                ./mvnw -f ./pom.xml -pl ${CART_MODULE} -am sonar:sonar \
                                  -Dsonar.projectKey=yas_cart \
                                  -Dsonar.host.url=${SONAR_HOST_URL} \
                                  -Dsonar.login=${SONAR_TOKEN}
                            """
                        }

                        if (env.RUN_PRODUCT == 'true') {
                            sh """
                                ./mvnw -f ./pom.xml -pl ${PRODUCT_MODULE} -am sonar:sonar \
                                  -Dsonar.projectKey=yas_product \
                                  -Dsonar.host.url=${SONAR_HOST_URL} \
                                  -Dsonar.login=${SONAR_TOKEN}
                            """
                        }

                        if (env.RUN_MEDIA == 'true') {
                            sh """
                                ./mvnw -f ./pom.xml -pl ${MEDIA_MODULE} -am sonar:sonar \
                                  -Dsonar.projectKey=yas_media \
                                  -Dsonar.host.url=${SONAR_HOST_URL} \
                                  -Dsonar.login=${SONAR_TOKEN}
                            """
                        }
                    }
                }
            }
        }

        stage('Build') {
            parallel {
                stage('Build Cart') {
                    when {
                        expression { env.RUN_CART == 'true' }
                    }
                    steps {
                        ws("${env.WORKSPACE}@build-cart") {
                            checkout scm
                            sh 'chmod +x mvnw || true'
                            sh "./mvnw -f ./pom.xml -pl ${CART_MODULE} -am package -DskipTests"
                            archiveArtifacts artifacts: '**/target/*.jar', allowEmptyArchive: true
                        }
                    }
                }

                stage('Build Product') {
                    when {
                        expression { env.RUN_PRODUCT == 'true' }
                    }
                    steps {
                        ws("${env.WORKSPACE}@build-product") {
                            checkout scm
                            sh 'chmod +x mvnw || true'
                            sh "./mvnw -f ./pom.xml -pl ${PRODUCT_MODULE} -am package -DskipTests"
                            archiveArtifacts artifacts: '**/target/*.jar', allowEmptyArchive: true
                        }
                    }
                }

                stage('Build Media') {
                    when {
                        expression { env.RUN_MEDIA == 'true' }
                    }
                    steps {
                        ws("${env.WORKSPACE}@build-media") {
                            checkout scm
                            sh 'chmod +x mvnw || true'
                            sh "./mvnw -f ./pom.xml -pl ${MEDIA_MODULE} -am package -DskipTests"
                            archiveArtifacts artifacts: '**/target/*.jar', allowEmptyArchive: true
                        }
                    }
                }
            }
        }
    }

    post {
        success {
            echo 'Pipeline SUCCESS'
        }
        failure {
            echo 'Pipeline FAILED'
        }
    }
}
