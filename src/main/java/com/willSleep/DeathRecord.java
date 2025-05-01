package com.willSleep;

import java.util.UUID;
import java.util.Date;

/**
 * 数据模型 - 死亡记录的数据模型
 */
public class DeathRecord {
    private final UUID playerUuid;
    private final String playerName;
    private final Date deathTime;
    private final String deathCause;
    private final String world;
    private final double x;
    private final double y;
    private final double z;
    private final String inventory;
    private final String armor;
    private final String offhand;


    public DeathRecord(UUID playerUuid, String playerName, Date deathTime,
                       String deathCause, org.bukkit.Location location,
                       String inventory, String armor, String offhand) {
        this.playerUuid = playerUuid;
        this.playerName = playerName;
        this.deathTime = deathTime;
        this.deathCause = deathCause;
        this.world = location.getWorld().getName();
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.inventory = inventory;
        this.armor = armor;
        this.offhand = offhand;
    }

    // Getters
    public UUID getPlayerUuid() { return playerUuid; }
    public String getPlayerName() { return playerName; }
    public Date getDeathTime() { return deathTime; }
    public String getDeathCause() { return deathCause; }
    public String getWorld() { return world; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }
    public String getInventory() { return inventory; }
    public String getArmor() { return armor; }
    public String getOffhand() { return offhand; }

}
