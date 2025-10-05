# 🏦 Système de Gestion des Cartes Bancaires et Détection de Fraude

Application Java console pour la gestion des cartes bancaires avec détection automatique de fraude.

## 📋 Description

Ce système permet aux banques de :
- Gérer le cycle de vie des cartes bancaires (création, activation, suspension, blocage)
- Suivre les opérations en temps réel (achats, retraits, paiements en ligne)
- Détecter automatiquement les comportements suspects
- Générer des rapports et statistiques

## 🛠️ Technologies Utilisées

- **Java 17** (record, sealed class, Stream API, Optional)
- **PostgreSQL** (base de données)
- **JDBC** (connexion base de données)
- **Maven** (gestion de dépendances)

## 📁 Architecture

Le projet suit une architecture en couches :

```
┌─────────────────────────────────┐
│       UI Layer (Menu)           │
│   (Interface utilisateur)       │
└─────────────────────────────────┘
              ↓
┌─────────────────────────────────┐
│      Service Layer              │
│   (Logique métier)              │
└─────────────────────────────────┘
              ↓
┌─────────────────────────────────┐
│       DAO Layer                 │
│   (Accès aux données)           │
└─────────────────────────────────┘
              ↓
┌─────────────────────────────────┐
│      Entity Layer               │
│   (Modèles de données)          │
└─────────────────────────────────┘
```

### Packages

- **`com.bank.entity`** : Entités (Client, Carte, OperationCarte, AlerteFraude)
- **`com.bank.dao`** : Accès aux données (CRUD)
- **`com.bank.service`** : Logique métier
- **`com.bank.ui`** : Interface utilisateur (menus console)
- **`com.bank.util`** : Utilitaires (DatabaseConnection)

## 🗄️ Modèle de Données

### Entités Principales

#### Client (record)
- `id` : Identifiant unique
- `nom` : Nom du client
- `email` : Email (unique)
- `telephone` : Numéro de téléphone

#### Carte (sealed class)
- **CarteDebit** : Plafond journalier
- **CarteCredit** : Plafond mensuel + taux d'intérêt
- **CartePrepayee** : Solde disponible

#### OperationCarte (record)
- Types : `ACHAT`, `RETRAIT`, `PAIEMENTENLIGNE`

#### AlerteFraude (record)
- Niveaux : `INFO`, `AVERTISSEMENT`, `CRITIQUE`

### Relations
- 1 Client → N Cartes
- 1 Carte → N Opérations
- 1 Carte → N Alertes

## 🚀 Installation

### Prérequis
- Java 17 ou supérieur
- PostgreSQL
- Maven

### Configuration de la base de données

1. Créer la base de données :
```bash
psql -U postgres -f database.sql
```

2. Modifier les identifiants dans `DatabaseConnection.java` si nécessaire :
```java
private static final String URL = "jdbc:postgresql://localhost:5432/bank_card_management";
private static final String USER = "postgres";
private static final String PASSWORD = "postgres";
```

### Compilation

```bash
mvn clean package
```

### Exécution

```bash
java -jar target/brief3-1.0-SNAPSHOT.jar
```

## 📖 Utilisation

### Menu Principal

```
========== MENU PRINCIPAL ==========
1. Gestion des Clients
2. Gestion des Cartes
3. Gestion des Opérations
4. Détection de Fraude
5. Rapports et Statistiques
0. Quitter
====================================
```

### Fonctionnalités

#### 1. Gestion des Clients
- Créer un nouveau client
- Rechercher par email/téléphone
- Modifier/Supprimer un client

#### 2. Gestion des Cartes
- Émettre une carte (Débit/Crédit/Prépayée)
- Activer/Suspendre/Bloquer une carte
- Consulter les cartes d'un client

#### 3. Gestion des Opérations
- Effectuer une opération (avec vérification de plafond)
- Consulter l'historique d'une carte
- Filtrer par type d'opération

#### 4. Détection de Fraude
- Analyser automatiquement les opérations suspectes
- Détecter :
  - Montants élevés (> 5000 EUR)
  - Opérations rapprochées dans des lieux différents
  - Tentatives multiples
- Bloquer automatiquement les cartes suspectes

#### 5. Rapports et Statistiques
- Top 5 cartes les plus utilisées
- Statistiques mensuelles par type d'opération
- Liste des cartes bloquées/suspendues

## 🔍 Détection de Fraude

Le système détecte automatiquement :

### 1. Montants Suspects
- Seuil : Opérations > 5000 EUR
- Action : Alerte `AVERTISSEMENT`

### 2. Opérations Rapprochées
- Critère : 2 opérations dans des lieux différents en moins de 30 minutes
- Action : Alerte `CRITIQUE` + **Blocage automatique**

### 3. Tentatives Multiples
- Critère : 5+ opérations en moins d'1 heure
- Action : Alerte `CRITIQUE` + **Suspension automatique**

## 📊 Diagramme de Classes

Voir le fichier `class-diagram.puml` pour le diagramme complet.

Pour visualiser :
```bash
plantuml class-diagram.puml
```
Ou utiliser : http://www.plantuml.com/plantuml/uml/

## 🔑 Fonctionnalités Java 17

- **Record** : Classes immuables (Client, OperationCarte, AlerteFraude)
- **Sealed Class** : Hiérarchie fermée (Carte avec 3 sous-types)
- **Stream API** : Traitement des collections
- **Optional** : Gestion des valeurs nulles
- **Pattern Matching** : Switch expressions

## 📝 Structure du Projet

```
brief3/
├── src/
│   └── main/
│       └── java/
│           └── com/bank/
│               ├── entity/          # Entités et enums
│               ├── dao/             # Accès aux données
│               ├── service/         # Logique métier
│               ├── ui/              # Menus console
│               ├── util/            # Utilitaires
│               └── Main.java        # Point d'entrée
├── database.sql                     # Script création DB
├── class-diagram.puml              # Diagramme de classes
├── pom.xml                         # Configuration Maven
└── README.md                       # Documentation
```

## 🧪 Exemples d'Utilisation

### Créer un client
```
Nom: Jean Dupont
Email: jean.dupont@email.com
Téléphone: 0612345678
```

### Émettre une carte crédit
```
ID du client: 1
Type de carte: 2 (Crédit)
Plafond mensuel: 5000
Taux d'intérêt: 1.5
```

### Effectuer un achat
```
ID de la carte: 1
Type d'opération: 1 (Achat)
Montant: 150.50
Lieu: Paris
```

### Analyser les fraudes
```
ID de la carte à analyser: 1
🔍 Analyse en cours...
✅ Analyse terminée!
```

## 🐛 Gestion des Erreurs

- **SQLException** : Erreurs base de données
- **IllegalArgumentException** : Validation des données
- **IllegalStateException** : États invalides (ex: activer une carte déjà active)
- **NumberFormatException** : Entrées utilisateur invalides

## 👨‍💻 Auteur

Projet développé dans le cadre de la formation Java 17 - Gestion des cartes bancaires et détection de fraude.

## 📅 Date

Septembre 2025

## 📄 Licence

Ce projet est un travail académique à des fins pédagogiques.
