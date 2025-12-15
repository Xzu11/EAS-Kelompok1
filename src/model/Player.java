package model;

public class Player {
    private String username;
    private String password;
    private int level, hp, maxHp, coins, xp, xpNextLevel, hpPotions, atkPotions, baseAttack;

    public Player(String u, String p) {
        this.username = u;
        this.password = p;
        this.level = 1;
        this.maxHp = 100;
        this.hp = 100;
        this.coins = 0;
        this.xp = 0;
        this.xpNextLevel = 100;
        this.hpPotions = 1;
        this.atkPotions = 0;
        this.baseAttack = 10;
    }

    public void gainXp(int amount) {
        xp += amount;
        if (xp >= xpNextLevel) {
            level++;
            xp = 0;
            xpNextLevel = (int) (xpNextLevel * 1.5);
            maxHp += 20;
            hp = maxHp;
            baseAttack += 2;
            System.out.println(">>> LEVEL UP! You are now Level " + level + " <<<");
        }
    }

    public void printStats() {
        System.out.println("\n--- " + username + " Status ---");
        System.out.println("Level: " + level + " | XP: " + xp + "/" + xpNextLevel);
        System.out.println("HP: " + hp + "/" + maxHp);
        System.out.println("Base Damage: " + baseAttack);
        System.out.println("Gold: " + coins);
        System.out.println("Inventory: [HP Potions: " + hpPotions + "] [STR Potions: " + atkPotions + "]");
    }

    public void recalcMaxHp() {
        this.maxHp = 100 + ((level - 1) * 20);
        if (hp > maxHp) hp = maxHp;
    }

    public void recalcXpNext() {
        this.xpNextLevel = 100 * (int) Math.pow(1.5, level - 1);
    }

    // Getter & Setter (enkapsulasi)
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    public int getHp() { return hp; }
    public void setHp(int hp) { this.hp = hp; }
    public int getMaxHp() { return maxHp; }
    public int getCoins() { return coins; }
    public void setCoins(int coins) { this.coins = coins; }
    public int getXp() { return xp; }
    public void setXp(int xp) { this.xp = xp; }
    public int getXpNextLevel() { return xpNextLevel; }
    public int getHpPotions() { return hpPotions; }
    public void setHpPotions(int hpPotions) { this.hpPotions = hpPotions; }
    public int getAtkPotions() { return atkPotions; }
    public void setAtkPotions(int atkPotions) { this.atkPotions = atkPotions; }
    public int getBaseAttack() { return baseAttack; }
    public void setBaseAttack(int baseAttack) { this.baseAttack = baseAttack; }
}

