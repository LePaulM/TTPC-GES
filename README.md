# TTPC-GES

**TTPC-GES** est une application de gestion destinée à la pension féline et canine **Tendres Truffes**.  
Elle permet de suivre les animaux accueillis, leurs mouvements d'entrée et de sortie, ainsi que d'importer/exporter les données au format CSV.

---

## 🐾 Fonctionnalités principales

- Ajout, modification et suppression d’animaux
- Suivi des mouvements (entrée / sortie / décès)
- Recherche rapide dans les tableaux
- Import/export des données en CSV
- Interface graphique claire en Java Swing
- Champs de saisie intelligents (date, combobox, champs restreints)

---

## 🖥️ Installation

1. Installer Java 17 ou une version plus réccente : https://www.oracle.com/fr/java/technologies/downloads/
2. Exécuter le fichier `TTPC-GES-Setup.exe` *(installateur Windows généré avec Inno Setup)*
3. Suivre les instructions à l’écran
4. Un raccourci vers **TTPC-GES** sera placé sur le bureau

---

## 🚀 Lancement manuel (développeurs)

Pour lancer l’application sans l’installateur :

```bash
cd TTPC-GES
mvn clean package
java -jar target/ttpc-ges-1.0.0-jar-with-dependencies.jar
