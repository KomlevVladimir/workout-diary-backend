import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

final String gitRepo = 'github.com/KomlevVladimir/workout-diary-backend.git'
final String githubCredentialsId = '1c810b6d-6a5e-431a-952b-1d0dfd740fa5'
final String registryName = 'docker.io'
final String imageName = 'komlevvladimir/workout-diary-backend'
String version

def getDateTime = {
    DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(ZonedDateTime.now(ZoneOffset.UTC))
}

pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                script {
                    dir("workout-diary-backend") {
                        git url: "https://$gitRepo", branch: 'master', credentialsId: githubCredentialsId

                        def commitHash = sh(returnStdout: true, script: 'git rev-parse --short HEAD').trim()
                        version = "${getDateTime()}-$commitHash"

                        sh 'chmod +x gradlew && ./gradlew clean build --no-daemon'

                        withCredentials([usernamePassword(credentialsId: githubCredentialsId,
                                passwordVariable: 'GITHUB_PASSWORD', usernameVariable: 'GITHUB_USERNAME')]) {
                            sh("docker login $registryName -u '$GITHUB_USERNAME' -p '$GITHUB_PASSWORD'")
                        }

                        sh "docker rmi -f \$(docker images '*/$imageName:latest' -q) || true"

                        docker.withRegistry("https://$registryName") {
                            def image = docker.build(imageName)
                            image.push(version)
                            image.push('latest')
                        }
                    }
                }
            }
        }

        stage('Integration tests') {
            agent {
                docker {
                    image 'komlevvladimir/workout-diary-backend-integration-tests'
                    reuseNode true
                    args '-u 0:0 --network host'
                    alwaysPull true
                    registryUrl 'https://docker.io/'
                }
            }
            steps {
                script {
                    sh "mv /tests ."
                    dir("tests") {
                        try {
                            sh "./gradlew clean test -i --no-daemon"
                        } finally {
                            allure results: [[path: 'build/allure-results']]
                        }
                    }
                }
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }
}
