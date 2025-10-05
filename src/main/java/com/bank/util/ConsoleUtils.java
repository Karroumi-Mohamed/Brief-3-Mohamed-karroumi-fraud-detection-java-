package com.bank.util;

import java.util.Scanner;

public class ConsoleUtils {

    private final static Scanner scanner = new Scanner(System.in);

    public static String readString(String prompt) {
        System.out.print(prompt + ": ");
        return scanner.nextLine().trim();
    }

    public static int readInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt + ": ");
                int value = Integer.parseInt(scanner.nextLine().trim());
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid integer");
            }
        }
    }

    public static double readDouble(String prompt) {
        while (true) {
            try {
                System.out.print(prompt + ": ");
                double value = Double.parseDouble(scanner.nextLine().trim());
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid decimal number.");
            }
        }
    }

    public static String readEmail(String prompt) {
        while (true) {
            String email = readString(prompt);
            if (email.contains("@")) {
                return email;
            }
            System.out.println("Enter a valid email");
        }
    }

    public static void displayMenu(String title, String[] options) {
        System.out.println("\n==== "+title+" ====");
        for (int i = 0; i < options.length; i++) {
            System.out.println((i + 1) + ". " + options[i]);
        }
        System.out.println();
    }
}
