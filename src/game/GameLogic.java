package game;

import model.Enemy;
import model.Player;
import model.GameMap;

import java.util.Random;

public class GameLogic {
    private static final int WIN_LEVEL = 10;
    private final Random rand = new Random();
    private final UserInterface ui = new UserInterface();
    private final GameMap map = new GameMap(5, 5); // contoh 5x5 map

    public GameMap getMap() {
        return map;
    }

    // === Map & Explore ===
    public boolean explore(Player currentPlayer) {
    if (currentPlayer.getLevel() >= WIN_LEVEL) {
        ui.println("!!! YOU SENSE THE DRAGON LORD !!!");
        combat(currentPlayer, new Enemy("DRAGON LORD", 500, 50, 2000));
        return true; // tetap lanjut ke menu utama
    }

    ui.println("You walk into the dark halls...");
    try { Thread.sleep(500); } catch (InterruptedException ignored) {}

    ui.println("Move: 1.Up  2.Down  3.Left  4.Right");
    int move = InputHandler.readIntInRange("Move -> ", 1, 4);
    map.movePlayer(move);
    map.printMap();

    // event random (chest / musuh) seperti sebelumnya
    if (rand.nextInt(100) < 30) {
        int goldFound = rand.nextInt(30) + 10;
        ui.println("Result: You found a chest with " + goldFound + " coins!");
        currentPlayer.setCoins(currentPlayer.getCoins() + goldFound);
    } else {
        String[] names = {"Goblin", "Skeleton", "Orc", "Assassin", "Dark Knight"};
        String eName = names[rand.nextInt(names.length)];
        int eHp = 20 + (currentPlayer.getLevel() * 15);
        int eDmg = 5 + (currentPlayer.getLevel() * 3);
        int eXp = 25 + (currentPlayer.getLevel() * 5);
        combat(currentPlayer, new Enemy(eName, eHp, eDmg, eXp));
    }

    // === Cek apakah semua X sudah habis ===
    if (map.allObjectivesCleared()) {
        while (true) {
            ui.println("Map sudah terselesaikan, " + currentPlayer.getUsername() +
                    " ingin melanjutkan permainan?");
            ui.println("1. Lanjut ke map baru");
            ui.println("2. Keluar (save & logout)");

            String ans = InputHandler.readLine("Pilih (1/2) -> ");
            try {
                if (ans.equals("1")) {
                    ui.println("Map baru ditemukan!");
                    map.regenerateMap();
                    map.printMap();
                    return true;   // lanjut main
                } else if (ans.equals("2")) {
                    return false;  // minta game loop berhenti
                } else {
                    throw new IllegalArgumentException("Pilihan menu tidak valid: " + ans);
                }
            } catch (IllegalArgumentException e) {
                ui.println("Error: " + e.getMessage());
            }
        }
    }

    return true; // default: lanjut
}


    public void showMap() {
        map.printMap();
    }

