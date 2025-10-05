package com.bank.ui;

import com.bank.entity.Client;
import com.bank.service.ClientService;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class MenuClient {
    private final Scanner scanner;
    private final ClientService clientService;

    public MenuClient(Scanner scanner, ClientService clientService) {
        this.scanner = scanner;
        this.clientService = clientService;
    }

    public void display() {
        boolean back = false;

        while (!back) {
            displayMenu();
            int choice = readChoice();

            switch (choice) {
                case 1 -> createClient();
                case 2 -> displayClient();
                case 3 -> displayAllClients();
                case 4 -> searchByEmail();
                case 5 -> searchByPhone();
                case 6 -> updateClient();
                case 7 -> deleteClient();
                case 0 -> back = true;
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void displayMenu() {
        System.out.println("\n===== CLIENT MANAGEMENT =====");
        System.out.println("1. Create a client");
        System.out.println("2. Display a client");
        System.out.println("3. Display all clients");
        System.out.println("4. Search by email");
        System.out.println("5. Search by phone");
        System.out.println("6. Update a client");
        System.out.println("7. Delete a client");
        System.out.println("0. Back");
        System.out.println("===============================");
        System.out.print("Your choice: ");
    }

    private void createClient() {
        try {
            System.out.println("\n--- Create a client ---");
            System.out.print("Name: ");
            String name = scanner.nextLine();
            System.out.print("Email: ");
            String email = scanner.nextLine();
            System.out.print("Phone: ");
            String phone = scanner.nextLine();

            Client client = clientService.createClient(name, email, phone);
            System.out.println("Client created successfully! ID: " + client.id());
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void displayClient() {
        try {
            System.out.print("\nClient ID: ");
            int id = Integer.parseInt(scanner.nextLine());

            Optional<Client> clientOpt = clientService.getClient(id);
            if (clientOpt.isPresent()) {
                Client client = clientOpt.get();
                System.out.println("\n--- Client information ---");
                System.out.println("ID: " + client.id());
                System.out.println("Name: " + client.name());
                System.out.println("Email: " + client.email());
                System.out.println("Phone: " + client.phone());
            } else {
                System.out.println("Client not found.");
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
        }
    }

    private void displayAllClients() {
        try {
            List<Client> clients = clientService.getAllClients();
            if (clients.isEmpty()) {
                System.out.println("\nNo client found.");
            } else {
                System.out.println("\n--- Client list ---");
                for (Client client : clients) {
                    System.out.printf("ID: %d | Name: %s | Email: %s | Phone: %s\n",
                        client.id(), client.name(), client.email(), client.phone());
                }
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
    }

    private void searchByEmail() {
        try {
            System.out.print("\nEmail: ");
            String email = scanner.nextLine();

            Optional<Client> clientOpt = clientService.searchByEmail(email);
            if (clientOpt.isPresent()) {
                Client client = clientOpt.get();
                System.out.println("Client found:");
                System.out.println("ID: " + client.id() + " | Name: " + client.name());
            } else {
                System.out.println("No client with this email.");
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
    }

    private void searchByPhone() {
        try {
            System.out.print("\nPhone: ");
            String phone = scanner.nextLine();

            Optional<Client> clientOpt = clientService.searchByPhone(phone);
            if (clientOpt.isPresent()) {
                Client client = clientOpt.get();
                System.out.println("Client found:");
                System.out.println("ID: " + client.id() + " | Name: " + client.name());
            } else {
                System.out.println("No client with this phone.");
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
    }

    private void updateClient() {
        try {
            System.out.print("\nClient ID to update: ");
            int id = Integer.parseInt(scanner.nextLine());

            Optional<Client> clientOpt = clientService.getClient(id);
            if (clientOpt.isEmpty()) {
                System.out.println("Client not found.");
                return;
            }

            System.out.print("New name: ");
            String name = scanner.nextLine();
            System.out.print("New email: ");
            String email = scanner.nextLine();
            System.out.print("New phone: ");
            String phone = scanner.nextLine();

            Client client = new Client(id, name, email, phone);
            if (clientService.updateClient(client)) {
                System.out.println("Client updated successfully!");
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
        }
    }

    private void deleteClient() {
        try {
            System.out.print("\nClient ID to delete: ");
            int id = Integer.parseInt(scanner.nextLine());

            System.out.print("Confirm deletion? (yes/no): ");
            String confirmation = scanner.nextLine();

            if (confirmation.equalsIgnoreCase("yes")) {
                if (clientService.deleteClient(id)) {
                    System.out.println("Client deleted successfully!");
                }
            } else {
                System.out.println("Deletion cancelled.");
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
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
