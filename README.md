# 🧠 TTPC-GES

TTPC-GES est une application Java pour la gestion des projets du club TTPC. Elle fonctionne sous Windows (actuellement) et utilise Java 17 + Maven.

---

## 🚀 Fonctionnalités

- Interface graphique Java
- Accès à une base de données (SQLite ou autre via JDBC)
- Gestion de projets (CRUD)
- Architecture propre et modulaire

---

## 🧱 Structure du projet

```bash
TTPC-GES/
├── pom.xml
├── src/
│   ├── main/java/com/ttpc/ges/    # Code Java principal
│   └── main/resources/            # Ressources (images, configs)
│   └── test/java/com/ttpc/ges/    # Tests unitaires
└── target/                        # Généré automatiquement au build
```

---

## Lancement 
 - Build
 -- mvn clean package
 - Launch
 -- java -jar target/ttpc-ges-1.0.0-jar-with-dependencies.jar

 ---

## 📌 Stratégie de développement

 - 🔀 Branches
main : stable (prod)

dev : branche de développement

feature/xxx : nouvelle fonctionnalité

bugfix/xxx : correction de bug

- 🧾 Convention de commit
Utilise les conventional commits :

Tag	Description
feat:	nouvelle fonctionnalité
fix:	correction de bug
docs:	mise à jour de la documentation
refactor:	refonte interne du code
test:	ajout/modif de tests
build:	build system / dépendances
chore:	tâches de maintenance
Exemples :
git commit -m "feat: ajout du module de création de projet"
git commit -m "fix: gestion des accents en UTF-8"

- 🧮 Versioning
Suivi via Semantic Versioning : MAJOR.MINOR.PATCH

MAJOR = changements incompatibles

MINOR = nouvelles features compatibles

PATCH = corrections sans rupture