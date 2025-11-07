# 🔧 Guide Pipeline Jenkins CI/CD

## 📋 Vue d'Ensemble

Pipeline Jenkins complet pour :
- ✅ Build & Tests (JUnit + Mockito)
- ✅ Couverture de code (JaCoCo)
- ✅ Analyse qualité (SonarQube)
- ✅ Quality Gate
- ✅ Docker Build & Push
- ✅ Déploiement automatique (optionnel)

## 🏗️ Architecture du Pipeline

```
┌────────────────────────────────────────────────────┐
│             JENKINS CI/CD PIPELINE                 │
├────────────────────────────────────────────────────┤
│                                                    │
│  1️⃣  Checkout Backend (GitHub)                    │
│      └─ Clone main branch                         │
│                                                    │
│  2️⃣  Build & Tests                                │
│      ├─ Maven clean verify                        │
│      ├─ JUnit tests (33 tests)                    │
│      └─ JaCoCo coverage report                    │
│                                                    │
│  3️⃣  Publish Test Reports                         │
│      ├─ JUnit XML                                 │
│      ├─ JaCoCo coverage                           │
│      └─ Archive artifacts                         │
│                                                    │
│  4️⃣  SonarQube Analysis                           │
│      ├─ Code quality scan                         │
│      ├─ Coverage integration                      │
│      └─ Send to SonarQube server                  │
│                                                    │
│  5️⃣  Quality Gate                                 │
│      └─ Wait for SonarQube validation             │
│         (Abort if FAILED)                         │
│                                                    │
│  6️⃣  Docker Build Backend                         │
│      ├─ Multi-stage build                         │
│      ├─ Tag: linda296/backend:5.1.0               │
│      └─ Tag: linda296/backend:latest              │
│                                                    │
│  7️⃣  Docker Push Backend                          │
│      └─ Push to Docker Hub                        │
│                                                    │
│  8️⃣  Deploy (optionnel)                           │
│      └─ SSH + docker-compose up                   │
│                                                    │
└────────────────────────────────────────────────────┘
```

## 🔧 Prérequis Jenkins

### Plugins Requis

Installez ces plugins dans Jenkins :
- **Git Plugin** - Clone des repos
- **Pipeline Plugin** - Support Jenkinsfile
- **Docker Pipeline Plugin** - Commandes Docker
- **JUnit Plugin** - Rapports de tests
- **JaCoCo Plugin** - Couverture de code
- **SonarQube Scanner Plugin** - Analyse SonarQube
- **Credentials Plugin** - Gestion des credentials
- **SSH Agent Plugin** - Déploiement SSH

### Installation des Plugins

```
Jenkins Dashboard → Manage Jenkins → Plugins → Available Plugins
Rechercher et installer chaque plugin, puis redémarrer Jenkins
```

## 🔐 Configuration des Credentials

### 1. Docker Hub Credentials

```
Jenkins → Manage Jenkins → Credentials → System → Global credentials
Cliquer "Add Credentials"

Kind: Username with password
Scope: Global
Username: linda296
Password: [votre token Docker Hub]
ID: dockerhub
Description: Docker Hub credentials
```

### 2. SSH Deploy Credentials (Optionnel)

Pour le déploiement automatique :

```
Jenkins → Manage Jenkins → Credentials → System → Global credentials
Cliquer "Add Credentials"

Kind: SSH Username with private key
Scope: Global
ID: ssh-deploy
Username: ubuntu
Private Key: [coller votre clé privée SSH]
Description: SSH key for deployment
```

## 🔗 Configuration SonarQube

### 1. Configurer le serveur SonarQube

```
Jenkins → Manage Jenkins → System → SonarQube servers
Cliquer "Add SonarQube"

Name: backend
Server URL: http://172.18.139.194:9000
Server authentication token: [générer dans SonarQube]
```

### 2. Générer le Token SonarQube

Dans SonarQube :
```
My Account → Security → Generate Tokens
Name: jenkins
Type: User Token
→ Copier le token généré
```

Ajouter dans Jenkins :
```
Jenkins → Manage Jenkins → Credentials → System → Global credentials
Kind: Secret text
Secret: [token SonarQube]
ID: sonarqube-token
Description: SonarQube authentication token
```

