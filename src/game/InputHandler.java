package game;

import java.util.InputMismatchException;
import java.util.Scanner;

public class InputHandler {
    private static final Scanner scanner = new Scanner(System.in);

    public static int readIntInRange(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = scanner.nextInt();
                scanner.nextLine(); // clear newline
                if (value < min || value > max) {
                    System.out.println("Input must be between " + min + " and " + max + ".");
                } else {
                    return value;
                }
            } catch (InputMismatchException e) {
                System.out.println("Please input a valid number.");
                scanner.nextLine(); // clear invalid
            }
        }
    }

    public static String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }
}

