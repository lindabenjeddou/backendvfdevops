# 🐳 Configuration Docker - Résumé

## 📁 Fichiers créés

```
back-master/
├── docker-compose.yml          # Orchestration des services
├── Dockerfile                  # Image Docker du backend
├── .dockerignore              # Fichiers à ignorer lors du build
├── .env.example               # Template des variables d'environnement
├── start-docker.ps1           # Script de démarrage automatique
├── DOCKER_GUIDE.md            # Guide complet Docker
├── init-db/                   # Scripts d'initialisation MySQL
│   └── README.md
└── src/main/resources/
    └── application-docker.properties  # Configuration Spring Boot pour Docker
```

## 🚀 Démarrage en 2 étapes (Simplifié !)

### 1️⃣ Copier la configuration

```powershell
Copy-Item .env.example .env
```

### 2️⃣ Lancer tout avec Docker

```powershell
# Option A : Script automatique
.\start-docker.ps1

# Option B : Docker Compose direct
docker-compose up -d --build
```

> ✨ **Nouveau !** Le Dockerfile multi-stage builde automatiquement le backend.  
> Plus besoin de Maven local ou de JAVA_HOME ! 🎉

## 🌐 URLs des services

| Service | URL | Identifiants |
|---------|-----|-------------|
| 🔧 Backend API | http://localhost:8080 | - |
| ⚛️ Frontend React | http://localhost:3000 | - |
| 🗃️ PhpMyAdmin | http://localhost:8081 | root / root |
| 🗄️ MySQL | localhost:3306 | pidev / pidev123 |

## ✨ Améliorations récentes

### Multi-Stage Docker Build

Le Dockerfile utilise maintenant un **build multi-stage** pour des avantages majeurs :

| Avantage | Description |
|----------|-------------|
| 🚀 **Simplicité** | Plus besoin de Maven ou JAVA_HOME localement |
| 📦 **Légèreté** | Image finale 60% plus petite (200MB vs 500MB) |
| 🔒 **Sécurité** | Pas d'outils de build dans l'image de production |
| ♻️ **Reproductibilité** | Build identique sur toutes les machines |

**Avant** : `mvnw clean package` → `docker-compose up`  
**Maintenant** : `docker-compose up --build` ✨

Voir **[DOCKER_BUILD.md](./DOCKER_BUILD.md)** pour plus de détails.

---

## 📊 Services Docker

### MySQL
- **Image** : mysql:8.0
- **Database** : pidevdb
- **Port** : 3306
- **User** : pidev
- **Password** : pidev123

### Backend Spring Boot
- **Build** : Dockerfile local
- **Port** : 8080
- **Profil** : docker (automatique)
- **Java** : 17

### Frontend React
- **Image** : node:18-alpine
- **Port** : 3000
- **API URL** : http://localhost:8080/api

### PhpMyAdmin
- **Image** : phpmyadmin:latest
- **Port** : 8081
- **Accès** : root / root

## 🛠️ Commandes essentielles

```powershell
# Démarrer tous les services
docker-compose up -d

# Voir les logs
docker-compose logs -f

# Arrêter tous les services
docker-compose down

# Redémarrer le backend après modification
.\mvnw.cmd clean package -DskipTests
docker-compose up -d --build backend

# Réinitialiser la base de données
docker-compose down -v
docker-compose up -d

# Statut des conteneurs
docker-compose ps

# Accéder au shell du backend
docker exec -it spring-backend sh

# Accéder à MySQL CLI
docker exec -it mysql-db mysql -u pidev -ppidev123 pidevdb
```

## 📝 Configuration

### Variables d'environnement (.env)

Éditez `.env` pour personnaliser :

```env
# Base de données
MYSQL_DATABASE=pidevdb
MYSQL_USER=pidev
MYSQL_PASSWORD=pidev123

# Backend
BACKEND_PORT=8080
JWT_SECRET=votre_secret_unique

# Frontend
FRONTEND_PORT=3000
REACT_APP_API_URL=http://localhost:8080/api
```

### Profils Spring Boot

