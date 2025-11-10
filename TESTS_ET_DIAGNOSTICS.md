# Tests et Diagnostics - Demandes d'Intervention

## 📊 Résumé des modifications

### ✅ Tests ajoutés (58 tests au total)

#### **DemandeInterventionServiceTest.java** (27 tests)
- Tests de récupération (getById, getAll, getByTechnicien)
- Tests d'affectation (technicien, testeur)
- Tests de confirmation d'intervention
- Tests de mise à jour et suppression
- Tests de gestion des erreurs et exceptions
- Tests avec valeurs nulles

#### **DemandeInterventionControllerTest.java** (31 tests)
- Tests des endpoints GET (technicien, récupération)
- Tests de création (CURATIVE, CORRECTIVE, PREVENTIVE)
- Tests d'affectation via API
- Tests de confirmation via API
- Tests des bons de travail
- Tests de gestion d'erreurs (404, 500)

## 🔧 Améliorations du code

### **DemandeInterventionService.java**
- ✅ Ajout de logs SLF4J détaillés
- ✅ Méthodes fallback JPA si les requêtes natives échouent
- ✅ Diagnostic automatique des problèmes
- ✅ Gestion robuste des exceptions

**Méthodes avec fallback :**
- `getDemandeById()` → `getFallbackDemandeById()`
- `getAllDemandes()` → `getFallbackAllDemandes()`
- `getDemandesByTechnicien()` → `getFallbackDemandesByTechnicien()`

### **DemandeInterventionController.java**
- ✅ Ajout de logs pour tracer les requêtes
- ✅ Logging du nombre de résultats retournés
- ✅ Meilleure gestion des erreurs

## 🐛 Diagnostic des problèmes GET

### Symptôme
Les méthodes GET ne retournent pas de données.

### Solutions implémentées

#### 1. **Logs détaillés**
Chaque requête GET affiche maintenant :
```
INFO: Requête GET /demandes/all
INFO: Nombre total de résultats: X
INFO: Nombre de DTOs mappés: Y
```

#### 2. **Méthodes Fallback**
Si les requêtes natives échouent, le système utilise automatiquement JPA standard :
```java
// Si la requête native échoue
repository.findAllWithNullSafeDates() → vide ou erreur
// Alors fallback automatique
repository.findAll() + mapping manuel
```

#### 3. **Vérification des requêtes natives**
Les requêtes dans `DemandeInterventionRepository.java` gèrent :
- Les dates nulles (`0000-00-00 00:00:00`)
- Les champs optionnels (testeur, technicien)
- Les valeurs COALESCE pour confirmation

## 🧪 Comment tester

### 1. Lancer l'application
```bash
mvn spring-boot:run
```

### 2. Consulter les logs
Regardez la console pour voir :
```
INFO DemandeInterventionService - Récupération de toutes les demandes
INFO DemandeInterventionService - Nombre total de résultats: 5
INFO DemandeInterventionController - Requête GET /demandes/all
INFO DemandeInterventionController - Nombre de demandes retournées: 5
```

### 3. Tester les endpoints

#### GET - Récupérer toutes les demandes
```bash
curl http://localhost:8080/demandes/all
```

#### GET - Récupérer une demande par ID
```bash
curl http://localhost:8080/demandes/recuperer/1
```

#### GET - Récupérer les demandes d'un technicien
```bash
curl http://localhost:8080/demandes/technicien/2
```

#### GET - Récupérer les bons de travail d'une intervention
```bash
curl http://localhost:8080/demandes/1/bons-travail
```

### 4. Lancer les tests unitaires
```bash
# Tous les tests
mvn test

# Tests du service uniquement
mvn test -Dtest=DemandeInterventionServiceTest

# Tests du controller uniquement
mvn test -Dtest=DemandeInterventionControllerTest
```

## 🔍 Vérifications à faire

### Si les GET ne fonctionnent toujours pas :

#### 1. Vérifier la base de données
```sql
-- Vérifier que la table existe
SHOW TABLES LIKE 'demande_intervention';

-- Vérifier qu'il y a des données
SELECT COUNT(*) FROM demande_intervention;

-- Vérifier la structure
DESCRIBE demande_intervention;
```

#### 2. Vérifier les logs
Recherchez dans les logs :
- `WARN: Aucun résultat de la requête native. Utilisation du fallback JPA.`
- `ERROR: Erreur lors de la récupération...`

#### 3. Vérifier la configuration
Dans `application.properties` ou `application.yml` :
```properties
# Logs SQL
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE

# Logs de l'application
logging.level.tn.esprit.PI=DEBUG
```

## 📝 Problèmes connus et solutions

### Problème 1 : Requêtes natives ne retournent rien
**Solution** : Les méthodes fallback JPA sont automatiquement utilisées

### Problème 2 : Dates au format '0000-00-00'
**Solution** : Les requêtes natives utilisent CASE WHEN pour convertir en NULL

### Problème 3 : Champs relationnels (testeur, technicien) NULL
**Solution** : Vérification avec COALESCE et gestion des valeurs nulles dans le mapping

## 🎯 Couverture des tests

### Service (DemandeInterventionService)
- ✅ Lecture : getById, getAll, getByTechnicien
- ✅ Affectations : assignTechnicien, assignTesteur
- ✅ Confirmation : confirmerIntervention
- ✅ Modification : updateDemande
- ✅ Suppression : deleteDemande
- ✅ Gestion erreurs : cas limites, exceptions, valeurs nulles

### Controller (DemandeInterventionController)
- ✅ GET : tous les endpoints de récupération
- ✅ POST : création (CURATIVE, CORRECTIVE, PREVENTIVE)
- ✅ PUT : mise à jour, affectations, confirmation
- ✅ Bons de travail : création et récupération
- ✅ Gestion erreurs : 404, 500, validation

## 🚀 Prochaines étapes

1. **Lancer l'application** et vérifier les logs
2. **Tester les endpoints** avec curl ou Postman
3. **Analyser les logs** pour identifier le problème exact
4. **Corriger la configuration** si nécessaire (base de données, hibernate)
5. **Relancer les tests** pour valider les corrections

## 📞 Support

Si le problème persiste après ces modifications :
1. Vérifiez que la base de données contient des données
2. Consultez les logs complets de l'application
3. Vérifiez la configuration Hibernate/JPA
4. Testez les requêtes SQL directement dans la base de données
