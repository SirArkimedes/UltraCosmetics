package be.isach.ultracosmetics.treasurechests.loot;

import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;

import org.bukkit.entity.Player;

import com.cryptomorin.xseries.XMaterial;

public class MoneyLoot implements Loot {

    @Override
    public LootReward giveToPlayer(Player player) {
        int min = SettingsManager.getConfig().getInt("TreasureChests.Loots.Money.Min");
        int max = SettingsManager.getConfig().getInt("TreasureChests.Loots.Money.Max");
        int money = randomInRange(min, max);
        UltraCosmeticsData.get().getPlugin().getEconomyHandler().getHook().deposit(player, money);
        // Spawn a firework if the player got more than 3/4 of the money they could have.
        boolean firework = money > 3 * SettingsManager.getConfig().getInt("TreasureChests.Loots.Money.Max") / 4;
        boolean toOthers = SettingsManager.getConfig().getBoolean("TreasureChests.Loots.Money.Message.enabled");
        String[] name = MessageManager.getMessage("Treasure-Chests-Loot.Money").replace("%money%", money + "").split("\n");
        String msg = getConfigMessage("TreasureChests.Loots.Money.Message.message").replace("%money%", String.valueOf(money));
        return new LootReward(name, XMaterial.SUNFLOWER.parseItem(), msg, toOthers, firework);
    }
}
