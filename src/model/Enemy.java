package model;

public class Enemy {
    private String name;
    private int hp, attack, xpReward;

    public Enemy(String n, int h, int a, int x) {
        this.name = n;
        this.hp = h;
        this.attack = a;
        this.xpReward = x;
    }

    public String getName() { return name; }
    public int getHp() { return hp; }
    public void setHp(int hp) { this.hp = hp; }
    public int getAttack() { return attack; }
    public int getXpReward() { return xpReward; }
}

