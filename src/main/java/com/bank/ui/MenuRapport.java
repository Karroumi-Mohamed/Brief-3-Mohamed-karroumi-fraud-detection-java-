package com.bank.ui;

import com.bank.entity.Carte;
import com.bank.entity.TypeOperation;
import com.bank.service.RapportService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class MenuRapport {
    private final Scanner scanner;
    private final RapportService rapportService;

    public MenuRapport(Scanner scanner, RapportService rapportService) {
        this.scanner = scanner;
        this.rapportService = rapportService;
    }

    public void display() {
        boolean back = false;

        while (!back) {
            displayMenu();
            int choice = readChoice();

            switch (choice) {
                case 1 -> displayTop5Cards();
                case 2 -> displayMonthlyStatistics();
                case 3 -> displayBlockedCards();
                case 4 -> displaySuspendedCards();
                case 5 -> displaySuspiciousCards();
                case 6 -> displayCompleteReport();
                case 0 -> back = true;
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void displayMenu() {
        System.out.println("\n===== REPORTS AND STATISTICS =====");
        System.out.println("1. Top 5 most used cards");
        System.out.println("2. Monthly statistics");
        System.out.println("3. Blocked cards");
        System.out.println("4. Suspended cards");
        System.out.println("5. Suspicious cards");
        System.out.println("6. Complete report");
        System.out.println("0. Back");
        System.out.println("==================================");
        System.out.print("Your choice: ");
    }

    private void displayTop5Cards() {
        try {
            List<Map.Entry<Integer, Long>> top5 = rapportService.obtenirTop5CartesUtilisees();
            if (top5.isEmpty()) {
                System.out.println("\nNo data available.");
            } else {
                System.out.println("\nTOP 5 MOST USED CARDS");
                System.out.println("-".repeat(40));
                for (int i = 0; i < top5.size(); i++) {
                    Map.Entry<Integer, Long> entry = top5.get(i);
                    System.out.printf("%d. Card ID %d: %d operation(s)\n",
                        i + 1, entry.getKey(), entry.getValue());
                }
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
    }

    private void displayMonthlyStatistics() {
        try {
            System.out.print("\nMonth (format MM/YYYY, ex: 01/2025): ");
            String monthStr = scanner.nextLine();
            String[] parts = monthStr.split("/");
            int month = Integer.parseInt(parts[0]);
            int year = Integer.parseInt(parts[1]);

            YearMonth yearMonth = YearMonth.of(year, month);

            Map<TypeOperation, BigDecimal> amounts = rapportService.obtenirStatistiquesMensuelles(yearMonth);
            Map<TypeOperation, Long> counts = rapportService.obtenirNombreOperationsMensuelles(yearMonth);

            if (amounts.isEmpty()) {
                System.out.println("\nNo operation for this month.");
            } else {
                System.out.println("\nSTATISTICS FOR " + yearMonth.format(DateTimeFormatter.ofPattern("MM/yyyy")));
                System.out.println("-".repeat(60));
                System.out.println(String.format("%-20s | %-15s | %-15s", "Type", "Count", "Total Amount"));
                System.out.println("-".repeat(60));

                BigDecimal totalAmount = BigDecimal.ZERO;
                long totalCount = 0;

                for (TypeOperation type : TypeOperation.values()) {
                    BigDecimal amount = amounts.getOrDefault(type, BigDecimal.ZERO);
                    Long count = counts.getOrDefault(type, 0L);
                    totalAmount = totalAmount.add(amount);
                    totalCount += count;

                    System.out.printf("%-20s | %15d | %15.2f €\n", type, count, amount);
                }

                System.out.println("-".repeat(60));
                System.out.printf("%-20s | %15d | %15.2f €\n", "TOTAL", totalCount, totalAmount);
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Invalid format. Use MM/YYYY");
        }
    }

    private void displayBlockedCards() {
        try {
            List<Carte> cartes = rapportService.obtenirCartesBloquees();
            if (cartes.isEmpty()) {
                System.out.println("\nNo blocked card.");
            } else {
                System.out.println("\nBLOCKED CARDS");
                System.out.println("-".repeat(60));
                displayCardList(cartes);
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
    }

    private void displaySuspendedCards() {
        try {
            List<Carte> cartes = rapportService.obtenirCartesSuspendues();
            if (cartes.isEmpty()) {
                System.out.println("\nNo suspended card.");
            } else {
                System.out.println("\nSUSPENDED CARDS");
                System.out.println("-".repeat(60));
                displayCardList(cartes);
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
    }

    private void displaySuspiciousCards() {
        try {
            List<Carte> cartes = rapportService.obtenirCartesSuspectes();
            if (cartes.isEmpty()) {
                System.out.println("\nNo suspicious card.");
            } else {
                System.out.println("\nSUSPICIOUS CARDS (Blocked or Suspended)");
                System.out.println("-".repeat(60));
                displayCardList(cartes);
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
    }

    private void displayCompleteReport() {
        try {
            String rapport = rapportService.genererRapportComplet();
            System.out.println("\n" + rapport);
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
    }

    private void displayCardList(List<Carte> cartes) {
        System.out.println(String.format("%-5s | %-16s | %-10s | %-12s | %-10s",
            "ID", "Number", "Type", "Status", "Client ID"));
        System.out.println("-".repeat(60));
        for (Carte carte : cartes) {
            System.out.printf("%-5d | %-16s | %-10s | %-12s | %-10d\n",
                carte.getId(),
                carte.getNumero(),
                carte.getTypeCarte(),
                carte.getStatut(),
                carte.getIdClient());
        }
        System.out.println("\nTotal: " + cartes.size() + " card(s)");
    }

    private int readChoice() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
