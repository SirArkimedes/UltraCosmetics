package be.isach.ultracosmetics.command.subcommands;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.command.SubCommand;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.type.CosmeticType;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class SubCommandReload extends SubCommand {

    public SubCommandReload(UltraCosmetics ultraCosmetics) {
        super("reload", "Reloads the plugin", "", ultraCosmetics);
    }

    @Override
    protected void onExeAnyone(CommandSender sender, String[] args) {
        sender.sendMessage(ChatColor.RED + "Warning: this may cause bad bugs to occur. If you experience issues, please restart the server.");
        ultraCosmetics.getLogger().info("Shutting down...");
        ultraCosmetics.shutdown();
        CosmeticType.removeAllTypes();
        ultraCosmetics.getLogger().info("Starting up...");
        ultraCosmetics.start();
        MessageManager.reload();
    }

}
