# 🚀 Déploiement Full-Stack - Backend + Frontend React

## 📋 Architecture du Projet

```
┌──────────────────────────────────────────────────────────────┐
│                     Docker Network                           │
│                                                              │
│  ┌─────────────┐         ┌──────────────┐                  │
│  │   Nginx     │         │   Spring     │                  │
│  │   (React)   │────────▶│    Boot      │                  │
│  │   Port 80   │  /api   │   Port 8089  │                  │
│  └─────────────┘         └──────┬───────┘                  │
│                                  │                           │
│                                  ▼                           │
│  ┌─────────────┐         ┌──────────────┐                  │
│  │ PhpMyAdmin  │────────▶│    MySQL     │                  │
│  │  Port 8081  │         │   Port 3306  │                  │
│  └─────────────┘         └──────────────┘                  │
│                                                              │
└──────────────────────────────────────────────────────────────┘
```

## 🗂️ Chemins des Projets

| Composant | Chemin Local | Repository Git |
|-----------|-------------|----------------|
| **Backend** | `C:\Users\user\OneDrive\Bureau\back-master` | https://github.com/lindabenjeddou/backendvfdevops.git |
| **Frontend** | `C:\Users\user\Downloads\notus-react-main\notus-react-main` | https://github.com/lindabenjeddou/pfesagemfinalefrontend.git |

## 📦 Services Docker

| Service | Port | URL | Description |
|---------|------|-----|-------------|
| **Frontend** | 80 | http://localhost | React (Notus) avec Nginx |
| **Backend** | 8089 | http://localhost:8089 | Spring Boot API |
| **MySQL** | 3306 | localhost:3306 | Base de données |
| **PhpMyAdmin** | 8081 | http://localhost:8081 | Administration BD |

## 🚀 Démarrage Rapide

### Prérequis

- ✅ Docker Desktop installé et démarré
- ✅ Ports disponibles : 80, 3306, 8089, 8081

### Option 1 : Démarrage Automatique (Recommandé)

```powershell
# Naviguer vers le dossier backend
cd C:\Users\user\OneDrive\Bureau\back-master

# Copier la configuration
Copy-Item .env.example .env

# Lancer tous les services avec le script
.\start-docker.ps1
```

### Option 2 : Démarrage Manuel

```powershell
# 1. Naviguer vers le backend
cd C:\Users\user\OneDrive\Bureau\back-master

# 2. Copier .env
Copy-Item .env.example .env

# 3. Build et démarrer tous les services
docker-compose up -d --build

# 4. Vérifier les logs
docker-compose logs -f
```

### Vérification du Déploiement

Après ~2-3 minutes, vérifiez que tous les services sont en ligne :

```powershell
# Statut des conteneurs
docker-compose ps

# Health check backend
curl http://localhost:8089/actuator/health

# Health check frontend
curl http://localhost
```

## 🌐 Accès aux Services

### Frontend React
- **URL** : http://localhost
- **Technologie** : React 18 + Tailwind CSS (Notus Template)
- **Nginx** : Proxy automatique vers l'API backend

### Backend API
- **URL** : http://localhost:8089
- **Health** : http://localhost:8089/actuator/health
- **Swagger** : http://localhost:8089/swagger-ui.html
- **Technologie** : Spring Boot 3 + Java 17

### PhpMyAdmin
- **URL** : http://localhost:8081
- **Serveur** : mysql
- **Utilisateur** : root
- **Mot de passe** : root

### MySQL
- **Host** : localhost
- **Port** : 3306
- **Base** : pidevdb
- **User** : pidev
- **Password** : pidev123

## 🔧 Configuration

### Variables d'Environnement (.env)

```env
# MySQL
MYSQL_ROOT_PASSWORD=root
MYSQL_DATABASE=pidevdb
MYSQL_USER=pidev
MYSQL_PASSWORD=pidev123

# Backend
BACKEND_PORT=8089
SERVER_PORT=8089
JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970

# Frontend
FRONTEND_PORT=80
NODE_ENV=production
REACT_APP_API_URL=http://localhost:8089

# PhpMyAdmin
PHPMYADMIN_PORT=8081
```

### Nginx Configuration (Frontend)

Le frontend utilise Nginx comme reverse proxy. La configuration (`nginx.conf` dans le frontend) route automatiquement les requêtes `/api/*` vers le backend :

```nginx
location /api {
    proxy_pass http://backend:8089;
    # ... headers ...
}
```

## 📁 Structure du Projet

