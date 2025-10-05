CREATE DATABASE bank_card_management;

\c bank_card_management;

CREATE TYPE statut_carte AS ENUM ('ACTIVE', 'SUSPENDUE', 'BLOQUEE');
CREATE TYPE type_carte AS ENUM ('DEBIT', 'CREDIT', 'PREPAYEE');
CREATE TYPE type_operation AS ENUM ('ACHAT', 'RETRAIT', 'PAIEMENTENLIGNE');
CREATE TYPE niveau_alerte AS ENUM ('INFO', 'AVERTISSEMENT', 'CRITIQUE');

CREATE TABLE Client (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    telephone VARCHAR(20)
);

CREATE TABLE Carte (
    id SERIAL PRIMARY KEY,
    numero VARCHAR(16) UNIQUE NOT NULL,
    dateExpiration DATE NOT NULL,
    statut statut_carte DEFAULT 'ACTIVE',
    typeCarte type_carte NOT NULL,
    idClient INT NOT NULL,
    plafondJournalier DECIMAL(10,2),
    plafondMensuel DECIMAL(10,2),
    tauxInteret DECIMAL(5,2),
    soldeDisponible DECIMAL(10,2),
    FOREIGN KEY (idClient) REFERENCES Client(id) ON DELETE CASCADE
);

CREATE TABLE OperationCarte (
    id SERIAL PRIMARY KEY,
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    montant DECIMAL(10,2) NOT NULL,
    type type_operation NOT NULL,
    lieu VARCHAR(100),
    idCarte INT NOT NULL,
    FOREIGN KEY (idCarte) REFERENCES Carte(id) ON DELETE CASCADE
);

CREATE TABLE AlerteFraude (
    id SERIAL PRIMARY KEY,
    description TEXT NOT NULL,
    niveau niveau_alerte NOT NULL,
    idCarte INT NOT NULL,
    dateCreation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (idCarte) REFERENCES Carte(id) ON DELETE CASCADE
);