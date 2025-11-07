# 📁 Scripts d'initialisation de la base de données

Ce dossier contient les scripts SQL qui seront exécutés automatiquement lors de la création du conteneur MySQL.

## 📝 Utilisation

1. Placez vos scripts SQL dans ce dossier
2. Les scripts sont exécutés par ordre alphabétique
3. Nommez vos scripts avec des préfixes numériques pour contrôler l'ordre :
   - `01-schema.sql` - Création de schéma
   - `02-data.sql` - Insertion de données
   - `03-users.sql` - Création d'utilisateurs

## 🔄 Exemple de script

### 01-schema.sql
```sql
-- Création de tables supplémentaires si nécessaire
CREATE TABLE IF NOT EXISTS test_table (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 02-data.sql
```sql
-- Insertion de données de test
INSERT INTO test_table (name) VALUES 
    ('Test 1'),
    ('Test 2'),
    ('Test 3');
```

## ⚠️ Important

- Les scripts sont exécutés **uniquement lors de la première création** du conteneur
- Si vous modifiez les scripts, vous devez recréer le volume MySQL :
  ```powershell
  docker-compose down -v
  docker-compose up -d
  ```

## 🗑️ Réinitialiser la base de données

Pour réinitialiser complètement la base de données :

```powershell
# Arrêter et supprimer le volume
docker-compose down -v

# Redémarrer (les scripts seront réexécutés)
docker-compose up -d
```

## 📚 Documentation

- [MySQL Docker Init Scripts](https://hub.docker.com/_/mysql)
- Les fichiers `.sql`, `.sql.gz`, et `.sh` sont supportés
