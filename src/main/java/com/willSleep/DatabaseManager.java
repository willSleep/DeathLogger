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
            String DBPath = plugin.getDataFolder().getAbsolutePath() + "/DeathLogger.db";

            // 建立连接
            connection = DriverManager.getConnection("jdbc:sqlite:" + DBPath);

            // 创建数据表
            createTables();

            plugin.getLogger().info("成功连接数据库.");
        } catch (ClassNotFoundException e) {
            plugin.getLogger().log(Level.SEVERE, "JDBC驱动程序未找到...", e);
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "无法连接数据库...", e);
        }

    }

    private void createTables() throws SQLException {
        // 建立玩家死亡信息表格
        String sql1 = "CREATE TABLE IF NOT EXISTS death_records (" +
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
                "offhand TEXT,"+                                // 副手物品(序列化字符串)
                "ping INTEGER"+                                    // 延迟
                ");";

        // 建立待转发到管理员的服务器通信信息表格
        String sql2 = "CREATE TABLE IF NOT EXISTS message_to_admins (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT,"+        // 自增主键
                "sender_uuid TEXT NOT NULL,"+                   // 发送者uuid
                "sender_name TEXT NOT NULL,"+                   // 发送者名称
                "send_time TIMESTAMP NOT NULL,"+                // 时间戳
                "request_id INTEGER NOT NULL"+                     // 请求的理赔id
                "content TEXT NOT NULL"+                        // 附加内容
                ");";

        // 确保statement自动关闭
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql1);
            stmt.execute(sql2);
        }
    }

    /**
     * 保存死亡记录到数据库
     * @param record 死亡记录对象
     */
    public void saveDeathRecord(DeathRecord record) {
        // PreparedStatement预编译sql防止注入
        String sql1 = "INSERT INTO death_records (" +
                "player_uuid, player_name, death_time, death_cause," +
                "world, x, y, z, inventory, armor, offhand, ping)" +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String sql2 = "INSERT INTO message_to_admins (" +
                "sender_uuid, sender_name, send_time, request_id, "+
                "content)"+
                "VALUES (?, ?, ?, ?, ?)";

        // 插入死亡数据
        try (PreparedStatement pstmt = connection.prepareStatement(sql1)) {
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
            pstmt.setInt(12, record.getPing());

            // 插入
            pstmt.executeUpdate();

        } catch(SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "无法保存死亡记录...", e);
        }

        // 插入待转发到管理员的服务器通信信息
        try (PreparedStatement pstmt = connection.prepareStatement(sql2)) {
            // 设置参数值


            // 插入
            pstmt.executeUpdate();

        } catch(SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "无法保存死亡记录...", e);
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
                plugin.getLogger().info("成功关闭与数据库的连接.");
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "关闭连接失败...", e);
        }
    }

}
