package game;

import java.util.Scanner;
import model.Player;

public class UserInterface {
    private static final Scanner SCANNER = new Scanner(System.in);

    public Scanner getScanner() {
        return SCANNER;
    }

    public void printHeader(String txt) {
        System.out.println("=================================");
        System.out.println(txt);
        System.out.println("=================================");
    }

    public void showAuthMenu() {
        System.out.println("1. Login");
        System.out.println("2. Register New Hero");
        System.out.println("3. View Global Leaderboard");
        System.out.println("4. Exit");
    }

    public void printMainMenu(Player p) {
        printHeader("MENU - " + p.getUsername() + " (Lvl " + p.getLevel() + ")");
        System.out.println("1. Explore the Dungeon");
        System.out.println("2. Visit the Blacksmith & Alchemist (Shop)");
        System.out.println("3. Character Stats / Inventory");
        System.out.println("4. Save & Logout");
        System.out.println("5. Show Map");
    }

    public void print(String s) {
        System.out.print(s);
    }

    public void println(String s) {
        System.out.println(s);
    }

    public void closeScanner() {
        SCANNER.close();
    }
}

