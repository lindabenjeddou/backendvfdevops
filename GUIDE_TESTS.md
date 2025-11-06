# Guide des Tests JUnit Mockito - Backend DevOps

## 📋 Tests créés

J'ai créé une suite complète de tests JUnit avec Mockito pour votre projet. Voici ce qui a été ajouté :

### Structure créée

```
src/test/
├── java/tn/esprit/PI/
│   ├── Services/
│   │   ├── ComponentServiceTest.java       ✅ 10 tests unitaires
│   │   └── ProjectServiceTest.java         ✅ 13 tests unitaires
│   ├── repository/
│   │   └── ComponentRpTest.java            ✅ 8 tests d'intégration
│   └── PIApplicationTests.java             ✅ Test de contexte Spring
│
├── resources/
│   └── application-test.properties         ✅ Configuration H2
│
└── README.md                               ✅ Documentation complète
```

## 🎯 Couverture des tests

### ComponentServiceTest (10 tests)
- ✅ `testAddOrIncrement_Success` - Ajout de composant
- ✅ `testAddOrIncrement_ThrowsException_WhenComponentIsNull` - Validation null
- ✅ `testUpdateCompo_Success` - Mise à jour réussie
- ✅ `testUpdateCompo_ReturnsNull_WhenComponentNotFound` - Composant inexistant
- ✅ `testRetrieveComp_Success` - Récupération de tous les composants
- ✅ `testDeleteCompByTRART_ARTICLE_Success` - Suppression réussie
- ✅ `testDeleteCompByTRART_ARTICLE_ReturnsFalse_WhenNotFound` - Suppression échouée
- ✅ `testFindByTrartArticle_Success` - Recherche par ID
- ✅ `testFindByTrartArticle_ThrowsException_WhenNotFound` - ID inexistant
- ✅ `testSearchComponents_Success` - Recherche avec critères

### ProjectServiceTest (13 tests)
- ✅ `testCreateProjetFromDTO_Success` - Création depuis DTO
- ✅ `testCreateProjetFromDTO_WithEmptyComponents` - DTO sans composants
- ✅ `testGetAllProjets_Success` - Liste tous les projets
- ✅ `testGetAllProjets_EmptyList` - Liste vide
- ✅ `testAddComponentToProject_Success` - Ajout composant au projet
- ✅ `testAddComponentToProject_ComponentAlreadyExists` - Composant déjà présent
- ✅ `testAddComponentToProject_ProjectNotFound` - Projet inexistant
- ✅ `testAddComponentToProject_ComponentNotFound` - Composant inexistant
- ✅ `testAddProject_Success` - Ajout projet valide
- ✅ `testAddProject_ThrowsException_WhenProjectNameIsNull` - Nom null
- ✅ `testAddProject_ThrowsException_WhenProjectNameIsEmpty` - Nom vide
- ✅ `testAddProject_ThrowsException_WhenManagerNameIsNull` - Manager null
- ✅ `testAddProject_ThrowsException_WhenManagerNameIsEmpty` - Manager vide

### ComponentRpTest (8 tests d'intégration)
- ✅ `testSaveComponent_Success` - Sauvegarde en base
- ✅ `testFindByTrartArticle_Success` - Recherche JPA
- ✅ `testFindByTrartArticle_NotFound` - Élément introuvable
- ✅ `testFindAll_Success` - FindAll JPA
- ✅ `testDeleteComponent_Success` - Suppression en base
- ✅ `testSearchComponents_WithAllParameters` - Recherche complète
- ✅ `testSearchComponents_WithPartialMatch` - Recherche partielle
- ✅ `testSearchComponents_NoResults` - Aucun résultat
- ✅ `testSearchComponents_WithNullParameters` - Tous les paramètres null

**Total : 31 tests automatisés** 🎉

## 🔧 Prérequis pour exécuter les tests

### 1. Configurer JAVA_HOME
Votre système nécessite la variable d'environnement JAVA_HOME :

**Windows PowerShell (Temporaire) :**
```powershell
$env:JAVA_HOME="C:\Program Files\Java\jdk-17"
```

**Windows (Permanent) :**
1. Panneau de configuration → Système → Paramètres système avancés
2. Variables d'environnement
3. Nouvelle variable système :
   - Nom : `JAVA_HOME`
   - Valeur : `C:\Program Files\Java\jdk-17` (ajustez le chemin)
4. Ajouter à PATH : `%JAVA_HOME%\bin`

### 2. Vérifier Maven
```powershell
.\mvnw.cmd --version
```

## 🚀 Exécution des tests

### Tous les tests
```powershell
.\mvnw.cmd clean test
```

### Un test spécifique
```powershell
.\mvnw.cmd test -Dtest=ComponentServiceTest
.\mvnw.cmd test -Dtest=ProjectServiceTest
.\mvnw.cmd test -Dtest=ComponentRpTest
```

