package com.bank.ui;

import com.bank.entity.Carte;
import com.bank.entity.OperationCarte;
import com.bank.entity.TypeOperation;
import com.bank.service.CarteService;
import com.bank.service.OperationService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class MenuOperation {
    private final Scanner scanner;
    private final OperationService operationService;
    private final CarteService carteService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public MenuOperation(Scanner scanner, OperationService operationService, CarteService carteService) {
        this.scanner = scanner;
        this.operationService = operationService;
        this.carteService = carteService;
    }

    public void display() {
        boolean back = false;

        while (!back) {
            displayMenu();
            int choice = readChoice();

            switch (choice) {
                case 1 -> performOperation();
                case 2 -> displayOperation();
                case 3 -> displayCardOperations();
                case 4 -> displayAllOperations();
                case 5 -> displayOperationsByType();
                case 0 -> back = true;
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void displayMenu() {
        System.out.println("\n===== OPERATION MANAGEMENT =====");
        System.out.println("1. Perform an operation");
        System.out.println("2. Display an operation");
        System.out.println("3. Card history");
        System.out.println("4. Display all operations");
        System.out.println("5. Filter by operation type");
        System.out.println("0. Back");
        System.out.println("================================");
        System.out.print("Your choice: ");
    }

    private void performOperation() {
        try {
            System.out.println("\n--- New Operation ---");
            System.out.print("Card ID: ");
            int idCarte = Integer.parseInt(scanner.nextLine());

            Optional<Carte> carteOpt = carteService.obtenirCarte(idCarte);
            if (carteOpt.isEmpty()) {
                System.out.println("Card not found.");
                return;
            }

            Carte carte = carteOpt.get();
            System.out.println("Card: " + carte.getNumero() + " | Status: " + carte.getStatut());

            System.out.println("\nOperation type:");
            System.out.println("1. Purchase");
            System.out.println("2. Withdrawal");
            System.out.println("3. Online payment");
            System.out.print("Choice: ");
            int typeChoice = Integer.parseInt(scanner.nextLine());

            TypeOperation type = switch (typeChoice) {
                case 1 -> TypeOperation.ACHAT;
                case 2 -> TypeOperation.RETRAIT;
                case 3 -> TypeOperation.PAIEMENTENLIGNE;
                default -> throw new IllegalArgumentException("Invalid type");
            };

            System.out.print("Amount (EUR): ");
            BigDecimal amount = new BigDecimal(scanner.nextLine());

            System.out.print("Location: ");
            String location = scanner.nextLine();

            OperationCarte operation = operationService.enregistrerOperation(idCarte, amount, type, location);
            System.out.println("Operation recorded successfully!");
            System.out.println("ID: " + operation.id());
            System.out.println("Date: " + operation.date().format(formatter));
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void displayOperation() {
        try {
            System.out.print("\nOperation ID: ");
            int id = Integer.parseInt(scanner.nextLine());

            Optional<OperationCarte> operationOpt = operationService.obtenirOperation(id);
            if (operationOpt.isPresent()) {
                OperationCarte op = operationOpt.get();
                displayOperationDetails(op);
            } else {
                System.out.println("Operation not found.");
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
        }
    }

    private void displayCardOperations() {
        try {
            System.out.print("\nCard ID: ");
            int idCarte = Integer.parseInt(scanner.nextLine());

            List<OperationCarte> operations = operationService.obtenirOperationsCarte(idCarte);
            if (operations.isEmpty()) {
                System.out.println("\nNo operation found for this card.");
            } else {
                System.out.println("\n--- Card History ---");
                System.out.println(String.format("%-5s | %-17s | %-10s | %-20s | %-20s",
                    "ID", "Date", "Amount", "Type", "Location"));
                System.out.println("-".repeat(80));
                for (OperationCarte op : operations) {
                    System.out.println(String.format("%-5d | %-17s | %10.2f € | %-20s | %-20s",
                        op.id(),
                        op.date().format(formatter),
                        op.montant(),
                        op.type(),
                        op.lieu()));
                }
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
        }
    }

    private void displayAllOperations() {
        try {
            List<OperationCarte> operations = operationService.obtenirToutesLesOperations();
            if (operations.isEmpty()) {
                System.out.println("\nNo operation found.");
            } else {
                System.out.println("\n--- All Operations ---");
                System.out.println(String.format("%-5s | %-17s | %-10s | %-20s | %-10s",
                    "ID", "Date", "Amount", "Type", "Card ID"));
                System.out.println("-".repeat(75));
                for (OperationCarte op : operations) {
                    System.out.println(String.format("%-5d | %-17s | %10.2f € | %-20s | %-10d",
                        op.id(),
                        op.date().format(formatter),
                        op.montant(),
                        op.type(),
                        op.idCarte()));
                }
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
    }

    private void displayOperationsByType() {
        try {
            System.out.println("\nOperation type:");
            System.out.println("1. Purchase");
            System.out.println("2. Withdrawal");
            System.out.println("3. Online payment");
            System.out.print("Choice: ");
            int typeChoice = Integer.parseInt(scanner.nextLine());

            TypeOperation type = switch (typeChoice) {
                case 1 -> TypeOperation.ACHAT;
                case 2 -> TypeOperation.RETRAIT;
                case 3 -> TypeOperation.PAIEMENTENLIGNE;
                default -> throw new IllegalArgumentException("Invalid type");
            };

            List<OperationCarte> operations = operationService.obtenirOperationsParType(type);
            if (operations.isEmpty()) {
                System.out.println("\nNo operation of type " + type + " found.");
            } else {
                System.out.println("\n--- Operations of type " + type + " ---");
                for (OperationCarte op : operations) {
                    System.out.printf("%s | %s | %.2f € | Card: %d\n",
                        op.date().format(formatter), op.lieu(), op.montant(), op.idCarte());
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void displayOperationDetails(OperationCarte op) {
        System.out.println("\n--- Operation Details ---");
        System.out.println("ID: " + op.id());
        System.out.println("Date: " + op.date().format(formatter));
        System.out.println("Amount: " + op.montant() + " EUR");
        System.out.println("Type: " + op.type());
        System.out.println("Location: " + op.lieu());
        System.out.println("Card ID: " + op.idCarte());
    }

    private int readChoice() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
