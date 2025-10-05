package com.bank.ui;

import com.bank.service.*;

import java.util.Scanner;

public class MenuPrincipal {
    private final Scanner scanner;
    private final ClientService clientService;
    private final CarteService cardService;
    private final OperationService operationService;
    private final FraudeService fraudService;
    private final RapportService reportService;

    private final MenuClient menuClient;
    private final MenuCarte menuCard;
    private final MenuOperation menuOperation;
    private final MenuFraude menuFraud;
    private final MenuRapport menuReport;

    public MenuPrincipal() {
        this.scanner = new Scanner(System.in);
        this.clientService = new ClientService();
        this.cardService = new CarteService();
        this.operationService = new OperationService();
        this.fraudService = new FraudeService();
        this.reportService = new RapportService();

        this.menuClient = new MenuClient(scanner, clientService);
        this.menuCard = new MenuCarte(scanner, cardService, clientService);
        this.menuOperation = new MenuOperation(scanner, operationService, cardService);
        this.menuFraud = new MenuFraude(scanner, fraudService);
        this.menuReport = new MenuRapport(scanner, reportService);
    }

    public void start() {
        System.out.println("=================================================");
        System.out.println("    BANK CARD MANAGEMENT SYSTEM");
        System.out.println("=================================================\n");

        boolean continueRunning = true;

        while (continueRunning) {
            displayMainMenu();
            int choice = readChoice();

            switch (choice) {
                case 1 -> menuClient.display();
                case 2 -> menuCard.display();
                case 3 -> menuOperation.display();
                case 4 -> menuFraud.display();
                case 5 -> menuReport.display();
                case 0 -> {
                    System.out.println("\nThank you for using the system. Goodbye!");
                    continueRunning = false;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }

        scanner.close();
    }

    private void displayMainMenu() {
        System.out.println("\n========== MAIN MENU ==========");
        System.out.println("1. Client Management");
        System.out.println("2. Card Management");
        System.out.println("3. Operation Management");
        System.out.println("4. Fraud Detection");
        System.out.println("5. Reports and Statistics");
        System.out.println("0. Exit");
        System.out.println("====================================");
        System.out.print("Your choice: ");
    }

    private int readChoice() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static void main(String[] args) {
        MenuPrincipal menu = new MenuPrincipal();
        menu.start();
    }
}
