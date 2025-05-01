package com.willSleep;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import javax.xml.crypto.dsig.spec.XSLTTransformParameterSpec;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 监听器 - 用于监听玩家死亡相关事件
 */
public class DeathListener implements Listener {
    private final DatabaseManager databaseManager;

    public DeathListener(DeathLogger plugin) {
        this.databaseManager = plugin.getDatabaseManager();
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        PlayerInventory inventory = player.getInventory();

        // 创建死亡记录对象
        DeathRecord record = new DeathRecord(
                player.getUniqueId(),
                player.getName(),
                new Date(),   // 精确到毫秒
                event.getDeathMessage(),
                player.getLocation(),
                serializeInventory(inventory.getContents()),
                serializeInventory(inventory.getArmorContents()),
                serializeItemStack(inventory.getItemInOffHand())
        );

        // 保存到数据库
        databaseManager.saveDeathRecord(record);

        player.sendMessage("死亡信息已记录至数据库");

    }

    /**
     * 序列化物品表
     * @param items 物品表数组
     * @return 序列化后的物品表字符串
     */
    private String serializeInventory(ItemStack[] items) {
        List<String> serialized = new ArrayList<>();
        for (ItemStack item : items) {
            serialized.add(serializeItemStack(item));
        }
        return String.join(";", serialized);
    }

    /**
     * 序列化物品堆
     * @param item 物品堆对象
     * @return 序列化后的物品字符串
     */
    private String serializeItemStack(ItemStack item) {
        if (item == null || item.getType().isAir()) {
            return "null";
        }

        StringBuilder builder = new StringBuilder();
        builder.append(item.getType().name());
        builder.append(",").append(item.getAmount());

        if (item.getItemMeta() != null && item.getItemMeta().hasDisplayName()) {
            // 替换所有.到\
            builder.append(",").append(item.getItemMeta().getDisplayName().replace(".", "\\"));
        }

        return builder.toString();


    }

}
