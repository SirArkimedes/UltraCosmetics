package be.isach.ultracosmetics.cosmetics.morphs;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.MorphType;
import be.isach.ultracosmetics.player.UltraPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.cryptomorin.xseries.XSound;

/**
 * Represents an instance of a cave spider morph summoned by a player.
 *
 * @author Chris6ix
 * @since 26-10-2022
 */
public class MorphCaveSpider extends Morph {
    private long coolDown = 0;

    public MorphCaveSpider(UltraPlayer owner, MorphType type, UltraCosmetics ultraCosmetics) {
        super(owner, type, ultraCosmetics);
    }

    @EventHandler
    public void onLeftClick(PlayerInteractEvent event) {
        if ((event.getAction() == Action.LEFT_CLICK_AIR
                || event.getAction() == Action.LEFT_CLICK_BLOCK) && event.getPlayer() == getPlayer()) {
            if (coolDown > System.currentTimeMillis()) return;
            event.setCancelled(true);
            XSound.ENTITY_SPIDER_AMBIENT.play(event.getPlayer().getLocation());
            coolDown = System.currentTimeMillis() + 500;
        }
    }
}
