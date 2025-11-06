# Tests JUnit avec Mockito

Ce projet contient des tests unitaires et d'intégration utilisant JUnit 5 et Mockito.

## Structure des tests

```
src/test/
├── java/
│   └── tn/esprit/PI/
│       ├── Services/
│       │   ├── ComponentServiceTest.java      # Tests unitaires pour ComponentService
│       │   └── ProjectServiceTest.java        # Tests unitaires pour ProjectService
│       ├── repository/
│       │   └── ComponentRpTest.java           # Tests d'intégration pour ComponentRp
│       └── PIApplicationTests.java            # Test de contexte Spring
└── resources/
    └── application-test.properties            # Configuration pour les tests
```

## Technologies utilisées

- **JUnit 5** (Jupiter) - Framework de tests
- **Mockito** - Framework de mock pour les tests unitaires
- **Spring Boot Test** - Support de tests Spring Boot
- **H2 Database** - Base de données en mémoire pour les tests
- **Jacoco** - Couverture de code

## Types de tests

### 1. Tests unitaires (avec Mockito)
- **ComponentServiceTest** : Tests de la logique métier avec mocks des repositories
- **ProjectServiceTest** : Tests de la logique métier avec mocks des dépendances

Les tests unitaires utilisent `@ExtendWith(MockitoExtension.class)` et mockent toutes les dépendances.

### 2. Tests d'intégration
- **ComponentRpTest** : Tests des requêtes JPA avec `@DataJpaTest`
- **PIApplicationTests** : Test du chargement du contexte Spring

Les tests d'intégration utilisent une base H2 en mémoire.

## Exécution des tests

### Exécuter tous les tests
```bash
mvn test
```

### Exécuter un test spécifique
```bash
mvn test -Dtest=ComponentServiceTest
```

### Exécuter avec rapport de couverture
```bash
mvn clean verify
```
Le rapport Jacoco sera généré dans `target/site/jacoco/index.html`

### Exécuter uniquement les tests unitaires
```bash
mvn test -Dgroups=unit
```

### Exécuter uniquement les tests d'intégration
```bash
mvn test -Dgroups=integration
```

## Configuration Maven

Le `pom.xml` contient déjà les dépendances nécessaires :
- `spring-boot-starter-test` (inclut JUnit 5, Mockito, AssertJ)
- `mockito-junit-jupiter`
- `h2` pour les tests
- `jacoco-maven-plugin` pour la couverture

## Bonnes pratiques

### Conventions de nommage
- Classe de test : `{ClasseTestée}Test`
- Méthode de test : `test{Méthode}_{Scénario}`
- Exemple : `testAddOrIncrement_Success()`

### Structure AAA (Arrange-Act-Assert)
```java
@Test
void testAddComponent_Success() {
    // Arrange - Préparation des données
    Component component = new Component();
    when(repository.save(any())).thenReturn(component);
    
    // Act - Exécution de la méthode testée
    Component result = service.addComponent(component);
    
    // Assert - Vérification des résultats
    assertNotNull(result);
    verify(repository, times(1)).save(component);
}
```

### Annotations importantes

#### Tests unitaires
- `@ExtendWith(MockitoExtension.class)` : Active Mockito
- `@Mock` : Crée un mock d'une dépendance
- `@InjectMocks` : Crée l'instance testée avec les mocks injectés
- `@BeforeEach` : Exécuté avant chaque test

#### Tests d'intégration
- `@DataJpaTest` : Configure le contexte JPA avec H2
- `@SpringBootTest` : Charge le contexte Spring complet
- `@Autowired` : Injection de dépendances réelles

## Exemples de vérifications Mockito

### Vérifier qu'une méthode a été appelée
```java
verify(repository).save(component);
verify(repository, times(1)).save(component);
verify(repository, never()).delete(any());
```

### Stubber le comportement d'un mock
```java
when(repository.findById("123")).thenReturn(Optional.of(component));
when(repository.save(any())).thenReturn(component);
doThrow(new RuntimeException()).when(repository).delete(any());
```

### Vérifier les exceptions
```java
assertThrows(IllegalArgumentException.class, () -> {
    service.addComponent(null);
});
```

## Couverture de code

Le plugin Jacoco génère automatiquement un rapport de couverture lors de `mvn verify`.

### Visualiser le rapport
1. Exécuter : `mvn clean verify`
2. Ouvrir : `target/site/jacoco/index.html`

### Objectifs de couverture
- **Line Coverage** : > 80%
- **Branch Coverage** : > 70%
- **Method Coverage** : > 80%

## Configuration H2 pour les tests

La base H2 est configurée dans `application-test.properties` :
```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.jpa.hibernate.ddl-auto=create-drop
```

Cette configuration :
- Crée une base en mémoire pour chaque test
- Supprime automatiquement les données après les tests
- Améliore la vitesse d'exécution

## Dépannage

### Les tests ne trouvent pas les classes
```bash
mvn clean compile test-compile
```

### Erreur de connexion à la base
Vérifier que H2 est dans les dépendances avec scope `test`

### Les mocks ne fonctionnent pas
Vérifier que `@ExtendWith(MockitoExtension.class)` est présent

### Les tests d'intégration échouent
Vérifier `application-test.properties` et que H2 est disponible

## Ressources

- [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [Spring Boot Testing](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)
- [AssertJ Documentation](https://assertj.github.io/doc/)
