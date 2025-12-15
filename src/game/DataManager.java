package game;

import model.Player;
import model.GameMap;

import java.io.*;
import java.util.*;

public class DataManager {
    private static final String DB_FILE = "data/gamedata.txt";
    private static final String SAVE_FILE = "data/save.txt";
    private final Map<String, Player> userDatabase = new HashMap<>();
    private final UserInterface ui = new UserInterface(); // Asumsi kelas ini ada

    public Map<String, Player> getUserDatabase() {
        return userDatabase;
    }

    // ----- Database User (Load/Save Akun) -----
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
                    // p.recalcMaxHp(); // Aktifkan jika ada method ini
                    p.setCoins(Integer.parseInt(parts[4]));
                    p.setXp(Integer.parseInt(parts[5]));
                    // p.recalcXpNext(); // Aktifkan jika ada method ini
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
        // Asumsi InputHandler ada
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

    // ----- SAVE & LOAD GAME PROGRESS (Updated) -----
    
    // Format Baru: 
    // username,level,hp,coins,xp,posX,posY,underPlayer,mapStringData
    public void saveGame(Player p, GameMap map) {
        File dir = new File("data");
        if (!dir.exists()) dir.mkdirs();
        
        // Ambil data peta yang sudah dikonversi jadi String
        String mapData = map.getMapDataString();
        
        try (PrintWriter pw = new PrintWriter(new FileWriter(SAVE_FILE))) {
            pw.println(p.getUsername() + "," + 
                       p.getLevel() + "," + 
                       p.getHp() + "," +
                       p.getCoins() + "," + 
                       p.getXp() + "," + 
                       map.getPlayerX() + "," + 
                       map.getPlayerY() + "," + 
                       map.getUnderPlayer() + "," + // Simpan pijakan (X/V/.)
                       mapData);                    // Simpan layout map
                       
            ui.println("[System] Progress saved successfully!");
        } catch (IOException e) {
            ui.println("[Error] Could not write save file.");
        }
    }

    public void loadSave(Player p, GameMap map) {
        File file = new File(SAVE_FILE);
        if (!file.exists()) {
            ui.println("[Info] No save file found.");
            return;
        }
        
        try (Scanner sc = new Scanner(file)) {
            if (!sc.hasNextLine()) return;
            String line = sc.nextLine();
            String[] parts = line.split(",");

            // Validasi kepemilikan save file
            if (parts.length >= 7 && parts[0].equals(p.getUsername())) {
                // 1. Load data Player
                p.setLevel(Integer.parseInt(parts[1]));
                // p.recalcMaxHp(); 
                p.setHp(Integer.parseInt(parts[2]));
                p.setCoins(Integer.parseInt(parts[3]));
                p.setXp(Integer.parseInt(parts[4]));
                // p.recalcXpNext();

                int loadedX = Integer.parseInt(parts[5]);
                int loadedY = Integer.parseInt(parts[6]);

                // 2. Load data Map
                // Cek apakah save file menggunakan format baru (ada data map)
                if (parts.length >= 9) {
                    char savedUnderPlayer = parts[7].charAt(0);
                    String mapData = parts[8];
                    
                    // Restore bentuk map dan status objective
                    map.loadMapFromData(mapData, savedUnderPlayer);
                    
                    // Taruh player di posisi terakhir
                    // (Method ini aman dipanggil karena loadMapFromData sudah set underPlayer)
                    map.setPlayerPosition(loadedX, loadedY);
                    
                    ui.println("[System] Game loaded. Map restored.");
                } else {
                    // Fallback untuk save file versi lama
                    map.regenerateMap(); 
                    map.setPlayerPosition(loadedX, loadedY);
                    ui.println("[System] Old save loaded. Map reset due to format update.");
                }
            } else {
                ui.println("[Warning] Save file belongs to another user or empty.");
            }
        } catch (Exception e) {
            ui.println("[Error] Save file corrupted.");
            e.printStackTrace();
        }
    }
}