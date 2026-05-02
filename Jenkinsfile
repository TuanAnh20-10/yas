pipeline {
    agent any

    environment {
        CART_DIR = 'cart'
        PRODUCT_DIR = 'product'
        MEDIA_DIR = 'media'

        CART_MODULE = 'cart'
        PRODUCT_MODULE = 'product'
        MEDIA_MODULE = 'media'

        // ====== SONAR ======
        SONAR_PROJECT_KEY = 'yas_project'
        SONAR_HOST_URL = 'https://everglade-starfish-fable.ngrok-free.dev'
    }

    options {
        disableConcurrentBuilds()
        timestamps()
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
                sh 'git fetch --all || true'
                sh 'chmod +x mvnw || true'
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
                        echo "ERROR: gitleaks chưa được cài trên Jenkins node"
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
                        sh "./mvnw -f ./pom.xml -pl ${CART_MODULE} -am clean test"
                    }
                    post {
                        always {
                            junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                        }
                    }
                }

                stage('Test Product') {
                    when {
                        expression { env.RUN_PRODUCT == 'true' }
                    }
                    steps {
                        sh "./mvnw -f ./pom.xml -pl ${PRODUCT_MODULE} -am clean test"
                    }
                    post {
                        always {
                            junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                        }
                    }
                }

                stage('Test Media') {
                    when {
                        expression { env.RUN_MEDIA == 'true' }
                    }
                    steps {
                        sh "./mvnw -f ./pom.xml -pl ${MEDIA_MODULE} -am clean test"
                    }
                    post {
                        always {
                            junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                        }
                    }
                }
            }
        }

        stage('Coverage Check') {
            steps {
                sh '''
                    echo "Checking JaCoCo reports..."
                    find . -type f | grep -E 'jacoco.*xml|jacoco.*csv|jacoco.*html' || true
                '''
            }
        }

        stage('SonarQube Scan') {
            steps {
                withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
                    sh '''
                        ./mvnw -f ./pom.xml clean verify sonar:sonar \
                          -Dsonar.projectKey=${SONAR_PROJECT_KEY} \
                          -Dsonar.host.url=${SONAR_HOST_URL} \
                          -Dsonar.login=${SONAR_TOKEN}
                    '''
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
                        sh "./mvnw -f ./pom.xml -pl ${CART_MODULE} -am clean package -DskipTests"
                    }
                }

                stage('Build Product') {
                    when {
                        expression { env.RUN_PRODUCT == 'true' }
                    }
                    steps {
                        sh "./mvnw -f ./pom.xml -pl ${PRODUCT_MODULE} -am clean package -DskipTests"
                    }
                }

                stage('Build Media') {
                    when {
                        expression { env.RUN_MEDIA == 'true' }
                    }
                    steps {
                        sh "./mvnw -f ./pom.xml -pl ${MEDIA_MODULE} -am clean package -DskipTests"
                    }
                }
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: '**/target/*.jar, **/target/surefire-reports/*.xml, **/target/site/**', allowEmptyArchive: true
        }
        success {
            echo 'Pipeline SUCCESS'
        }
        failure {
            echo 'Pipeline FAILED'
        }
    }
}