## 🚀 Création du Pipeline

### 1. Créer un nouveau Pipeline

```
Jenkins Dashboard → New Item
Nom: piplinepfe (ou votre nom)
Type: Pipeline
→ OK
```

### 2. Configuration du Pipeline

```groovy
Pipeline section:
  Definition: Pipeline script from SCM
  SCM: Git
  Repository URL: https://github.com/lindabenjeddou/backendvfdevops.git
  Credentials: (none pour repo public)
  Branch Specifier: */main
  Script Path: Jenkinsfile
```

### 3. Configuration des Triggers (Optionnel)

```
Build Triggers:
  ☑ GitHub hook trigger for GITScm polling
  ☑ Poll SCM: H/5 * * * * (toutes les 5 min)
```

## 📝 Variables d'Environnement

Modifiez dans le `Jenkinsfile` :

```groovy
environment {
    // Images Docker
    BACKEND_IMAGE_NAME  = 'linda296/backend'
    BACKEND_IMAGE_TAG   = '5.1.0'              // ← Incrémenter à chaque version
    
    // SonarQube
    SONAR_PROJECT_KEY   = 'tn.esprit:backend'
    SONARQUBE_ENV_NAME  = 'backend'            // ← Nom dans Jenkins config
    
    // Credentials
    DOCKERHUB_CREDS_ID  = 'dockerhub'          // ← ID dans Jenkins
    
    // Déploiement (optionnel)
    DEPLOY_USER         = 'ubuntu'
    DEPLOY_HOST         = '192.168.1.100'      // ← Votre serveur
    DEPLOY_PATH         = '/opt/pfe-backend'
}
```

## 🐛 Résolution du Problème "Permission denied" (mvnw)

### Problème Original

```bash
/bin/sh: ./mvnw: Permission denied
```

### Solution Appliquée

Le `Dockerfile` a été corrigé pour donner les permissions :

```dockerfile
# Copier tous les fichiers sources
COPY . .

# ✅ Donner les permissions d'exécution
RUN chmod +x mvnw

# Build avec Maven Wrapper
RUN ./mvnw -B -DskipTests clean package
```

### Alternative (Git)

Si le problème persiste, configurez Git pour préserver les permissions :

```bash
# Dans votre repo local
git update-index --chmod=+x mvnw
git commit -m "fix: Add execute permission to mvnw"
git push origin main
```

## 🔄 Exécution du Pipeline

### 1. Build Manuel

```
Jenkins → piplinepfe → Build Now
```

### 2. Build Automatique (GitHub Webhook)

Dans GitHub :
```
Repository → Settings → Webhooks → Add webhook
Payload URL: http://JENKINS_URL/github-webhook/
Content type: application/json
Events: Just the push event
Active: ☑
```

### 3. Surveillance du Build

```
Jenkins → piplinepfe → #[build number] → Console Output
```

## 📊 Résultats du Pipeline

### Tests JUnit

```
✅ 33 tests passés
   - ComponentServiceTest: 10 tests
   - ProjectServiceTest: 13 tests  
   - ComponentRpTest: 9 tests
   - PIApplicationTests: 1 test
```

### Couverture JaCoCo

```
📊 Coverage actuelle:
   - Classes: 49.3%
   - Méthodes: 16.0%
   - Lignes: 8.9%
   - Branches: 3.2%
```

Accès : `Jenkins → Build → Coverage Report`

### SonarQube

```
✅ Quality Gate: PASSED
   - Bugs: 0
   - Code Smells: X
   - Security Hotspots: 0
   - Duplications: X%
```

Accès : http://172.18.139.194:9000/dashboard?id=tn.esprit%3Abackend

### Docker Hub

```
✅ Images publiées:
   - linda296/backend:5.1.0
   - linda296/backend:latest
```

Accès : https://hub.docker.com/r/linda296/backend

## 🚨 Dépannage

### Erreur: "No such DSL method 'jacoco'"

**Cause** : Plugin JaCoCo non installé

**Solution** :
```
Manage Jenkins → Plugins → Available → "JaCoCo"
→ Install without restart
```

### Erreur: "No previous SonarQube analysis found"

**Cause** : Manque `withSonarQubeEnv`

