package game;

import model.Player;
import model.GameMap;

import java.io.*;
import java.util.*;

public class DataManager {
    private static final String DB_FILE = "data/gamedata.txt";
    private static final String SAVE_FILE = "data/save.txt";
    private final Map<String, Player> userDatabase = new HashMap<>();
    private final UserInterface ui = new UserInterface();

    public Map<String, Player> getUserDatabase() {
        return userDatabase;
    }

    // ----- Database user (multi-user, sama konsep lama) -----
    public void loadDatabase() {
        File file = new File(DB_FILE);
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 9) {
                    Player p = new Player(parts[0], parts[1]);
                    p.setLevel(Integer.parseInt(parts[2]));
                    p.setHp(Integer.parseInt(parts[3]));
                    p.recalcMaxHp();
                    p.setCoins(Integer.parseInt(parts[4]));
                    p.setXp(Integer.parseInt(parts[5]));
                    p.recalcXpNext();
                    p.setHpPotions(Integer.parseInt(parts[6]));
                    p.setAtkPotions(Integer.parseInt(parts[7]));
                    p.setBaseAttack(Integer.parseInt(parts[8]));
                    userDatabase.put(p.getUsername(), p);
                }
            }
            ui.println("[System] Database loaded.");
        } catch (IOException e) {
            ui.println("[Error] Could not load data.");
        }
    }

    public void saveDatabase() {
        File dir = new File("data");
        if (!dir.exists()) dir.mkdirs();
        try (PrintWriter pw = new PrintWriter(new FileWriter(DB_FILE))) {
            for (Player p : userDatabase.values()) {
                pw.println(p.getUsername() + "," + p.getPassword() + "," + p.getLevel() + "," +
                        p.getHp() + "," + p.getCoins() + "," + p.getXp() + "," +
                        p.getHpPotions() + "," + p.getAtkPotions() + "," + p.getBaseAttack());
            }
            ui.println("[System] Game saved successfully.");
        } catch (IOException e) {
            ui.println("[Error] Could not save data.");
        }
    }

    public Player login() {
        String user = InputHandler.readLine("Username: ");
        String pass = InputHandler.readLine("Password: ");

        if (userDatabase.containsKey(user)) {
            Player p = userDatabase.get(user);
            if (p.getPassword().equals(pass)) {
                ui.println("Welcome back, " + user + "!");
                return p;
            } else {
                ui.println("Wrong password!");
            }
        } else {
            ui.println("User not found.");
        }
        return null;
    }

    public void register() {
        String user = InputHandler.readLine("New Username: ");
        if (userDatabase.containsKey(user)) {
            ui.println("Username already taken!");
            return;
        }
        String pass = InputHandler.readLine("Set Password: ");
        Player newPlayer = new Player(user, pass);
        userDatabase.put(user, newPlayer);
        ui.println("Registration successful! You can now login.");
        saveDatabase();
    }

    public void showLeaderboard() {
        ui.printHeader("GLOBAL LEADERBOARD");
        List<Player> players = new ArrayList<>(userDatabase.values());
        players.sort((p1, p2) -> Integer.compare(p2.getLevel(), p1.getLevel()));

        int rank = 1;
        for (Player p : players) {
            System.out.println(rank + ". " + p.getUsername() + " - Lvl " + p.getLevel() +
                    " (Atk Power: " + p.getBaseAttack() + ")");
            rank++;
            if (rank > 5) break;
        }
        System.out.println("---------------------------------");
    }

    // ----- Save / Load Game (wajib sesuai instruksi) -----
    // Format save: username,level,hp,coins,xp,posX,posY
    public void saveGame(Player p, GameMap map) {
        File dir = new File("data");
        if (!dir.exists()) dir.mkdirs();
        try (PrintWriter pw = new PrintWriter(new FileWriter(SAVE_FILE))) {
            pw.println(p.getUsername() + "," + p.getLevel() + "," + p.getHp() + "," +
                    p.getCoins() + "," + p.getXp() + "," + map.getPlayerX() + "," + map.getPlayerY());
            ui.println("[System] Save file updated.");
        } catch (IOException e) {
            ui.println("[Error] Could not write save file.");
        }
    }

    public void loadSave(Player p, GameMap map) {
        File file = new File(SAVE_FILE);
        if (!file.exists()) return;
        try (Scanner sc = new Scanner(file)) {
            if (!sc.hasNextLine()) return;
            String line = sc.nextLine();
            String[] parts = line.split(",");
            if (parts.length >= 7 && parts[0].equals(p.getUsername())) {
                p.setLevel(Integer.parseInt(parts[1]));
                p.recalcMaxHp();
                p.setHp(Integer.parseInt(parts[2]));
                p.setCoins(Integer.parseInt(parts[3]));
                p.setXp(Integer.parseInt(parts[4]));
                p.recalcXpNext();
                map.setPlayerPosition(Integer.parseInt(parts[5]), Integer.parseInt(parts[6]));
                ui.println("[System] Save loaded for " + p.getUsername());
            }
        } catch (FileNotFoundException e) {
            ui.println("[Warning] Save file not found.");
        }
    }
}

