pipeline {
    agent any

    environment {
        // ======================
        // Repos & Docker
        // ======================
        BACKEND_REPO_URL    = 'https://github.com/lindabenjeddou/backendvfdevops.git'
        FRONTEND_REPO_URL   = 'https://github.com/lindabenjeddou/pfesagemfinalefrontend.git'

        BACKEND_IMAGE_NAME  = 'linda296/backend'
        BACKEND_IMAGE_TAG   = '5.1.0'
        
        FRONTEND_IMAGE_NAME = 'linda296/frontend'
        FRONTEND_IMAGE_TAG  = '5.1.0'

        // ======================
        // SonarQube
        // ======================
        SONAR_PROJECT_KEY   = 'tn.esprit:backend'
        SONARQUBE_ENV_NAME  = 'backend'          // nom de l'instance Sonar configurée dans Jenkins

        // ======================
        // Credentials IDs Jenkins
        // ======================
        DOCKERHUB_CREDS_ID  = 'dockerhub'        // credentialsId Docker Hub
        SSH_DEPLOY_CREDS_ID = 'ssh-deploy'       // credentialsId SSH (clé privée) pour le serveur

        // ======================
        // Déploiement serveur (optionnel)
        // ======================
        DEPLOY_USER         = 'ubuntu'           // 🔁 à adapter
        DEPLOY_HOST         = 'YOUR.SERVER.IP'   // 🔁 à adapter  
        DEPLOY_PATH         = '/opt/pfe-backend' // 🔁 à adapter
    }

    options {
        skipDefaultCheckout(true)
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    stages {

        stage('Checkout Backend') {
            steps {
                echo '📥 Clonage du backend...'
                git branch: 'main', url: "${BACKEND_REPO_URL}"
            }
        }

        stage('Build & Tests (JUnit + Mockito + JaCoCo)') {
            steps {
                echo '🧪 Build + tests + JaCoCo...'
                sh 'mvn -B clean verify jacoco:report'
            }
        }

        stage('Publish Test Reports') {
            steps {
                echo '📊 Publication des rapports JUnit & JaCoCo...'

                // Rapports JUnit
                junit testResults: 'target/surefire-reports/*.xml', allowEmptyResults: false

                // Rapport JaCoCo
                script {
                    jacoco(
                        execPattern: 'target/jacoco.exec',
                        classPattern: 'target/classes',
                        sourcePattern: 'src/main/java',
                        inclusionPattern: '**/*.class',
                        exclusionPattern: '**/*Test*.class, **/generated/**'
                    )
                }

                // Archiver les rapports
                archiveArtifacts artifacts: 'target/site/jacoco/jacoco.xml, target/surefire-reports/*.xml',
                                  fingerprint: true
            }
        }

        stage('SonarQube Analysis') {
            steps {
                echo '🔎 Analyse SonarQube avec JaCoCo XML...'
                withSonarQubeEnv("${SONARQUBE_ENV_NAME}") {
                    sh """
                        mvn -B sonar:sonar \
                          -Dsonar.projectKey=${SONAR_PROJECT_KEY} \
                          -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml \
                          -DskipTests
                    """
                }
            }
        }

        stage('Quality Gate') {
            steps {
                echo '✅ Vérification du Quality Gate SonarQube...'
                timeout(time: 10, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Docker Build Backend') {
            steps {
                echo '🐳 Build de l\'image Docker backend...'
                sh """
                    docker build -t ${BACKEND_IMAGE_NAME}:${BACKEND_IMAGE_TAG} .
                    docker tag ${BACKEND_IMAGE_NAME}:${BACKEND_IMAGE_TAG} ${BACKEND_IMAGE_NAME}:latest
                """
            }
        }

        stage('Docker Push Backend') {
            steps {
                echo '📤 Push de l\'image backend sur Docker Hub...'
                withCredentials([usernamePassword(
                    credentialsId: "${DOCKERHUB_CREDS_ID}", 
                    usernameVariable: 'DOCKER_USER', 
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    sh """
                        echo "${DOCKER_PASS}" | docker login -u "${DOCKER_USER}" --password-stdin
                        docker push ${BACKEND_IMAGE_NAME}:${BACKEND_IMAGE_TAG}
                        docker push ${BACKEND_IMAGE_NAME}:latest
                        docker logout || true
                    """
                }
            }
        }

        stage('Deploy with Docker Compose') {
            when {
                expression { 
                    return env.DEPLOY_HOST != 'YOUR.SERVER.IP' && 
                           currentBuild.resultIsBetterOrEqualTo('SUCCESS')
                }
            }
            steps {
                echo '🚀 Déploiement sur le serveur via docker-compose...'

                sshagent(credentials: ["${SSH_DEPLOY_CREDS_ID}"]) {
                    sh """
                        ssh -o StrictHostKeyChecking=no ${DEPLOY_USER}@${DEPLOY_HOST} << 'EOF'
                          set -e

                          echo "[Deploy] Création dossier si absent..."
                          mkdir -p ${DEPLOY_PATH}
                          cd ${DEPLOY_PATH}

                          echo "[Deploy] Pull de la dernière image..."
                          docker compose pull backend || docker-compose pull backend

                          echo "[Deploy] Redémarrage du backend..."
                          docker compose up -d --no-deps backend || docker-compose up -d --no-deps backend

                          echo "[Deploy] Vérification du statut..."
                          docker compose ps || docker-compose ps

                          echo "[Deploy] Terminé avec succès ✅"
                        EOF
                    """
                }
            }
        }
    }

    post {
        success {
            echo '✔ SUCCESS — Tests OK, Quality Gate OK, image Docker poussée.'
            script {
                if (env.DEPLOY_HOST != 'YOUR.SERVER.IP') {
                    echo '✔ Déploiement effectué avec succès !'
                }
            }
        }
        failure {
            echo '✘ ECHEC — Vérifier tests, SonarQube, Docker ou déploiement.'
            echo '📋 Consultez les logs ci-dessus pour plus de détails.'
        }
        always {
            echo '🏁 Pipeline terminé.'
            // Nettoyer les images Docker locales (optionnel)
            sh 'docker system prune -f || true'
        }
    }
}
