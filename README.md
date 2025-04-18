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

## Lancement 
 - Build
 -- mvn clean package
 - Launch
 -- java -jar target/ttpc-ges-1.0.0-jar-with-dependencies.jar

