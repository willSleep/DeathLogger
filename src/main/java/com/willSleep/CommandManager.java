package com.willSleep;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * 命令管理类
 */
public class CommandManager implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch(command.getName().toLowerCase()) {
            // 根据指令分发逻辑
            case "deathclaim":
                return executeDeathClaim(sender, args);
            default:
                return false;
        }
    }

    private boolean executeDeathClaim(CommandSender sender, String[] args) {


        return true;
    }

}
