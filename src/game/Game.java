package game;

import model.Player;

public class Game {
    private final UserInterface ui = new UserInterface();
    private final DataManager dataManager = new DataManager();
    private final GameLogic logic = new GameLogic();
    private Player currentPlayer;

    public void start() {
        dataManager.loadDatabase();
        ui.printHeader("THE SHADOW SPIRE V2: ADVANCED EDITION");

        boolean inAuthMenu = true;
        while (inAuthMenu) {
            ui.showAuthMenu();
            int choice = InputHandler.readIntInRange("-> ", 1, 4);
            switch (choice) {
                case 1:
                    currentPlayer = dataManager.login();
                    if (currentPlayer != null) inAuthMenu = false;
                    break;
                case 2:
                    dataManager.register();
                    break;
                case 3:
                    dataManager.showLeaderboard();
                    break;
                case 4:
                    ui.println("Goodbye.");
                    dataManager.saveDatabase();
                    return;
            }
        }

        // coba load save map + status
        dataManager.loadSave(currentPlayer, logic.getMap());

        if (currentPlayer != null) {
            runGameLoop();
        }

        dataManager.saveDatabase();
        ui.closeScanner();
    }

    private void runGameLoop() {
        boolean running = true;
        while (running) {
            ui.printMainMenu(currentPlayer);
            int input = InputHandler.readIntInRange("-> ", 1, 5);

            switch (input) {
                case 1:
                    logic.explore(currentPlayer);
                    break;
                case 2:
                    logic.visitShop(currentPlayer);
                    break;
                case 3:
                    currentPlayer.printStats();
                    break;
                case 4:
                    dataManager.saveGame(currentPlayer, logic.getMap());
                    dataManager.saveDatabase();
                    ui.println("Game saved & logged out.");
                    running = false;
                    break;
                case 5:
                    logic.showMap();
                    break;
            }

            if (currentPlayer.getHp() <= 0 && running) {
                ui.println("You have died. You were dragged back to town.");
                currentPlayer.setHp(currentPlayer.getMaxHp());
                currentPlayer.setCoins(currentPlayer.getCoins() / 2);
                currentPlayer.setXp(0);
                dataManager.saveDatabase();
            }
        }
    }
}
