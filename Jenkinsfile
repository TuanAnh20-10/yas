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
        SONAR_HOST_URL = 'https://fool-food-cornbread.ngrok-free.dev/'

        // Coverage gate threshold (line coverage), 0.70 = 70%
        COVERAGE_MIN = '0.70'
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

                    echo "Changed files:\n${changedFiles}"

                    def runAll = false
                    if (!changedFiles) { runAll = true }

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
                        gitleaks detect --source . --no-git -v --config gitleaks.toml --report-format json --report-path gitleaks-report.json || \
                        (echo "Gitleaks found leaked secrets" && exit 1)
                    else
                        echo "ERROR: gitleaks chua duoc cai tren Jenkins node"
                        exit 1
                    fi
                '''
            }
            post {
                always {
                    archiveArtifacts artifacts: 'gitleaks-report.json', allowEmptyArchive: true
                }
            }
        }

        stage('Unit Test') {
            parallel {
                stage('Test Cart') {
                    when { expression { env.RUN_CART == 'true' } }
                    steps {
                        ws("${env.WORKSPACE}@test-cart") {
                            checkout scm
                            sh 'chmod +x mvnw || true'
                            // jacoco:report generates target/site/jacoco/jacoco.xml + jacoco.exec
                            // requires jacoco-maven-plugin (prepare-agent + report) bound in pom.xml
                            sh "./mvnw -f ./pom.xml -pl ${CART_MODULE} -am test jacoco:report"
                            junit allowEmptyResults: true, testResults: "${CART_MODULE}/target/surefire-reports/*.xml"
                            archiveArtifacts artifacts: "${CART_MODULE}/target/site/jacoco/**, ${CART_MODULE}/target/jacoco.exec", allowEmptyArchive: true
                        }
                    }
                }

                stage('Test Product') {
                    when { expression { env.RUN_PRODUCT == 'true' } }
                    steps {
                        ws("${env.WORKSPACE}@test-product") {
                            checkout scm
                            sh 'chmod +x mvnw || true'
                            sh "./mvnw -f ./pom.xml -pl ${PRODUCT_MODULE} -am test jacoco:report"
                            junit allowEmptyResults: true, testResults: "${PRODUCT_MODULE}/target/surefire-reports/*.xml"
                            archiveArtifacts artifacts: "${PRODUCT_MODULE}/target/site/jacoco/**, ${PRODUCT_MODULE}/target/jacoco.exec", allowEmptyArchive: true
                        }
                    }
                }

                stage('Test Media') {
                    when { expression { env.RUN_MEDIA == 'true' } }
                    steps {
                        ws("${env.WORKSPACE}@test-media") {
                            checkout scm
                            sh 'chmod +x mvnw || true'
                            sh "./mvnw -f ./pom.xml -pl ${MEDIA_MODULE} -am test jacoco:report"
                            junit allowEmptyResults: true, testResults: "${MEDIA_MODULE}/target/surefire-reports/*.xml"
                            archiveArtifacts artifacts: "${MEDIA_MODULE}/target/site/jacoco/**, ${MEDIA_MODULE}/target/jacoco.exec", allowEmptyArchive: true
                        }
                    }
                }
            }
        }

        stage('Coverage Report & Gate (>= 70%)') {
            steps {
                script {
                    // Publish coverage in Jenkins UI + fail/mark UNSTABLE if below threshold.
                    // Requires the JaCoCo Jenkins plugin.
                    if (env.RUN_CART == 'true') {
                        ws("${env.WORKSPACE}@test-cart") {
                            jacoco(
                                execPattern: "${CART_MODULE}/target/jacoco.exec",
                                classPattern: "${CART_MODULE}/target/classes",
                                sourcePattern: "${CART_MODULE}/src/main/java",
                                minimumLineCoverage: "${env.COVERAGE_MIN}",
                                changeBuildStatus: true
                            )
                        }
                    }
                    if (env.RUN_PRODUCT == 'true') {
                        ws("${env.WORKSPACE}@test-product") {
                            jacoco(
                                execPattern: "${PRODUCT_MODULE}/target/jacoco.exec",
                                classPattern: "${PRODUCT_MODULE}/target/classes",
                                sourcePattern: "${PRODUCT_MODULE}/src/main/java",
                                minimumLineCoverage: "${env.COVERAGE_MIN}",
                                changeBuildStatus: true
                            )
                        }
                    }
                    if (env.RUN_MEDIA == 'true') {
                        ws("${env.WORKSPACE}@test-media") {
                            jacoco(
                                execPattern: "${MEDIA_MODULE}/target/jacoco.exec",
                                classPattern: "${MEDIA_MODULE}/target/classes",
                                sourcePattern: "${MEDIA_MODULE}/src/main/java",
                                minimumLineCoverage: "${env.COVERAGE_MIN}",
                                changeBuildStatus: true
                            )
                        }
                    }
                }
            }
            post {
                unstable {
                    script {
                        error("Coverage below ${env.COVERAGE_MIN} threshold - failing pipeline")
                    }
                }
            }
        }

        stage('SonarQube Scan') {
            steps {
                withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
                    script {
                        def runSonarForModule = { workspaceSuffix, moduleName ->
                            ws("${env.WORKSPACE}@${workspaceSuffix}") {
                                withEnv(["MODULE_NAME=${moduleName}"]) {
                                    sh 'chmod +x mvnw || true'
                                    sh '''
                                        ./mvnw -U -f ./pom.xml -pl common-library,$MODULE_NAME -am install -DskipTests
                                        ./mvnw -U -f ./$MODULE_NAME/pom.xml \
                                          org.sonarsource.scanner.maven:sonar-maven-plugin:4.0.0.4121:sonar \
                                          -Dsonar.projectKey=$SONAR_PROJECT_KEY \
                                          -Dsonar.host.url=$SONAR_HOST_URL \
                                          -Dsonar.login=$SONAR_TOKEN \
                                          -Dsonar.java.binaries=target/classes \
                                          -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
                                    '''
                                }
                            }
                        }

                        if (env.RUN_CART == 'true')    { runSonarForModule('test-cart', env.CART_MODULE) }
                        if (env.RUN_PRODUCT == 'true') { runSonarForModule('test-product', env.PRODUCT_MODULE) }
                        if (env.RUN_MEDIA == 'true')   { runSonarForModule('test-media', env.MEDIA_MODULE) }
                    }
                }
            }
        }

        stage('Dependency & Code Scan - Snyk') {
            steps {
                withCredentials([string(credentialsId: 'snyk-token', variable: 'SNYK_TOKEN')]) {
                    script {
                        def runSnykForModule = { workspaceSuffix, moduleName ->
                            ws("${env.WORKSPACE}@${workspaceSuffix}") {
                                sh """
                                    if ! command -v snyk >/dev/null 2>&1; then
                                        npm install -g snyk
                                    fi
                                    snyk auth \$SNYK_TOKEN
                                    snyk test --file=${moduleName}/pom.xml --severity-threshold=high --json-file-output=${moduleName}-snyk-report.json || \
                                    (echo "Snyk found high/critical vulnerabilities in ${moduleName}" && exit 1)
                                """
                                archiveArtifacts artifacts: "${moduleName}-snyk-report.json", allowEmptyArchive: true
                            }
                        }

                        if (env.RUN_CART == 'true')    { runSnykForModule('test-cart', env.CART_MODULE) }
                        if (env.RUN_PRODUCT == 'true') { runSnykForModule('test-product', env.PRODUCT_MODULE) }
                        if (env.RUN_MEDIA == 'true')   { runSnykForModule('test-media', env.MEDIA_MODULE) }
                    }
                }
            }
        }

        stage('Build') {
            parallel {
                stage('Build Cart') {
                    when { expression { env.RUN_CART == 'true' } }
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
                    when { expression { env.RUN_PRODUCT == 'true' } }
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
                    when { expression { env.RUN_MEDIA == 'true' } }
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
        unstable {
            echo 'Pipeline UNSTABLE - check coverage/scan reports'
        }
        failure {
            echo 'Pipeline FAILED'
        }
    }
}
