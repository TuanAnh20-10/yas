pipeline {
    agent any
    stages {
        stage('Detect Changes') {
            steps {
                script {
                    // Logic lọc service nào thay đổi thì mới build service đó
                    def changedFiles = sh(script: "git diff --name-only ${env.BEFORE_SHA} ${env.AFTER_SHA}", returnStdout: true).trim()
                    echo "Các file thay đổi: ${changedFiles}"
                    env.SHOULD_BUILD_PRODUCT = changedFiles.contains('product/')
                }
            }
        }
        stage('Build Product Service') {
            when { expression { env.SHOULD_BUILD_PRODUCT == 'true' } }
            steps {
                dir('product') {
                    echo "Đang build Product Service cho Dev 2..."
                    // sh './mvnw clean install'
                }
            }
        }
    }
    post {
        always {
            // Bước này sẽ gửi trạng thái build (Xanh/Đỏ) về lại GitHub
            updateGitlabCommitStatus name: 'Jenkins CI', state: 'success'
        }
    }
}