**Solution** : Déjà corrigé dans le Jenkinsfile actuel :
```groovy
withSonarQubeEnv('backend') {
    sh "mvn sonar:sonar ..."
}
```

### Erreur: "docker: command not found"

**Cause** : Docker non installé sur l'agent Jenkins

**Solution** :
```bash
# Sur le serveur Jenkins
sudo apt update
sudo apt install docker.io
sudo usermod -aG docker jenkins
sudo systemctl restart jenkins
```

### Erreur: "Permission denied" (Docker socket)

**Cause** : Jenkins n'a pas accès à Docker

**Solution** :
```bash
sudo chmod 666 /var/run/docker.sock
# Ou mieux :
sudo usermod -aG docker jenkins
```

### Quality Gate en ERROR

**Cause** : Code ne respecte pas les standards SonarQube

**Solution** :
1. Consulter SonarQube Dashboard
2. Corriger les issues critiques
3. Re-pousser le code

### Tests échouent dans Jenkins mais passent localement

**Cause** : Différences d'environnement

**Solution** :
```groovy
// Ajouter dans Jenkinsfile
environment {
    JAVA_HOME = '/usr/lib/jvm/java-17-openjdk-amd64'
}
```

## 📈 Optimisations

### 1. Cache Maven

Ajoutez un volume Maven pour accélérer les builds :

```groovy
pipeline {
    agent {
        docker {
            image 'maven:3.9-eclipse-temurin-17'
            args '-v $HOME/.m2:/root/.m2'
        }
    }
}
```

### 2. Build Parallèles

```groovy
stage('Tests Parallèles') {
    parallel {
        stage('Unit Tests') {
            steps {
                sh 'mvn test'
            }
        }
        stage('Integration Tests') {
            steps {
                sh 'mvn verify -P integration-tests'
            }
        }
    }
}
```

### 3. Notifications

```groovy
post {
    success {
        emailext (
            subject: "✅ Build SUCCESS: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
            body: "Le pipeline a réussi !",
            to: "your-email@example.com"
        )
    }
}
```

## 🔐 Sécurité

### Bonnes Pratiques

1. **Ne jamais committer de secrets dans Git**
   - Utilisez Jenkins Credentials
   - Utilisez des variables d'environnement

2. **Tokens Docker Hub**
   - Utilisez un token d'accès, pas le mot de passe
   - https://hub.docker.com/settings/security

3. **SonarQube Token**
   - Token avec permissions limitées
   - Renouveler régulièrement

4. **SSH Keys**
   - Clé SSH dédiée au déploiement
   - Permissions limitées sur le serveur cible

## 📚 Commandes Utiles

### Vérifier la configuration Jenkins

```bash
# Jenkins CLI
java -jar jenkins-cli.jar -s http://localhost:8080/ list-jobs

# Voir les credentials
curl -u admin:password http://localhost:8080/credentials/
```

### Tester le Dockerfile localement

```bash
# Build
docker build -t backend-test .

# Vérifier l'image
docker images | grep backend-test

# Run test
docker run -p 8089:8089 backend-test
```

### Tester Maven localement

```bash
# Même commande que Jenkins
mvn -B clean verify jacoco:report

# Vérifier le JAR
ls -lh target/*.jar
```

## 🎯 Checklist Avant Build

- [ ] Tous les tests passent localement
- [ ] `mvnw` a les permissions d'exécution
- [ ] Credentials configurés dans Jenkins
- [ ] SonarQube serveur accessible
- [ ] Docker Hub credentials valides
- [ ] Jenkinsfile à jour dans le repo
- [ ] Variables d'environnement correctes

## 📞 Support

### Logs Jenkins

```bash
# Logs Jenkins
tail -f /var/log/jenkins/jenkins.log

# Logs d'un build spécifique
Jenkins → Build → Console Output
```

### Tester SonarQube

```bash
curl -u admin:admin http://172.18.139.194:9000/api/system/status
```

### Tester Docker Hub

```bash
docker login
docker pull linda296/backend:5.1.0
```

---

**Dernière mise à jour** : 7 novembre 2024  
**Version Pipeline** : 5.1.0  
**Status** : ✅ Production Ready
