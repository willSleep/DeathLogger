package com.willSleep;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;


public final class DeathLogger extends JavaPlugin {

    private DatabaseManager databaseManager;

    private File dataFolder;

    @Override
    public void onEnable() {

        // 生成配置文件
        saveDefaultConfig();

        // 初始化数据库
        this.databaseManager = new DatabaseManager(this);
        this.databaseManager.initializeDatabase();

        // 注册事件监听器
        getServer().getPluginManager().registerEvents(new DeathListener(this), this);

        // 为指令注册执行
        getServer().getPluginCommand("deathclaim").setExecutor(new CommandManager());   // 理赔

        getLogger().info("插件已加载");

        System.out.println(getDataFolder().getAbsolutePath());   // test
    }

    @Override
    public void onDisable() {
        if (databaseManager != null) {
            databaseManager.closeConnection();
        }

        getLogger().info("插件已卸载");

    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
}
