# TTPC-GES

Application de gestion d'animaux (TENDRES TRUFFES) en Java.

## 🛠 Structure du projet

```
TTPC-GES/
├── src/                        # Code source Java (organisé par packages)
│   ├── Main.java
│   ├── model/
│   └── view/
├── resources/                 # Splashscreen, icônes, fichiers externes
│   ├── logo_TENDRES_TRUFFES.png
│   └── logo_TENDRES_TRUFFES.ico
├── lib/                       # Librairie JDBC
│   └── sqlite-jdbc-<version>.jar
├── manifest/                  # Fichier MANIFEST.MF pour le splash et la classe principale
│   └── MANIFEST.MF
├── build_fat_jar_recursive.bat # Script Windows pour compiler et générer le .jar autonome
```

## ▶️ Compilation

1. Vérifier que `JAVA_HOME` est défini et accessible depuis `cmd`
2. Double-cliquer sur `build_fat_jar_recursive.bat` ou exécuter en ligne de commande

Le script va :
- Compiler le code source
- Copier les ressources
- Décompresser la lib JDBC
- Créer `TTPC-GES-fat.jar` autonome avec splash intégré

## ✅ Utilisation

Lancer l'application :
```bash
java -jar TTPC-GES-fat.jar
```

## 📦 Distribution

- L'application fonctionne sans installation de Java sur les postes cibles si un JRE est ajouté
- Le splashscreen est affiché automatiquement au démarrage