    // === Combat ===
    public void combat(Player currentPlayer, Enemy enemy) {
        ui.println("\n*** BATTLE STARTED: " + enemy.getName() + " ***");
        boolean buffActive = false;

        while (enemy.getHp() > 0 && currentPlayer.getHp() > 0) {
            ui.println("\nEnemy HP: " + enemy.getHp() + " | Your HP: " +
                    currentPlayer.getHp() + "/" + currentPlayer.getMaxHp());
            ui.println("Potions: [H]ealth: " + currentPlayer.getHpPotions() +
                    " | [S]trength: " + currentPlayer.getAtkPotions());
            ui.println("Actions: [A]ttack | [H]eal | [S]trength Potion | [R]un");
            String action = InputHandler.readLine("Action -> ").toUpperCase();

            switch (action) {
                case "A":
                    int baseDmg = rand.nextInt(10) + currentPlayer.getBaseAttack() +
                            (currentPlayer.getLevel() * 2);
                    if (buffActive) {
                        baseDmg = (int) (baseDmg * 1.5);
                        System.out.print("[BUFFED] ");
                    }
                    int take = rand.nextInt(enemy.getAttack()) + 2;
                    enemy.setHp(enemy.getHp() - baseDmg);
                    currentPlayer.setHp(currentPlayer.getHp() - take);
                    ui.println("You hit for " + baseDmg + " dmg! Took " + take + " dmg.");
                    break;

                case "H":
                    if (currentPlayer.getHpPotions() > 0) {
                        currentPlayer.setHp(currentPlayer.getHp() + 50);
                        if (currentPlayer.getHp() > currentPlayer.getMaxHp())
                            currentPlayer.setHp(currentPlayer.getMaxHp());
                        currentPlayer.setHpPotions(currentPlayer.getHpPotions() - 1);
                        int take2 = rand.nextInt(enemy.getAttack()) + 2;
                        currentPlayer.setHp(currentPlayer.getHp() - take2);
                        ui.println("Healed 50 HP. Took " + take2 + " dmg while drinking.");
                    } else {
                        ui.println("No Health Potions left!");
                    }
                    break;

                case "S":
                    if (currentPlayer.getAtkPotions() > 0) {
                        if (!buffActive) {
                            currentPlayer.setAtkPotions(currentPlayer.getAtkPotions() - 1);
                            buffActive = true;
                            ui.println(">>> YOU DRINK THE RED ELIXIR! STRENGTH INCREASED! <<<");
                            int take3 = rand.nextInt(enemy.getAttack()) + 2;
                            currentPlayer.setHp(currentPlayer.getHp() - take3);
                            ui.println("The enemy hit you for " + take3 + " while you powered up.");
                        } else {
                            ui.println("You are already buffed!");
                        }
                    } else {
                        ui.println("No Strength Potions left!");
                    }
                    break;

                case "R":
                    if (enemy.getName().equals("DRAGON LORD")) {
                        ui.println("You cannot run from the Dragon!");
                    } else {
                        ui.println("You ran away.");
                        return;
                    }
                    break;

                default:
                    ui.println("Invalid action.");
            }
        }

        if (currentPlayer.getHp() > 0) {
            ui.println("VICTORY! +" + enemy.getXpReward() + " XP");
            currentPlayer.gainXp(enemy.getXpReward());
            int coinReward = rand.nextInt(25) + 15;
            currentPlayer.setCoins(currentPlayer.getCoins() + coinReward);

            if (enemy.getName().equals("DRAGON LORD")) {
                ui.printHeader("YOU HAVE BEATEN THE GAME!");
                ui.println("Legendary Status Achieved.");
            }
        }
    }

    // === Shop ===
    public void visitShop(Player currentPlayer) {
        boolean inShop = true;
        while (inShop) {
            ui.println("\n--- BLACKSMITH & ALCHEMIST ---");
            ui.println("Your Gold: " + currentPlayer.getCoins());
            ui.println("1. Buy Health Potion (50g)  - Restores 50 HP");
            ui.println("2. Buy Strength Potion (100g) - +50% DMG for one battle");
            ui.println("3. Sharpen Sword (250g) - PERMANENT +5 Base Damage");
            ui.println("4. Exit Shop");

            int c = InputHandler.readIntInRange("-> ", 1, 4);
            switch (c) {
                case 1:
                    if (currentPlayer.getCoins() >= 50) {
                        currentPlayer.setCoins(currentPlayer.getCoins() - 50);
                        currentPlayer.setHpPotions(currentPlayer.getHpPotions() + 1);
                        ui.println("Purchased Health Potion.");
                    } else ui.println("Not enough gold.");
                    break;
                case 2:
                    if (currentPlayer.getCoins() >= 100) {
                        currentPlayer.setCoins(currentPlayer.getCoins() - 100);
                        currentPlayer.setAtkPotions(currentPlayer.getAtkPotions() + 1);
                        ui.println("Purchased Strength Potion.");
                    } else ui.println("Not enough gold.");
                    break;
                case 3:
                    if (currentPlayer.getCoins() >= 250) {
                        currentPlayer.setCoins(currentPlayer.getCoins() - 250);
                        currentPlayer.setBaseAttack(currentPlayer.getBaseAttack() + 5);
                        ui.println("Weapon Sharpened! Base Attack is now " +
                                currentPlayer.getBaseAttack());
                    } else ui.println("Not enough gold.");
                    break;
                case 4:
                    inShop = false;
                    break;
            }
        }
    }
}