```
back-master/                          # Repository Backend
├── docker-compose.yml                # ⭐ Orchestration complète (backend + frontend)
├── Dockerfile                        # Multi-stage build backend
├── .env.example                      # Variables d'environnement
├── src/
│   ├── main/
│   │   ├── java/                     # Code Java
│   │   └── resources/
│   │       └── application-docker.properties  # Config Docker
│   └── test/                         # Tests JUnit
├── pom.xml                           # Dépendances Maven
└── Jenkinsfile                       # Pipeline CI/CD

notus-react-main/                     # Repository Frontend
├── Dockerfile                        # Multi-stage build frontend (React + Nginx)
├── nginx.conf                        # Configuration Nginx avec proxy
├── package.json                      # Dépendances NPM
├── src/                              # Code React
└── public/                           # Assets statiques
```

## 🛠️ Commandes Utiles

### Gestion des Conteneurs

```powershell
# Démarrer tous les services
cd C:\Users\user\OneDrive\Bureau\back-master
docker-compose up -d

# Arrêter tous les services
docker-compose down

# Arrêter et supprimer les volumes (⚠️ perte de données)
docker-compose down -v

# Redémarrer un service spécifique
docker-compose restart backend
docker-compose restart frontend

# Rebuild un service
docker-compose up -d --build backend
docker-compose up -d --build frontend
```

### Logs et Debugging

```powershell
# Logs de tous les services
docker-compose logs -f

# Logs d'un service spécifique
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f mysql

# Dernières 100 lignes
docker-compose logs --tail=100 backend

# Accéder au shell d'un conteneur
docker exec -it spring-backend sh
docker exec -it react-frontend sh
```

### Base de Données

```powershell
# Accéder à MySQL CLI
docker exec -it mysql-db mysql -u pidev -ppidev123 pidevdb

# Backup de la base
docker exec mysql-db mysqldump -u pidev -ppidev123 pidevdb > backup.sql

# Restaurer un backup
docker exec -i mysql-db mysql -u pidev -ppidev123 pidevdb < backup.sql

# Voir les tables
docker exec -it mysql-db mysql -u pidev -ppidev123 pidevdb -e "SHOW TABLES;"
```

## 🔄 Workflow de Développement

### Modification du Backend

```powershell
# 1. Modifier le code Java dans back-master/
# 2. Rebuild le backend
cd C:\Users\user\OneDrive\Bureau\back-master
docker-compose up -d --build backend

# 3. Vérifier les logs
docker-compose logs -f backend
```

### Modification du Frontend

```powershell
# 1. Modifier le code React dans notus-react-main/
# 2. Rebuild le frontend
cd C:\Users\user\OneDrive\Bureau\back-master
docker-compose up -d --build frontend

# 3. Vérifier dans le navigateur
# Ouvrir http://localhost
```

### Test de l'API depuis le Frontend

Le frontend peut appeler l'API de deux façons :

1. **Via Nginx Proxy** (Recommandé) :
   ```javascript
   // Dans le code React
   axios.get('/api/components')  // Proxé par Nginx vers http://backend:8089/api/components
   ```

2. **Directement** :
   ```javascript
   axios.get('http://localhost:8089/api/components')
   ```

## 🚨 Dépannage

### Le backend ne démarre pas

```powershell
# Vérifier que MySQL est healthy
docker-compose ps

# Voir les logs détaillés
docker-compose logs backend

# Vérifier la connexion MySQL
docker exec -it mysql-db mysql -u pidev -ppidev123 -e "SELECT 1;"
```

### Le frontend ne se charge pas

```powershell
# Vérifier le conteneur
docker-compose ps frontend

# Voir les logs Nginx
docker-compose logs frontend

# Vérifier la build
docker-compose build --no-cache frontend
```

### Erreur CORS

Si vous voyez des erreurs CORS dans la console du navigateur :

1. Vérifiez que `application-docker.properties` contient :
   ```properties
   spring.web.cors.allowed-origins=http://localhost,http://frontend
   ```

2. Ou utilisez le proxy Nginx (déjà configuré) en appelant `/api/*` au lieu de `http://localhost:8089/api/*`

### Port déjà utilisé

```powershell
# Trouver le processus sur le port 80
netstat -ano | findstr :80

# Tuer le processus (PID)
taskkill /PID <PID> /F

# Ou changer le port dans .env
FRONTEND_PORT=3000
```

### Réinitialiser complètement

```powershell
# Arrêter et supprimer tout
docker-compose down -v --rmi all

# Nettoyer Docker
docker system prune -a

# Redémarrer
docker-compose up -d --build
```

## 📊 Monitoring et Health Checks

### Health Checks Automatiques

Docker vérifie automatiquement la santé des services :

| Service | Health Check | Interval |
|---------|--------------|----------|
| MySQL | `mysqladmin ping` | 5s |
| Backend | `/actuator/health` | 30s |
| Frontend | Nginx port 80 | 30s |

### Vérifications Manuelles

