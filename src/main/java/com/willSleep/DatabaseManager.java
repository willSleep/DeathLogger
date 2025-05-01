package com.willSleep;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.*;
import java.util.logging.Level;


/**
* 数据库管理类 - 负责所有SQLite数据库操作
*/

public class DatabaseManager {

    public final JavaPlugin plugin;

    private Connection connection;

    public DatabaseManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * 初始化数据库连接并创建表结构
     */
    public void initializeDatabase() {
        try {
            // 加载sqlite的jdbc驱动类
            Class.forName("org.sqlite.JDBC");

            // 构建数据库文件路径
            String dbPath = plugin.getDataFolder().getAbsolutePath() + "/death_records.db";

            // 建立连接
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);

            // 创建数据表
            createTables();

            plugin.getLogger().info("Successfully connected to database.");
        } catch (ClassNotFoundException e) {
            plugin.getLogger().log(Level.SEVERE, "Couldn't find JDBC...", e);
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to connection...", e);
        }

    }

    private void createTables() throws SQLException {
        // 建表语句
        String sql = "CREATE TABLE IF NOT EXISTS death_records (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT,"+        // 自增主键
                "player_uuid TEXT NOT NULL,"+                   // 玩家uuid
                "player_name TEXT NOT NULL,"+                   // 玩家名
                "death_time TIMESTAMP NOT NULL,"+               // 时间戳
                "death_cause TEXT NOT NULL,"+                   // 死亡原因
                "world TEXT NOT NULL,"+                         // 死亡世界
                "x REAL NOT NULL,"+                             // x坐标
                "y REAL NOT NULL,"+                             // y坐标
                "z REAL NOT NULL,"+                             // z坐标
                "inventory TEXT,"+                              // 背包物品(序列化字符串)
                "armor TEXT,"+                                  // 装备(序列化字符串)
                "offhand TEXT"+                                 // 副手物品(序列化字符串)
                ");";

        // 确保statement自动关闭
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    /**
     * 保存死亡记录到数据库
     * @param record 死亡记录对象
     */
    public void saveDeathRecord(DeathRecord record) {
        // PreparedStatement预编译sql防止注入
        String sql = "INSERT INTO death_records (" +
                "player_uuid, player_name, death_time, death_cause," +
                "world, x, y, z, inventory, armor, offhand)" +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            // 设置参数值
            pstmt.setString(1, record.getPlayerUuid().toString());
            pstmt.setString(2, record.getPlayerName());
            pstmt.setTimestamp(3, new Timestamp(record.getDeathTime().getTime()));
            pstmt.setString(4, record.getDeathCause());
            pstmt.setString(5, record.getWorld());
            pstmt.setDouble(6, record.getX());
            pstmt.setDouble(7, record.getY());
            pstmt.setDouble(8, record.getZ());
            pstmt.setString(9, record.getInventory());
            pstmt.setString(10, record.getArmor());
            pstmt.setString(11, record.getOffhand());

            // 插入
            pstmt.executeUpdate();

        } catch(SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Couldn't save death log...", e);
        }
    }

    /**
     * 关闭数据库连接
     */
    public void closeConnection() {
        try {
            // 检查连接状态是否有效且未关闭
            if (connection != null && !connection.isClosed()) {
                connection.close();
                plugin.getLogger().info("successfully close connection.");
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed close connection...", e);
        }
    }

}
