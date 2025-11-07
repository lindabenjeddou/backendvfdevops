# 🐳 Guide Docker Compose - Full Stack Application

## 📋 Stack Technologique

- **Backend** : Spring Boot (Java 17)
- **Base de données** : MySQL 8.0
- **Frontend** : React (Node 18)
- **Administration BD** : PhpMyAdmin

---

## 🚀 Démarrage Rapide

### Prérequis

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) installé et démarré
- Ports disponibles : 3000, 3306, 8080, 8081

### 1. Build du Backend

Avant de lancer Docker Compose, construisez le JAR :

```powershell
# Avec Maven Wrapper (recommandé)
.\mvnw.cmd clean package -DskipTests

# Ou avec Maven installé
mvn clean package -DskipTests
```

### 2. Configuration

Copiez le fichier d'exemple et adaptez si nécessaire :

```powershell
Copy-Item .env.example .env
```

Éditez `.env` pour modifier les mots de passe et configurations.

### 3. Lancer tous les services

```powershell
# Démarrer tous les conteneurs
docker-compose up -d

# Voir les logs
docker-compose logs -f

# Voir les logs d'un service spécifique
docker-compose logs -f backend
```

### 4. Vérifier le déploiement

Attendez quelques secondes puis vérifiez :

- **Backend API** : http://localhost:8080
- **Frontend React** : http://localhost:3000
- **PhpMyAdmin** : http://localhost:8081
- **MySQL** : localhost:3306

---

## 📦 Services Docker

### 🗄️ MySQL Database

```yaml
Service: mysql
Port: 3306
User: pidev
Password: pidev123
Database: pidevdb
```

**Accès direct** :
```powershell
docker exec -it mysql-db mysql -u pidev -ppidev123 pidevdb
```

### 🔧 Backend Spring Boot

```yaml
Service: backend
Port: 8080
Health: http://localhost:8080/actuator/health
```

**Logs en temps réel** :
```powershell
docker-compose logs -f backend
```

### ⚛️ Frontend React

```yaml
Service: frontend
Port: 3000
URL: http://localhost:3000
```

**Rebuild du frontend** :
```powershell
docker-compose restart frontend
```

### 🗃️ PhpMyAdmin

```yaml
Service: phpmyadmin
Port: 8081
User: root
Password: root
```

Accès : http://localhost:8081

---

## 🛠️ Commandes Utiles

### Gestion des conteneurs

```powershell
# Démarrer tous les services
docker-compose up -d

# Arrêter tous les services
docker-compose down

# Redémarrer un service
docker-compose restart backend

# Voir les services en cours
docker-compose ps

# Arrêter et supprimer tout (y compris volumes)
docker-compose down -v
```

### Logs et Debug

```powershell
# Logs de tous les services
docker-compose logs -f

# Logs d'un service spécifique
docker-compose logs -f backend

# Dernières 100 lignes
docker-compose logs --tail=100 backend

# Accéder au shell d'un conteneur
docker exec -it spring-backend sh
```

### Build et Rebuild

```powershell
# Rebuild le backend après des modifications
docker-compose build backend

# Rebuild et redémarrer
docker-compose up -d --build backend

# Rebuild tout
docker-compose build --no-cache
```

### Base de données

```powershell
# Backup de la base de données
docker exec mysql-db mysqldump -u pidev -ppidev123 pidevdb > backup.sql

# Restaurer une sauvegarde
docker exec -i mysql-db mysql -u pidev -ppidev123 pidevdb < backup.sql

# Accès MySQL CLI
docker exec -it mysql-db mysql -u pidev -ppidev123 pidevdb
```

---

## 🔧 Configuration Avancée

### Variables d'environnement

Éditez `.env` pour personnaliser :

```env
# Ports
BACKEND_PORT=8080
FRONTEND_PORT=3000
MYSQL_PORT=3306

# Base de données
MYSQL_DATABASE=pidevdb
MYSQL_USER=pidev
MYSQL_PASSWORD=pidev123

# JWT Secret (CHANGEZ EN PRODUCTION !)
JWT_SECRET=votre_secret_jwt_unique
```

