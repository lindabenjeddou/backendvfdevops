# 🎯 Projet Full-Stack - Backend Spring Boot + Frontend React

## 📌 Vue d'Ensemble

Application full-stack complète avec :
- **Backend** : Spring Boot 3 + Java 17 + MySQL
- **Frontend** : React 18 + Tailwind CSS (Notus Template)
- **DevOps** : Docker + Docker Compose + Jenkins CI/CD
- **Tests** : JUnit 5 + Mockito + JaCoCo + SonarQube

## 🗂️ Repositories

| Composant | Repository GitHub |
|-----------|------------------|
| **Backend** | https://github.com/lindabenjeddou/backendvfdevops.git |
| **Frontend** | https://github.com/lindabenjeddou/pfesagemfinalefrontend.git |

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────┐
│              FULL-STACK APPLICATION             │
├─────────────────────────────────────────────────┤
│                                                 │
│  Frontend (React + Nginx)                      │
│  └─ http://localhost                           │
│     └─ Proxy /api → Backend                    │
│                                                 │
│  Backend (Spring Boot)                         │
│  └─ http://localhost:8089                      │
│     └─ REST API                                │
│        └─ MySQL Database                       │
│           └─ localhost:3306                    │
│                                                 │
│  PhpMyAdmin                                    │
│  └─ http://localhost:8081                      │
│                                                 │
└─────────────────────────────────────────────────┘
```

## 🚀 Démarrage en 30 secondes

```powershell
# 1. Cloner le backend
git clone https://github.com/lindabenjeddou/backendvfdevops.git
cd backendvfdevops

# 2. Copier la configuration
Copy-Item .env.example .env

# 3. Lancer tout
.\start-docker.ps1
```

**C'est tout !** Accédez à http://localhost 🎉

## 📦 Technologies

### Backend
- **Framework** : Spring Boot 3.2.x
- **Langage** : Java 17
- **Base de données** : MySQL 8.0
- **Build** : Maven 3.9+
- **Tests** : JUnit 5 + Mockito
- **Couverture** : JaCoCo
- **Qualité** : SonarQube

### Frontend
- **Framework** : React 18.3.1
- **UI** : Tailwind CSS 2.0
- **Template** : Notus React
- **Router** : React Router 5.3
- **HTTP** : Axios
- **Build** : React Scripts 5.0
- **Serveur** : Nginx 1.25

### DevOps
- **Conteneurisation** : Docker + Docker Compose
- **CI/CD** : Jenkins Pipeline
- **Registry** : Docker Hub
- **Monitoring** : Spring Boot Actuator

## 📁 Structure des Fichiers

```
back-master/                                    # Backend Repository
├── src/
│   ├── main/
│   │   ├── java/tn/esprit/PI/                 # Code Java
│   │   │   ├── Controllers/                   # REST Controllers
│   │   │   ├── Services/                      # Business Logic
│   │   │   ├── entity/                        # JPA Entities
│   │   │   └── repository/                    # JPA Repositories
│   │   └── resources/
│   │       ├── application.properties         # Config principale
│   │       └── application-docker.properties  # Config Docker
│   └── test/                                  # Tests JUnit
│       ├── java/tn/esprit/PI/
│       │   ├── Services/                      # Tests unitaires
│       │   └── repository/                    # Tests d'intégration
│       └── resources/
│           └── application-test.properties    # Config tests
├── target/                                     # Artifacts Maven
├── docker-compose.yml                          # ⭐ Orchestration complète
├── Dockerfile                                  # Multi-stage backend
├── .env.example                                # Variables d'environnement
├── start-docker.ps1                            # Script de démarrage
├── pom.xml                                     # Dépendances Maven
├── Jenkinsfile                                 # Pipeline CI/CD
└── Documentation/
    ├── FULL_STACK_DEPLOYMENT.md               # Guide complet
    ├── DOCKER_README.md                       # Résumé Docker
    ├── DOCKER_BUILD.md                        # Multi-Stage Build
    ├── DOCKER_GUIDE.md                        # Guide détaillé
    └── GUIDE_TESTS.md                         # Guide tests

notus-react-main/                              # Frontend Repository
├── src/
│   ├── components/                            # Composants React
│   ├── layouts/                               # Layouts
│   ├── views/                                 # Pages
│   └── assets/                                # CSS, images
├── public/                                     # Assets statiques
├── build/                                      # Build production
├── Dockerfile                                  # Multi-stage frontend
├── nginx.conf                                  # Config Nginx + Proxy
├── package.json                                # Dépendances NPM
└── .env.example                                # Variables d'environnement
```

## 🌐 Services et Ports

| Service | Port Local | Port Container | Description |
|---------|-----------|----------------|-------------|
| Frontend | 80 | 80 | React + Nginx |
| Backend | 8089 | 8089 | Spring Boot API |
| MySQL | 3306 | 3306 | Base de données |
| PhpMyAdmin | 8081 | 80 | Admin BD |

## 🔧 Configuration

### Fichier .env

```env
# MySQL
MYSQL_ROOT_PASSWORD=root
MYSQL_DATABASE=pidevdb
MYSQL_USER=pidev
MYSQL_PASSWORD=pidev123

# Backend
BACKEND_PORT=8089
SERVER_PORT=8089
JWT_SECRET=votre_secret_jwt