```powershell
# Backend
curl http://localhost:8089/actuator/health

# Frontend
curl http://localhost

# MySQL
docker exec mysql-db mysqladmin ping -h localhost -u root -proot
```

## 🔐 Sécurité

### ⚠️ Avant la Production

1. **Changez tous les mots de passe** dans `.env`
2. **Générez un nouveau JWT_SECRET** :
   ```powershell
   [Convert]::ToBase64String((1..64 | ForEach-Object { Get-Random -Minimum 0 -Maximum 256 }))
   ```
3. **Utilisez HTTPS** avec un certificat SSL
4. **Désactivez PhpMyAdmin** en production
5. **Configurez un firewall**
6. **Utilisez des secrets Docker** au lieu de `.env`

### Production Best Practices

```yaml
# docker-compose.prod.yml
services:
  backend:
    environment:
      SPRING_PROFILES_ACTIVE: prod
      SPRING_JPA_HIBERNATE_DDL_AUTO: validate
      SPRING_JPA_SHOW_SQL: "false"
  
  phpmyadmin:
    profiles: [dev]  # Ne démarre pas en prod
```

## 🎯 CI/CD avec Jenkins

Le projet inclut un `Jenkinsfile` pour l'intégration continue :

### Pipeline Stages

1. **Checkout** : Clone le code depuis GitHub
2. **Build & Tests** : Maven build + JUnit + JaCoCo
3. **SonarQube Analysis** : Analyse de code
4. **Quality Gate** : Validation SonarQube
5. **Docker Build** : Build de l'image Docker
6. **Docker Push** : Push vers Docker Hub

### Configuration Jenkins

```groovy
environment {
    IMAGE_NAME = 'linda296/backend'
    IMAGE_TAG = '5.1.0'
    SONAR_PROJECT_KEY = 'tn.esprit:backend'
}
```

### Déploiement depuis Docker Hub

```powershell
# Pull l'image depuis Docker Hub
docker pull linda296/backend:5.1.0

# Run le conteneur
docker run -d -p 8089:8089 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/pidevdb \
  -e SPRING_DATASOURCE_USERNAME=pidev \
  -e SPRING_DATASOURCE_PASSWORD=pidev123 \
  linda296/backend:5.1.0
```

## 📚 Documentation Complémentaire

- **[DOCKER_README.md](./DOCKER_README.md)** - Résumé Docker
- **[DOCKER_BUILD.md](./DOCKER_BUILD.md)** - Multi-Stage Build
- **[DOCKER_GUIDE.md](./DOCKER_GUIDE.md)** - Guide complet Docker
- **[GUIDE_TESTS.md](./GUIDE_TESTS.md)** - Tests JUnit + Mockito

## ✅ Checklist de Déploiement

### Développement

- [ ] Docker Desktop installé et démarré
- [ ] Cloner les deux repositories (backend + frontend)
- [ ] Copier `.env.example` vers `.env`
- [ ] Ports disponibles : 80, 3306, 8089, 8081
- [ ] Lancer `docker-compose up -d --build`
- [ ] Vérifier http://localhost (frontend)
- [ ] Vérifier http://localhost:8089/actuator/health (backend)

### Production

- [ ] Changer tous les mots de passe
- [ ] Générer un nouveau JWT_SECRET
- [ ] Configurer HTTPS avec certificat SSL
- [ ] Désactiver PhpMyAdmin
- [ ] Configurer le firewall
- [ ] Tester le pipeline Jenkins
- [ ] Configurer les backups MySQL
- [ ] Mettre en place le monitoring

## 🎓 Formation Rapide

### Pour un nouveau développeur

1. Installer Docker Desktop
2. Cloner le repository backend
3. Naviguer dans le dossier : `cd C:\Users\user\OneDrive\Bureau\back-master`
4. Copier la config : `Copy-Item .env.example .env`
5. Lancer : `.\start-docker.ps1`
6. Accéder à http://localhost

**C'est tout !** Le script gère automatiquement :
- ✅ Build Maven du backend
- ✅ Build npm du frontend
- ✅ Configuration de MySQL
- ✅ Démarrage de tous les services

## 🆘 Support

### Logs importants

```powershell
# Tout voir
docker-compose logs -f

# Juste les erreurs
docker-compose logs -f | Select-String "ERROR"

# Export des logs
docker-compose logs > logs.txt
```

### Informations système

```powershell
# Version Docker
docker --version
docker-compose --version

# État des conteneurs
docker-compose ps

# Utilisation des ressources
docker stats

# Réseau Docker
docker network ls
docker network inspect pidev-network
```

---

**Créé le** : 7 novembre 2024  
**Stack** : Spring Boot + React + MySQL + Nginx  
**Auteur** : Configuration Full-Stack automatisée