### Avec rapport de couverture Jacoco
```powershell
.\mvnw.cmd clean verify
```
📊 Rapport généré dans : `target/site/jacoco/index.html`

### Tests en mode debug
```powershell
.\mvnw.cmd test -X
```

## 📊 Résultats attendus

Quand les tests s'exécutent correctement, vous devriez voir :

```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running tn.esprit.PI.Services.ComponentServiceTest
[INFO] Tests run: 10, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running tn.esprit.PI.Services.ProjectServiceTest
[INFO] Tests run: 13, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running tn.esprit.PI.repository.ComponentRpTest
[INFO] Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running tn.esprit.PI.PIApplicationTests
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 32, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] BUILD SUCCESS
```

## 🎨 Concepts utilisés

### Tests Unitaires (Mockito)
Les tests unitaires testent une classe isolée en mockant ses dépendances :

```java
@ExtendWith(MockitoExtension.class)
class ComponentServiceTest {
    @Mock
    private ComponentRp componentRp;  // Mock du repository
    
    @InjectMocks
    private ComponentService componentService;  // Classe testée
    
    @Test
    void testAddOrIncrement_Success() {
        // Arrange
        when(componentRp.save(any())).thenReturn(testComponent);
        
        // Act
        Component result = componentService.addOrIncrement(testComponent);
        
        // Assert
        assertNotNull(result);
        verify(componentRp, times(1)).save(testComponent);
    }
}
```

### Tests d'Intégration (@DataJpaTest)
Les tests d'intégration testent avec une vraie base H2 :

```java
@DataJpaTest
class ComponentRpTest {
    @Autowired
    private ComponentRp componentRp;  // Repository réel
    
    @Test
    void testSaveComponent_Success() {
        Component saved = componentRp.save(testComponent);
        assertNotNull(saved.getTrartArticle());
    }
}
```

## 📚 Bonnes pratiques appliquées

✅ **Nommage clair** : `test{Méthode}_{Scénario}`
✅ **Structure AAA** : Arrange-Act-Assert
✅ **Couverture complète** : Cas normaux + cas d'erreur
✅ **Isolation** : Chaque test est indépendant
✅ **Mocks appropriés** : Tests unitaires avec Mockito
✅ **Tests d'intégration** : Validation avec base H2
✅ **Vérifications Mockito** : `verify()` pour les interactions
✅ **Assertions claires** : Messages explicites

## 🔍 Analyse de couverture

Après `mvnw clean verify`, ouvrez le rapport Jacoco :
```
target/site/jacoco/index.html
```

Vous verrez :
- **Line Coverage** : % de lignes exécutées
- **Branch Coverage** : % de branches testées
- **Method Coverage** : % de méthodes couvertes
- **Class Coverage** : % de classes testées

## 🐛 Dépannage

### Erreur : Classes not found
```powershell
.\mvnw.cmd clean compile test-compile
```

### Erreur : H2 database
Vérifier dans `pom.xml` :
```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>test</scope>
</dependency>
```

### Erreur : Mock not working
Vérifier l'annotation :
```java
@ExtendWith(MockitoExtension.class)
```

### Tests trop lents
Les tests utilisent H2 en mémoire, donc ils doivent être rapides.
Si lents, vérifier la configuration réseau/firewall.

## 📝 Configuration Maven (déjà présente)

Votre `pom.xml` contient déjà toutes les dépendances nécessaires :

```xml
<!-- Tests -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>

<!-- Mockito -->
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>

<!-- H2 pour tests -->
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>test</scope>
</dependency>

<!-- Jacoco -->
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.12</version>
</plugin>
```

## 🎓 Ressources d'apprentissage

- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [Spring Testing](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)
- [AssertJ](https://assertj.github.io/doc/)

## ✅ Checklist de validation

- [x] Tests unitaires ComponentService (10 tests)
- [x] Tests unitaires ProjectService (13 tests)
- [x] Tests d'intégration ComponentRp (8 tests)
- [x] Test de contexte Spring (1 test)
- [x] Configuration H2 pour tests
- [x] Documentation complète
- [x] Jacoco configuré
- [ ] JAVA_HOME configuré (à faire)
- [ ] Exécuter les tests
- [ ] Vérifier le rapport de couverture

## 🚀 Prochaines étapes

1. **Configurer JAVA_HOME** (voir section Prérequis)
2. **Exécuter les tests** : `.\mvnw.cmd clean test`
3. **Vérifier le rapport Jacoco** : `.\mvnw.cmd verify` puis ouvrir `target/site/jacoco/index.html`
4. **Ajouter plus de tests** pour d'autres services si nécessaire
5. **Intégrer dans CI/CD** (Jenkins, GitLab CI, GitHub Actions)

---

**Créé le** : 6 novembre 2024
**Technologie** : JUnit 5 + Mockito + Spring Boot Test + H2
**Tests totaux** : 32 tests automatisés