# Frontend
FRONTEND_PORT=80
REACT_APP_API_URL=http://localhost:8089
```

## 📖 Documentation

| Document | Description |
|----------|-------------|
| **[FULL_STACK_DEPLOYMENT.md](./FULL_STACK_DEPLOYMENT.md)** | 📘 Guide complet de déploiement |
| **[DOCKER_README.md](./DOCKER_README.md)** | 🐳 Résumé Docker |
| **[DOCKER_BUILD.md](./DOCKER_BUILD.md)** | 🏗️ Multi-Stage Build |
| **[DOCKER_GUIDE.md](./DOCKER_GUIDE.md)** | 📚 Guide détaillé Docker |
| **[GUIDE_TESTS.md](./GUIDE_TESTS.md)** | 🧪 Tests JUnit + Mockito |

## 🛠️ Commandes Essentielles

### Démarrage

```powershell
# Démarrer tous les services
docker-compose up -d

# Démarrer avec rebuild
docker-compose up -d --build

# Script automatique
.\start-docker.ps1
```

### Arrêt

```powershell
# Arrêter les services
docker-compose down

# Arrêter et supprimer les volumes
docker-compose down -v
```

### Logs

```powershell
# Tous les logs
docker-compose logs -f

# Logs d'un service
docker-compose logs -f backend
docker-compose logs -f frontend
```

### Rebuild

```powershell
# Rebuild le backend après modifications
docker-compose up -d --build backend

# Rebuild le frontend
docker-compose up -d --build frontend
```

## 🧪 Tests

### Exécuter les tests

```powershell
# Tests unitaires + JaCoCo
.\mvnw.cmd clean test

# Tests + rapport JaCoCo
.\mvnw.cmd clean verify jacoco:report

# Rapport disponible dans
target/site/jacoco/index.html
```

### Coverage Actuelle

- **Classes** : 49.3%
- **Méthodes** : 16.0%
- **Lignes** : 8.9%
- **Branches** : 3.2%

## 🔄 CI/CD Pipeline (Jenkins)

### Stages du Pipeline

1. **Checkout** - Clone du code GitHub
2. **Build & Tests** - Maven + JUnit + JaCoCo
3. **Publish Reports** - JUnit + JaCoCo dans Jenkins
4. **SonarQube Analysis** - Analyse de code
5. **Quality Gate** - Validation qualité
6. **Docker Build** - Build image Docker
7. **Docker Push** - Push vers Docker Hub (linda296/backend)

### Variables

```groovy
IMAGE_NAME = 'linda296/backend'
IMAGE_TAG = '5.1.0'
SONAR_PROJECT_KEY = 'tn.esprit:backend'
```

## 🔐 Sécurité

### Développement

- ✅ JWT pour l'authentification
- ✅ CORS configuré pour localhost
- ✅ Validation des entrées
- ✅ Prepared statements (JPA)

### Production

- [ ] Changer tous les mots de passe
- [ ] Générer nouveau JWT_SECRET
- [ ] Activer HTTPS
- [ ] Désactiver PhpMyAdmin
- [ ] Configurer firewall
- [ ] Rate limiting
- [ ] Audit logs

## 🚨 Dépannage

### Backend ne démarre pas

```powershell
docker-compose logs backend
docker exec -it mysql-db mysql -u pidev -ppidev123 -e "SELECT 1;"
```

### Frontend ne charge pas

```powershell
docker-compose logs frontend
curl http://localhost
```

### Port déjà utilisé

```powershell
# Trouver le processus
netstat -ano | findstr :80

# Tuer le processus
taskkill /PID <PID> /F
```

### Réinitialiser tout

```powershell
docker-compose down -v --rmi all
docker system prune -a
docker-compose up -d --build
```

## 📊 Monitoring

### Health Checks

```powershell
# Backend
curl http://localhost:8089/actuator/health

# Frontend
curl http://localhost

# MySQL
docker exec mysql-db mysqladmin ping
```

### Métriques

- **Backend** : http://localhost:8089/actuator/metrics
- **Prometheus** : http://localhost:8089/actuator/prometheus

## 👥 Équipe de Développement

### Pour commencer

1. Installer Docker Desktop
2. Cloner le repository
3. Copier `.env.example` → `.env`
4. Lancer `.\start-docker.ps1`
5. Ouvrir http://localhost

### Workflow Git

```bash
# Feature branch
git checkout -b feature/nouvelle-fonctionnalite

# Commit
git add .
git commit -m "feat: description"

# Push
git push origin feature/nouvelle-fonctionnalite

# Pull Request vers main
```

## 📝 TODO

- [ ] Augmenter la couverture de tests (>80%)
- [ ] Ajouter tests E2E (Cypress/Selenium)
- [ ] Configurer Kubernetes
- [ ] Mettre en place monitoring (Prometheus + Grafana)
- [ ] Ajouter cache Redis
- [ ] Documentation Swagger/OpenAPI
- [ ] Internationalisation (i18n)

## 📞 Support

### Documentation

- Consultez les fichiers `*.md` dans le repository
- Lisez [FULL_STACK_DEPLOYMENT.md](./FULL_STACK_DEPLOYMENT.md)

### Logs

```powershell
docker-compose logs -f > logs.txt
```

### Issues GitHub

Ouvrez une issue sur :
- Backend : https://github.com/lindabenjeddou/backendvfdevops/issues
- Frontend : https://github.com/lindabenjeddou/pfesagemfinalefrontend/issues

## 📜 License

MIT License - Voir le fichier LICENSE

---

**Dernière mise à jour** : 7 novembre 2024  
**Version** : 5.1.0  
**Stack** : Spring Boot 3 + React 18 + MySQL 8 + Docker