### Volumes persistants

Les données MySQL sont persistées dans un volume Docker :

```powershell
# Lister les volumes
docker volume ls

# Inspecter le volume MySQL
docker volume inspect back-master_mysql_data

# Supprimer le volume (⚠️ PERTE DE DONNÉES)
docker volume rm back-master_mysql_data
```

---

## 🚨 Dépannage

### Le backend ne démarre pas

1. Vérifiez que MySQL est healthy :
   ```powershell
   docker-compose ps
   ```

2. Vérifiez les logs :
   ```powershell
   docker-compose logs backend
   ```

3. Vérifiez la connexion à la base :
   ```powershell
   docker exec -it mysql-db mysql -u pidev -ppidev123 -e "SHOW DATABASES;"
   ```

### Le frontend ne se connecte pas au backend

1. Vérifiez `REACT_APP_API_URL` dans `.env`
2. Vérifiez que le backend est accessible :
   ```powershell
   curl http://localhost:8080/actuator/health
   ```

### Ports déjà utilisés

Si un port est déjà utilisé, modifiez dans `.env` :

```env
BACKEND_PORT=8081
FRONTEND_PORT=3001
```

Puis dans `docker-compose.yml`, utilisez `${BACKEND_PORT}:8080`

### Nettoyer complètement

```powershell
# Arrêter et supprimer tout
docker-compose down -v

# Supprimer les images
docker-compose down --rmi all

# Nettoyer Docker
docker system prune -a
```

---

## 📝 Structure Frontend (à créer)

Si vous n'avez pas encore de frontend React, créez-le :

```powershell
# À la racine du projet
npx create-react-app frontend
cd frontend

# Installer axios pour les appels API
npm install axios

# Créer .env.local
echo "REACT_APP_API_URL=http://localhost:8080/api" > .env.local
```

---

## 🔐 Production

### Sécurité

⚠️ **Avant de déployer en production** :

1. Changez tous les mots de passe dans `.env`
2. Utilisez un nouveau `JWT_SECRET` :
   ```powershell
   # Générer un secret aléatoire
   [Convert]::ToBase64String((1..64 | ForEach-Object { Get-Random -Minimum 0 -Maximum 256 }))
   ```
3. Utilisez HTTPS avec un reverse proxy (nginx)
4. Limitez l'accès à PhpMyAdmin
5. Utilisez des variables d'environnement sécurisées

### Docker Compose Production

Créez `docker-compose.prod.yml` :

```yaml
version: '3.8'

services:
  backend:
    restart: always
    environment:
      SPRING_PROFILES_ACTIVE: prod
      SPRING_JPA_HIBERNATE_DDL_AUTO: validate
      SPRING_JPA_SHOW_SQL: "false"
```

Lancez avec :
```powershell
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d
```

---

## 📊 Monitoring

### Health Checks

```powershell
# Backend health
curl http://localhost:8080/actuator/health

# MySQL health
docker exec mysql-db mysqladmin ping -h localhost -u root -proot

# Statut des conteneurs
docker-compose ps
```

### Métriques

Activez Spring Boot Actuator dans `application.properties` :

```properties
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=always
```

Accédez aux métriques :
- http://localhost:8080/actuator/health
- http://localhost:8080/actuator/metrics

---

## 📚 Ressources

- [Documentation Docker Compose](https://docs.docker.com/compose/)
- [Spring Boot avec Docker](https://spring.io/guides/gs/spring-boot-docker/)
- [MySQL Docker Hub](https://hub.docker.com/_/mysql)
- [Create React App](https://create-react-app.dev/)

---

**Créé le** : 7 novembre 2024  
**Version Docker Compose** : 3.8  
**Services** : 4 (MySQL, Backend, Frontend, PhpMyAdmin)
