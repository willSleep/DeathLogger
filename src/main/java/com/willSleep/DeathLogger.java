package com.willSleep;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;


public final class DeathLogger extends JavaPlugin {

    private DatabaseManager databaseManager;

    private File dataFolder;

    @Override
    public void onEnable() {
        // 创建配置文件夹
        dataFolder = new File(getDataFolder(), "DeathLogger");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        // 初始化数据库
        this.databaseManager = new DatabaseManager(this);
        this.databaseManager.initializeDatabase();

        // 注册事件监听器
        getServer().getPluginManager().registerEvents(new DeathListener(this), this);

        getLogger().info("DeathLogger has been enabled.");
    }

    @Override
    public void onDisable() {
        if (databaseManager != null) {
            databaseManager.closeConnection();
        }

        getLogger().info("DeathLogger has been disabled.");

    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
}
