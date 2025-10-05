package com.bank.ui;

import com.bank.entity.AlerteFraude;
import com.bank.entity.NiveauAlerte;
import com.bank.service.FraudeService;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class MenuFraude {
    private final Scanner scanner;
    private final FraudeService fraudeService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public MenuFraude(Scanner scanner, FraudeService fraudeService) {
        this.scanner = scanner;
        this.fraudeService = fraudeService;
    }

    public void display() {
        boolean back = false;

        while (!back) {
            displayMenu();
            int choice = readChoice();

            switch (choice) {
                case 1 -> launchFraudAnalysis();
                case 2 -> displayCardAlerts();
                case 3 -> displayAllAlerts();
                case 4 -> displayCriticalAlerts();
                case 5 -> displayAlertsByLevel();
                case 0 -> back = true;
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void displayMenu() {
        System.out.println("\n===== FRAUD DETECTION =====");
        System.out.println("1. Launch fraud analysis");
        System.out.println("2. Display card alerts");
        System.out.println("3. Display all alerts");
        System.out.println("4. Display critical alerts");
        System.out.println("5. Filter by alert level");
        System.out.println("0. Back");
        System.out.println("===========================");
        System.out.print("Your choice: ");
    }

    private void launchFraudAnalysis() {
        try {
            System.out.print("\nCard ID to analyze: ");
            int idCarte = Integer.parseInt(scanner.nextLine());

            System.out.println("Analysis in progress...");
            fraudeService.detecterFraudes(idCarte);
            System.out.println("Analysis completed! Check alerts if anomalies have been detected.");
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
        }
    }

    private void displayCardAlerts() {
        try {
            System.out.print("\nCard ID: ");
            int idCarte = Integer.parseInt(scanner.nextLine());

            List<AlerteFraude> alertes = fraudeService.obtenirAlertesCarte(idCarte);
            if (alertes.isEmpty()) {
                System.out.println("\nNo alert for this card.");
            } else {
                System.out.println("\n--- Card Alerts ---");
                displayAlertList(alertes);
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
        }
    }

    private void displayAllAlerts() {
        try {
            List<AlerteFraude> alertes = fraudeService.obtenirToutesLesAlertes();
            if (alertes.isEmpty()) {
                System.out.println("\nNo alert found.");
            } else {
                System.out.println("\n--- All Alerts ---");
                displayAlertList(alertes);
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
    }

    private void displayCriticalAlerts() {
        try {
            List<AlerteFraude> alertes = fraudeService.obtenirAlertesCritiques();
            if (alertes.isEmpty()) {
                System.out.println("\nNo critical alert found.");
            } else {
                System.out.println("\nCRITICAL ALERTS");
                displayAlertList(alertes);
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
    }

    private void displayAlertsByLevel() {
        try {
            System.out.println("\nAlert level:");
            System.out.println("1. INFO");
            System.out.println("2. WARNING");
            System.out.println("3. CRITICAL");
            System.out.print("Choice: ");
            int levelChoice = Integer.parseInt(scanner.nextLine());

            NiveauAlerte niveau = switch (levelChoice) {
                case 1 -> NiveauAlerte.INFO;
                case 2 -> NiveauAlerte.AVERTISSEMENT;
                case 3 -> NiveauAlerte.CRITIQUE;
                default -> throw new IllegalArgumentException("Invalid level");
            };

            List<AlerteFraude> alertes = fraudeService.obtenirAlertesParNiveau(niveau);
            if (alertes.isEmpty()) {
                System.out.println("\nNo alert of level " + niveau + " found.");
            } else {
                System.out.println("\n--- Alerts of level " + niveau + " ---");
                displayAlertList(alertes);
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void displayAlertList(List<AlerteFraude> alertes) {
        System.out.println(String.format("%-5s | %-17s | %-15s | %-10s | %s",
            "ID", "Date", "Level", "Card ID", "Description"));
        System.out.println("-".repeat(100));

        for (AlerteFraude alerte : alertes) {
            String icone = switch (alerte.niveau()) {
                case INFO -> "ℹ️";
                case AVERTISSEMENT -> "⚠️";
                case CRITIQUE -> "🚨";
            };

            System.out.println(String.format("%-5d | %-17s | %-15s | %-10d | %s %s",
                alerte.id(),
                alerte.dateCreation().format(formatter),
                alerte.niveau(),
                alerte.idCarte(),
                icone,
                alerte.description()));
        }
        System.out.println("\nTotal: " + alertes.size() + " alert(s)");
    }

    private int readChoice() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
