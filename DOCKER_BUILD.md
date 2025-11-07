# 🏗️ Build Multi-Stage Docker

## ✨ Nouveau Dockerfile Optimisé

Le Dockerfile a été mis à jour avec une approche **multi-stage** qui offre plusieurs avantages :

### 🎯 Avantages

| Avant | Après |
|-------|-------|
| ❌ Build local requis avec Maven | ✅ Build automatique dans Docker |
| ❌ Besoin de JAVA_HOME configuré | ✅ Pas besoin de Java localement |
| ❌ Image finale volumineuse (JDK) | ✅ Image légère (JRE uniquement) |
| ❌ Dépendances locales nécessaires | ✅ Environnement isolé et reproductible |

### 📦 Architecture du Build

```dockerfile
# Stage 1: BUILD (JDK + Maven)
FROM eclipse-temurin:17-jdk-alpine
├── Copie du code source
├── Exécution de Maven (./mvnw clean package)
└── Génération du JAR

# Stage 2: RUNTIME (JRE seulement)
FROM eclipse-temurin:17-jre-alpine
├── Copie uniquement du JAR depuis Stage 1
└── Image finale légère (~200MB vs ~500MB)
```

## 🚀 Utilisation

### Option 1 : Docker Compose (Recommandé)

```powershell
# Plus besoin de build Maven local !
docker-compose up -d --build
```

### Option 2 : Docker direct

```powershell
# Build de l'image
docker build -t backend-app .

# Run du conteneur
docker run -p 8080:8080 --name backend backend-app
```

### Option 3 : Script automatique

```powershell
# Le script gère tout automatiquement
.\start-docker.ps1
```

## 🔍 Détails Techniques

### Stage 1: Build (JDK)
- **Image** : `eclipse-temurin:17-jdk-alpine` (~350MB)
- **Rôle** : Compiler le code Java et créer le JAR
- **Outils** : JDK 17 + Maven Wrapper (ou Maven)
- **Output** : `target/*.jar`

### Stage 2: Runtime (JRE)
- **Image** : `eclipse-temurin:17-jre-alpine` (~180MB)
- **Rôle** : Exécuter uniquement le JAR
- **Input** : JAR copié depuis Stage 1
- **Profil** : `docker` (automatique)

### Comparaison des tailles

```
Ancien Dockerfile (sans multi-stage):
└── Image finale: ~500MB (JDK inclus)

Nouveau Dockerfile (multi-stage):
├── Stage Build: ~350MB (temporaire, supprimé)
└── Image finale: ~200MB (JRE + JAR uniquement)
```

## ⚙️ Configuration

### Variables d'environnement Docker

Le backend utilise automatiquement le profil `docker` qui configure :

```properties
spring.datasource.url=jdbc:mysql://mysql:3306/pidevdb
spring.profiles.active=docker
```

Voir `src/main/resources/application-docker.properties` pour tous les détails.

## 🛠️ Commandes Avancées

### Build avec cache

```powershell
# Build normal (utilise le cache)
docker-compose build

# Build sans cache (complet)
docker-compose build --no-cache
```

### Build uniquement le backend

```powershell
# Build et redémarrer
docker-compose up -d --build backend

# Build sans démarrer
docker-compose build backend
```

### Voir les logs du build

```powershell
# Build avec logs détaillés
docker-compose build --progress=plain backend
```

## 📊 Workflow de Développement

### Scénario 1 : Modification du code Java

```powershell
# 1. Modifier vos fichiers .java
# 2. Rebuild le backend
docker-compose up -d --build backend

# 3. Vérifier les logs
docker-compose logs -f backend
```

### Scénario 2 : Modification de la configuration

```powershell
# 1. Modifier application-docker.properties
# 2. Rebuild
docker-compose up -d --build backend
```

### Scénario 3 : Modification des dépendances (pom.xml)

```powershell
# 1. Modifier pom.xml
# 2. Build complet sans cache
docker-compose build --no-cache backend
docker-compose up -d backend
```

## 🐛 Dépannage

### Le build échoue dans Docker

```powershell
# Voir les logs de build détaillés
docker-compose build --progress=plain backend

# Vérifier les fichiers copiés
docker-compose build --progress=plain --no-cache backend
```

### Erreur "mvnw: Permission denied"

Le Dockerfile gère automatiquement les permissions. Si le problème persiste :

```powershell
# Sur Linux/Mac, donner les permissions d'exécution
git update-index --chmod=+x mvnw

# Puis rebuild
docker-compose build --no-cache backend
```

### Build très lent

Le premier build prend du temps (téléchargement des dépendances Maven). Les builds suivants utilisent le cache Docker et sont beaucoup plus rapides.

```powershell
# Pour accélérer, utilisez un volume Maven (optionnel)
# Ajoutez dans docker-compose.yml sous backend:
volumes:
  - ~/.m2:/root/.m2
```

## 🎯 Avantages du Multi-Stage

### 1. **Sécurité**
- Pas d'outils de build dans l'image finale
- Surface d'attaque réduite
- Image de production minimale

### 2. **Performance**
- Image finale 60% plus petite
- Déploiement plus rapide
- Moins de bande passante

### 3. **Reproductibilité**
- Build identique sur toutes les machines
- Pas de "ça marche sur mon PC"
- Environnement de build isolé

### 4. **Simplicité**
- Plus besoin de Maven local
- Plus besoin de JAVA_HOME
- Une seule commande : `docker-compose up`

## 📝 Comparaison Complète

### Ancien Workflow
```powershell
1. Installer Java JDK 17
2. Configurer JAVA_HOME
3. Installer Maven ou configurer mvnw
4. mvnw clean package -DskipTests
5. docker-compose up -d
```

### Nouveau Workflow
```powershell
1. docker-compose up -d --build
```

## 🚀 CI/CD avec Jenkins

Le Dockerfile multi-stage s'intègre parfaitement avec Jenkins :

```groovy
stage('Docker Build') {
    steps {
        script {
            docker.build("backend:${env.BUILD_NUMBER}")
        }
    }
}
```

Jenkins n'a plus besoin de Maven installé, Docker gère tout !

## 📚 Références

- [Docker Multi-Stage Builds](https://docs.docker.com/build/building/multi-stage/)
- [Spring Boot Docker Best Practices](https://spring.io/guides/topicals/spring-boot-docker/)
- [Eclipse Temurin Images](https://hub.docker.com/_/eclipse-temurin)

---

**Mise à jour** : 7 novembre 2024  
**Type** : Multi-Stage Build  
**Réduction de taille** : ~60% (500MB → 200MB)  
**Avantage principal** : Build automatique sans dépendances locales
