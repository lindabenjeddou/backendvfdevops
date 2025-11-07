# ⚡ Quick Start - Démarrage Rapide

## 🎯 Objectif

Démarrer l'application complète (Backend + Frontend + MySQL) en **moins de 2 minutes**.

## ✅ Prérequis

```powershell
# Vérifier Docker
docker --version
# ✅ Docker version 20.x ou supérieur

# Vérifier Docker Compose
docker-compose --version
# ✅ Docker Compose version 2.x ou supérieur

# Vérifier que Docker Desktop est démarré
docker info
# ✅ Doit afficher les informations système
```

## 🚀 Démarrage (3 commandes)

```powershell
# 1️⃣ Naviguer vers le projet
cd C:\Users\user\OneDrive\Bureau\back-master

# 2️⃣ Copier la configuration
Copy-Item .env.example .env

# 3️⃣ Tout démarrer !
.\start-docker.ps1
```

## ⏱️ Timeline

```
00:00 ⏳ Démarrage de Docker Compose...
00:30 🗄️  MySQL démarre...
01:00 🔧 Backend build en cours...
01:30 ⚛️  Frontend build en cours...
02:00 ✅ Tous les services sont prêts !
```

## 🌐 Accès Rapide

| Service | URL | Status |
|---------|-----|--------|
| **Frontend** | http://localhost | 🌐 Interface utilisateur |
| **Backend API** | http://localhost:8089 | 🔧 REST API |
| **Health Check** | http://localhost:8089/actuator/health | 📊 {"status":"UP"} |
| **PhpMyAdmin** | http://localhost:8081 | 🗃️ root / root |
| **MySQL** | localhost:3306 | 🗄️ pidev / pidev123 |

## 🎨 Vérification Visuelle

### ✅ Tout fonctionne si :

```powershell
docker-compose ps
```

Affiche :
```
NAME               STATUS         PORTS
mysql-db           Up (healthy)   0.0.0.0:3306->3306/tcp
spring-backend     Up (healthy)   0.0.0.0:8089->8089/tcp
react-frontend     Up             0.0.0.0:80->80/tcp
phpmyadmin         Up             0.0.0.0:8081->80/tcp
```

### 🔍 Tests rapides

```powershell
# Test Backend
curl http://localhost:8089/actuator/health
# ✅ {"status":"UP"}

# Test Frontend
curl http://localhost
# ✅ Code HTML de React

# Test MySQL
docker exec -it mysql-db mysql -u pidev -ppidev123 -e "SELECT 1;"
# ✅ | 1 |
```

## 🛑 Arrêt

```powershell
# Arrêter proprement
docker-compose down

# Arrêter et nettoyer les volumes (⚠️ perte de données)
docker-compose down -v
```

## 🔄 Redémarrage

```powershell
# Redémarrer tout
docker-compose restart

# Redémarrer un service
docker-compose restart backend
```

## 📊 Voir les Logs

```powershell
# Tous les logs en temps réel
docker-compose logs -f

# Logs d'un service spécifique
docker-compose logs -f backend

# Dernières 50 lignes
docker-compose logs --tail=50 backend
```

## 🚨 Problèmes Courants

### ❌ "Port is already allocated"

```powershell
# Trouver le processus
netstat -ano | findstr :80    # ou :8089, :3306

# Tuer le processus
taskkill /PID <PID> /F

# Ou changer le port dans .env
FRONTEND_PORT=3000
BACKEND_PORT=8090
```

### ❌ "Cannot connect to MySQL"

```powershell
# Vérifier que MySQL est healthy
docker-compose ps mysql

# Voir les logs MySQL
docker-compose logs mysql

# Redémarrer MySQL
docker-compose restart mysql
```

### ❌ "Frontend ne charge pas"

```powershell
# Vérifier les logs
docker-compose logs frontend

# Rebuild le frontend
docker-compose up -d --build frontend

# Vider le cache du navigateur (Ctrl+Shift+R)
```

### ❌ "Docker Desktop n'est pas démarré"

1. Ouvrir Docker Desktop
2. Attendre qu'il soit prêt (icône verte)
3. Relancer `.\start-docker.ps1`

## 🔧 Modifications Rapides

### Backend (Java)

```powershell
# 1. Modifier le code Java
# 2. Rebuild
docker-compose up -d --build backend
# 3. Vérifier
docker-compose logs -f backend
```

### Frontend (React)

```powershell
# 1. Modifier le code React dans notus-react-main/
# 2. Rebuild
docker-compose up -d --build frontend
# 3. Rafraîchir le navigateur (Ctrl+F5)
```

### Base de données

```powershell
# Accéder à PhpMyAdmin
# http://localhost:8081
# Serveur: mysql
# User: root
# Password: root
```

## 📚 Documentation Complète

| Fichier | Description |
|---------|-------------|
| **README_FULLSTACK.md** | Vue d'ensemble complète |
| **FULL_STACK_DEPLOYMENT.md** | Guide de déploiement détaillé |
| **DOCKER_GUIDE.md** | Commandes Docker avancées |
| **GUIDE_TESTS.md** | Exécuter les tests |

## 🎯 Prochaines Étapes

### Pour le développement

1. ✅ Lancer l'application
2. 📖 Lire **FULL_STACK_DEPLOYMENT.md**
3. 🧪 Exécuter les tests : `.\mvnw.cmd test`
4. 📊 Voir la couverture : `target/site/jacoco/index.html`

### Pour la production

1. 📝 Changer les mots de passe dans `.env`
2. 🔐 Générer nouveau JWT_SECRET
3. 🌐 Configurer HTTPS
4. 🚀 Déployer avec Jenkins

## 💡 Astuces

### Gain de temps

```powershell
# Alias PowerShell (ajouter dans $PROFILE)
function Start-App { docker-compose up -d }
function Stop-App { docker-compose down }
function Logs-App { docker-compose logs -f }

# Utilisation
Start-App
Logs-App backend
Stop-App
```

### Monitoring en continu

```powershell
# Terminal 1: Logs backend
docker-compose logs -f backend

# Terminal 2: Logs frontend
docker-compose logs -f frontend

# Terminal 3: Logs MySQL
docker-compose logs -f mysql
```

### Auto-restart en développement

Les conteneurs sont configurés avec `restart: always`, donc ils redémarrent automatiquement si :
- Docker Desktop redémarre
- Le PC redémarre
- Un conteneur crash

## ✅ Checklist Post-Installation

- [ ] Frontend accessible → http://localhost
- [ ] Backend accessible → http://localhost:8089
- [ ] Health check OK → http://localhost:8089/actuator/health
- [ ] PhpMyAdmin OK → http://localhost:8081
- [ ] MySQL accessible → localhost:3306
- [ ] Tous conteneurs "Up (healthy)" → `docker-compose ps`
- [ ] Aucune erreur dans les logs → `docker-compose logs`

## 🆘 Besoin d'Aide ?

```powershell
# État complet du système
docker-compose ps
docker-compose logs

# Nettoyer et redémarrer (⚠️ perte de données)
docker-compose down -v
docker-compose up -d --build

# Documentation
Get-Content FULL_STACK_DEPLOYMENT.md
```

---

**🎉 Félicitations !** Votre stack full-stack est opérationnelle !

**Prochaine étape** → Ouvrir http://localhost dans votre navigateur
