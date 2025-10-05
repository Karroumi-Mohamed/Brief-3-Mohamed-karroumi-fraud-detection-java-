package com.bank.ui;

import com.bank.entity.*;
import com.bank.service.CarteService;
import com.bank.service.ClientService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class MenuCarte {
    private final Scanner scanner;
    private final CarteService carteService;
    private final ClientService clientService;

    public MenuCarte(Scanner scanner, CarteService carteService, ClientService clientService) {
        this.scanner = scanner;
        this.carteService = carteService;
        this.clientService = clientService;
    }

    public void display() {
        boolean back = false;

        while (!back) {
            displayMenu();
            int choice = readChoice();

            switch (choice) {
                case 1 -> issueCard();
                case 2 -> displayCard();
                case 3 -> displayClientCards();
                case 4 -> displayAllCards();
                case 5 -> activateCard();
                case 6 -> suspendCard();
                case 7 -> blockCard();
                case 0 -> back = true;
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void displayMenu() {
        System.out.println("\n===== CARD MANAGEMENT =====");
        System.out.println("1. Issue a card");
        System.out.println("2. Display a card");
        System.out.println("3. Display client cards");
        System.out.println("4. Display all cards");
        System.out.println("5. Activate a card");
        System.out.println("6. Suspend a card");
        System.out.println("7. Block a card");
        System.out.println("0. Back");
        System.out.println("===========================");
        System.out.print("Your choice: ");
    }

    private void issueCard() {
        try {
            System.out.println("\n--- Card Issuance ---");
            System.out.print("Client ID: ");
            int idClient = Integer.parseInt(scanner.nextLine());

            Optional<Client> clientOpt = clientService.obtenirClient(idClient);
            if (clientOpt.isEmpty()) {
                System.out.println("Client not found.");
                return;
            }

            System.out.println("\nCard type:");
            System.out.println("1. Debit Card");
            System.out.println("2. Credit Card");
            System.out.println("3. Prepaid Card");
            System.out.print("Choice: ");
            int cardType = Integer.parseInt(scanner.nextLine());

            switch (cardType) {
                case 1 -> createDebitCard(idClient);
                case 2 -> createCreditCard(idClient);
                case 3 -> createPrepaidCard(idClient);
                default -> System.out.println("Invalid card type.");
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }

    private void createDebitCard(int idClient) throws SQLException {
        System.out.print("Daily limit (EUR): ");
        BigDecimal limit = new BigDecimal(scanner.nextLine());

        CarteDebit carte = carteService.creerCarteDebit(idClient, limit);
        System.out.println("Debit Card created successfully!");
        System.out.println("Number: " + carte.getNumero());
        System.out.println("ID: " + carte.getId());
    }

    private void createCreditCard(int idClient) throws SQLException {
        System.out.print("Monthly limit (EUR): ");
        BigDecimal monthlyLimit = new BigDecimal(scanner.nextLine());
        System.out.print("Interest rate (%): ");
        BigDecimal interestRate = new BigDecimal(scanner.nextLine());

        CarteCredit carte = carteService.creerCarteCredit(idClient, monthlyLimit, interestRate);
        System.out.println("Credit Card created successfully!");
        System.out.println("Number: " + carte.getNumero());
        System.out.println("ID: " + carte.getId());
    }

    private void createPrepaidCard(int idClient) throws SQLException {
        System.out.print("Initial balance (EUR): ");
        BigDecimal balance = new BigDecimal(scanner.nextLine());

        CartePrepayee carte = carteService.creerCartePrepayee(idClient, balance);
        System.out.println("Prepaid Card created successfully!");
        System.out.println("Number: " + carte.getNumero());
        System.out.println("ID: " + carte.getId());
    }

    private void displayCard() {
        try {
            System.out.print("\nCard ID: ");
            int id = Integer.parseInt(scanner.nextLine());

            Optional<Carte> carteOpt = carteService.obtenirCarte(id);
            if (carteOpt.isPresent()) {
                Carte carte = carteOpt.get();
                displayCardDetails(carte);
            } else {
                System.out.println("Card not found.");
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
        }
    }

    private void displayClientCards() {
        try {
            System.out.print("\nClient ID: ");
            int idClient = Integer.parseInt(scanner.nextLine());

            List<Carte> cartes = carteService.obtenirCartesClient(idClient);
            if (cartes.isEmpty()) {
                System.out.println("\nNo card found for this client.");
            } else {
                System.out.println("\n--- Client Cards ---");
                for (Carte carte : cartes) {
                    System.out.printf("ID: %d | Number: %s | Type: %s | Status: %s\n",
                        carte.getId(), carte.getNumero(), carte.getTypeCarte(), carte.getStatut());
                }
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
        }
    }

    private void displayAllCards() {
        try {
            List<Carte> cartes = carteService.obtenirToutesLesCartes();
            if (cartes.isEmpty()) {
                System.out.println("\nNo card found.");
            } else {
                System.out.println("\n--- Card List ---");
                for (Carte carte : cartes) {
                    System.out.printf("ID: %d | Number: %s | Type: %s | Status: %s | Client ID: %d\n",
                        carte.getId(), carte.getNumero(), carte.getTypeCarte(),
                        carte.getStatut(), carte.getIdClient());
                }
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
    }

    private void activateCard() {
        try {
            System.out.print("\nCard ID to activate: ");
            int id = Integer.parseInt(scanner.nextLine());

            if (carteService.activerCarte(id)) {
                System.out.println("Card activated successfully!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }

    private void suspendCard() {
        try {
            System.out.print("\nCard ID to suspend: ");
            int id = Integer.parseInt(scanner.nextLine());

            if (carteService.suspendreCarte(id)) {
                System.out.println("Card suspended successfully!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void blockCard() {
        try {
            System.out.print("\nCard ID to block: ");
            int id = Integer.parseInt(scanner.nextLine());

            if (carteService.bloquerCarte(id)) {
                System.out.println("Card blocked successfully!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void displayCardDetails(Carte carte) {
        System.out.println("\n--- Card Details ---");
        System.out.println("ID: " + carte.getId());
        System.out.println("Number: " + carte.getNumero());
        System.out.println("Type: " + carte.getTypeCarte());
        System.out.println("Status: " + carte.getStatut());
        System.out.println("Expiration date: " + carte.getDateExpiration());
        System.out.println("Client ID: " + carte.getIdClient());

        if (carte instanceof CarteDebit cd) {
            System.out.println("Daily limit: " + cd.getPlafondJournalier() + " EUR");
        } else if (carte instanceof CarteCredit cc) {
            System.out.println("Monthly limit: " + cc.getPlafondMensuel() + " EUR");
            System.out.println("Interest rate: " + cc.getTauxInteret() + "%");
        } else if (carte instanceof CartePrepayee cp) {
            System.out.println("Available balance: " + cp.getSoldeDisponible() + " EUR");
        }
    }

    private int readChoice() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
