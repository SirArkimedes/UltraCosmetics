package be.isach.ultracosmetics.cosmetics;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.CosmeticEntType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.EntitySpawningManager;

import org.bukkit.entity.Entity;

public abstract class EntityCosmetic<T extends CosmeticEntType<?>,E extends Entity> extends Cosmetic<T> {
    /**
     * The Entity, if it isn't a Custom Entity.
     */
    protected E entity;

    public EntityCosmetic(UltraPlayer owner, T type, UltraCosmetics ultraCosmetics) {
        super(owner, type, ultraCosmetics);
    }

    public E getEntity() {
        return entity;
    }

    @SuppressWarnings("unchecked")
    protected E spawnEntity() {
        // Bypass WorldGuard protection.
        EntitySpawningManager.setBypass(true);
        E newEntity = (E) getPlayer().getWorld().spawnEntity(getPlayer().getLocation(), getType().getEntityType());
        EntitySpawningManager.setBypass(false);
        return newEntity;
    }

    protected void removeEntity() {
        if (entity != null) {
            entity.remove();
        }
    }

    protected void setupEntity() {
    }
}
