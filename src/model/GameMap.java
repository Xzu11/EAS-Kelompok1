package model;

import java.util.Random;

public class GameMap {
    private char[][] grid;
    private int playerX;
    private int playerY;
    private int remainingObjectives;
    
    // Variabel kunci: Mengingat apa yang ada di bawah kaki player (., X, atau V)
    private char underPlayer; 

    public GameMap(int rows, int cols) {
        grid = new char[rows][cols];
        initMap();
    }

    private void initMap() {
        // Isi seluruh map dengan titik (.)
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                grid[i][j] = '.';
            }
        }

        remainingObjectives = 0;
        
        // Taruh 3 X di posisi random
        placeRandomObjectives(3);

        // Set posisi awal player
        playerX = 0;
        playerY = 0;
        
        // Default pijakan awal adalah lantai kosong
        underPlayer = '.'; 
        grid[playerX][playerY] = 'P';
    }

    private void placeRandomObjectives(int count) {
        Random rand = new Random();
        int placed = 0;
        
        while (placed < count) {
            int x = rand.nextInt(grid.length);
            int y = rand.nextInt(grid[0].length);
            
            // Pastikan tidak overlap dengan player (0,0) dan belum ada objective
            if (grid[x][y] == '.' && !(x == 0 && y == 0)) {
                grid[x][y] = 'X';
                remainingObjectives++;
                placed++;
            }
        }
    }

    // --- LOGIKA PERGERAKAN ---
    public void movePlayer(int direction) {
        int newX = playerX;
        int newY = playerY;

        switch (direction) {
            case 1: newX = Math.max(0, playerX - 1); break; // Up
            case 2: newX = Math.min(grid.length - 1, playerX + 1); break; // Down
            case 3: newY = Math.max(0, playerY - 1); break; // Left
            case 4: newY = Math.min(grid[0].length - 1, playerY + 1); break; // Right
        }

        // Jika tidak bergerak (nabrak tembok), stop
        if (newX == playerX && newY == playerY) {
            return;
        }

        // 1. KEMBALIKAN isi tile lama
        // Jika tadinya kita injak V, saat pergi, V-nya muncul lagi di peta
        grid[playerX][playerY] = underPlayer;

        // 2. CEK tile tujuan
        char targetCell = grid[newX][newY];

        if (targetCell == 'X') {
            remainingObjectives--; // Objective berkurang
            underPlayer = 'V';     // Simpan 'V' di saku (akan digambar saat player pindah lagi nanti)
        } else if (targetCell == 'V') {
            underPlayer = 'V';     // Tetap simpan 'V'
        } else {
            underPlayer = '.';     // Lantai biasa
        }

        // 3. PINDAHKAN Player
        playerX = newX;
        playerY = newY;
        grid[playerX][playerY] = 'P';
    }

    public void printMap() {
        System.out.println("\n--- MAP ---");
        for (char[] row : grid) {
            for (char c : row) {
                System.out.print(c + " ");
            }
            System.out.println();
        }
        System.out.println("Objectives remaining: " + remainingObjectives);
    }

    public boolean allObjectivesCleared() {
        return remainingObjectives <= 0;
    }

    public int getPlayerX() { return playerX; }
    public int getPlayerY() { return playerY; }
    public char getUnderPlayer() { return underPlayer; } // Getter baru untuk save system

    public void setPlayerPosition(int x, int y) {
        // Hapus jejak player lama (kembalikan underPlayer)
        grid[playerX][playerY] = underPlayer;
        
        playerX = Math.max(0, Math.min(grid.length - 1, x));
        playerY = Math.max(0, Math.min(grid[0].length - 1, y));
        
        // Peringatan: Saat set posisi manual (misal load game), 
        // underPlayer harus sudah di-set sebelumnya lewat loadMapFromData
        grid[playerX][playerY] = 'P';
    }

    // Reset game total
    public void regenerateMap() {
        int rows = grid.length;
        int cols = grid[0].length;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = '.';
            }
        }
        remainingObjectives = 0;
        placeRandomObjectives(3);
        playerX = 0;
        playerY = 0;
        underPlayer = '.'; 
        grid[playerX][playerY] = 'P';
    }

    // --- FITUR SAVE & LOAD MAP ---

    // Mengubah data map (X dan V) menjadi satu baris String
    public String getMapDataString() {
        StringBuilder sb = new StringBuilder();
        boolean isEmpty = true;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                char c = grid[i][j];
                // Kita simpan X dan V. Posisi P disimpan terpisah di variable playerX/Y
                if (c == 'X' || c == 'V') {
                    sb.append(i).append(":").append(j).append(":").append(c).append("#");
                    isEmpty = false;
                }
            }
        }
        return isEmpty ? "EMPTY" : sb.toString();
    }

    // Mengembalikan kondisi map dari String
    public void loadMapFromData(String data, char savedUnderPlayer) {
        // Reset grid ke kosong
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                grid[i][j] = '.';
            }
        }
        this.remainingObjectives = 0;

        // Parse data
        if (!data.equals("EMPTY") && !data.isEmpty()) {
            String[] items = data.split("#");
            for (String item : items) {
                try {
                    String[] parts = item.split(":");
                    int r = Integer.parseInt(parts[0]);
                    int c = Integer.parseInt(parts[1]);
                    char type = parts[2].charAt(0);

                    grid[r][c] = type;

                    if (type == 'X') {
                        this.remainingObjectives++;
                    }
                } catch (Exception e) {
                    System.out.println("Error parsing map data: " + item);
                }
            }
        }
        
        // Restore pijakan terakhir player
        this.underPlayer = savedUnderPlayer;
    }
}