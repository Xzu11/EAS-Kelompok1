package model;

public class GameMap {
    private char[][] grid;
    private int playerX;
    private int playerY;
    private int remainingObjectives;
    
    // TAMBAHAN: Variabel untuk menyimpan apa yang ada di bawah kaki player (apakah '.', 'X', atau 'V')
    private char underPlayer; 

    public GameMap(int rows, int cols) {
        grid = new char[rows][cols];
        initMap();
    }

    private void initMap() {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                grid[i][j] = '.';
            }
        }

        remainingObjectives = 0;
        placeRandomObjectives(3);

        playerX = 0;
        playerY = 0;
        
        // Awal game, player berdiri di atas lantai kosong
        underPlayer = '.'; 
        grid[playerX][playerY] = 'P';
    }

    private void placeRandomObjectives(int count) {
        java.util.Random rand = new java.util.Random();
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

    public void movePlayer(int direction) {
        int newX = playerX;
        int newY = playerY;

        switch (direction) {
            case 1: newX = Math.max(0, playerX - 1); break; // up
            case 2: newX = Math.min(grid.length - 1, playerX + 1); break; // down
            case 3: newY = Math.max(0, playerY - 1); break; // left
            case 4: newY = Math.min(grid[0].length - 1, playerY + 1); break; // right
        }

        // Jika posisi tidak berubah (misal nabrak tembok), tidak perlu update apa-apa
        if (newX == playerX && newY == playerY) {
            return;
        }

        // 1. KEMBALIKAN isi grid lama (sebelum player pindah)
        // Jika sebelumnya kita menginjak 'V', maka saat pergi tinggalkan 'V' lagi.
        grid[playerX][playerY] = underPlayer;

        // 2. CEK apa yang ada di posisi BARU (newX, newY)
        char targetCell = grid[newX][newY];

        if (targetCell == 'X') {
            // Jika kita menginjak Objective
            remainingObjectives--;
            underPlayer = 'V'; // Kita simpan 'V' di saku, supaya saat player pergi nanti, jejaknya jadi 'V'
        } else if (targetCell == 'V') {
            // Jika kita menginjak yang sudah pernah diambil (V), tetap simpan V
            underPlayer = 'V';
        } else {
            // Jika menginjak lantai biasa
            underPlayer = '.';
        }

        // 3. UPDATE posisi player
        playerX = newX;
        playerY = newY;
        grid[playerX][playerY] = 'P'; // Tampilkan player di posisi baru
    }

    public boolean allObjectivesCleared() {
        return remainingObjectives <= 0;
    }

    public int getPlayerX() { return playerX; }
    public int getPlayerY() { return playerY; }

    public void setPlayerPosition(int x, int y) {
        // Kembalikan tile lama
        grid[playerX][playerY] = underPlayer; 
        
        playerX = Math.max(0, Math.min(grid.length - 1, x));
        playerY = Math.max(0, Math.min(grid[0].length - 1, y));
        
        // Asumsikan teleport selalu ke tempat kosong (reset underPlayer)
        // Atau kamu bisa cek grid[playerX][playerY] dulu seperti di movePlayer jika perlu
        underPlayer = '.'; 
        grid[playerX][playerY] = 'P';
    }

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
        underPlayer = '.'; // Reset memory pijakan player
        grid[playerX][playerY] = 'P';
    }
}