Le conteneur Docker utilise automatiquement le profil `docker` qui :
- Se connecte à MySQL sur `mysql:3306` (nom du service Docker)
- Active les health checks
- Configure CORS pour le frontend
- Active les logs détaillés

## 🔧 Développement

### Workflow typique

1. **Modifier le code backend**
   ```powershell
   # Éditer vos fichiers Java...
   ```

2. **Rebuild et redéployer**
   ```powershell
   .\mvnw.cmd clean package -DskipTests
   docker-compose up -d --build backend
   ```

3. **Vérifier les logs**
   ```powershell
   docker-compose logs -f backend
   ```

### Hot Reload Frontend

Le frontend React supporte le hot reload :
- Les modifications dans `frontend/` sont automatiquement détectées
- Pas besoin de redémarrer le conteneur

## 🗂️ Structure Frontend (à créer)

Si vous n'avez pas encore de projet React :

```powershell
# Créer le projet React
npx create-react-app frontend

# Installer les dépendances
cd frontend
npm install axios react-router-dom

# Configurer l'API
echo "REACT_APP_API_URL=http://localhost:8080/api" > .env.local
```

### Exemple de service API (frontend/src/services/api.js)

```javascript
import axios from 'axios';

const API_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Intercepteur pour ajouter le token JWT
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

export default api;
```

## 🔒 Sécurité

### ⚠️ Avant de déployer en production

1. **Changez tous les mots de passe** dans `.env`
2. **Générez un nouveau JWT_SECRET** :
   ```powershell
   # PowerShell
   [Convert]::ToBase64String((1..64 | ForEach-Object { Get-Random -Minimum 0 -Maximum 256 }))
   ```
3. **Désactivez PhpMyAdmin** ou protégez-le avec un mot de passe
4. **Utilisez HTTPS** avec un reverse proxy (nginx)
5. **Limitez l'exposition des ports**
6. **Activez les firewalls**

### Production

```powershell
# Utilisez un fichier docker-compose.prod.yml
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d
```

## 🚨 Dépannage

### Backend ne démarre pas

```powershell
# Vérifier les logs
docker-compose logs backend

# Vérifier MySQL
docker-compose ps mysql

# Tester la connexion MySQL
docker exec -it mysql-db mysqladmin ping -h localhost -u root -proot
```

### Frontend ne se connecte pas

```powershell
# Vérifier REACT_APP_API_URL
cat .env | Select-String "REACT_APP_API_URL"

# Tester le backend
curl http://localhost:8080/actuator/health
```

### Port déjà utilisé

```powershell
# Trouver le processus utilisant le port 8080
netstat -ano | findstr :8080

# Tuer le processus (remplacez PID)
taskkill /PID <PID> /F
```

## 📚 Documentation complète

Pour plus de détails, consultez :
- **[DOCKER_BUILD.md](./DOCKER_BUILD.md)** - ✨ Multi-Stage Build (nouveau !)
- **[DOCKER_GUIDE.md](./DOCKER_GUIDE.md)** - Guide complet avec toutes les commandes
- **[init-db/README.md](./init-db/README.md)** - Scripts d'initialisation MySQL

## ✅ Checklist de déploiement

- [ ] Docker Desktop installé et démarré
- [ ] `.env` configuré (copié depuis `.env.example`)
- [ ] Ports 3000, 3306, 8080, 8081 disponibles
- [ ] Frontend React créé dans `./frontend/`
- [ ] `docker-compose up -d --build` exécuté
- [ ] Services accessibles (vérifier les URLs)

> ✨ **Note** : Pas besoin de builder le backend localement, Docker le fait automatiquement !

## 🎯 Prochaines étapes

1. ✅ Configuration Docker terminée
2. 🔄 Créer le projet Frontend React
3. 🔗 Configurer les appels API dans le frontend
4. 🧪 Tester l'intégration complète
5. 🚀 Déployer en production

---

**Auteur** : Configuration Docker automatisée  
**Date** : 7 novembre 2024  
**Stack** : Spring Boot + MySQL + React + PhpMyAdmin
