package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.Updatable;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.run.FallDamageManager;
import be.isach.ultracosmetics.util.Area;
import be.isach.ultracosmetics.util.BlockUtils;
import be.isach.ultracosmetics.util.Particles;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.spigotmc.event.entity.EntityDismountEvent;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents an instance of a rocket gadget summoned by a player.
 *
 * @author iSach
 * @since 08-17-2015
 */
public class GadgetRocket extends Gadget implements Updatable {

    private static final BlockFace[] CARDINAL = new BlockFace[] { BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST };
    private static final Material FENCE = XMaterial.OAK_FENCE.parseMaterial();
    public static final Set<GadgetRocket> ROCKETS_WITH_BLOCKS = new HashSet<>();

    private boolean stillEquipped = true;
    private boolean launching;
    private ArmorStand armorStand;
    // key is used for easy access for contains() checks
    private Map<Block,BlockState> blocks = new HashMap<>();
    private List<FallingBlock> fallingBlocks = new ArrayList<>();
    private Entity playerVehicle = null;
    private int height;
    private RocketTask activeTask = null;

    public GadgetRocket(UltraPlayer owner, GadgetType type, UltraCosmetics ultraCosmetics) {
        super(owner, type, ultraCosmetics);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onRightClick() {
        getPlayer().setVelocity(new Vector(0, 1, 0));
        final Location loc = getPlayer().getLocation();
        loc.setX(loc.getBlockX() + 0.5);
        loc.setY(loc.getBlockY());
        loc.setZ(loc.getBlockZ() + 0.5);
        Bukkit.getScheduler().runTaskLater(getUltraCosmetics(), () -> {
            if (getOwner() == null || getOwner().getCurrentGadget() != this) return;
            ROCKETS_WITH_BLOCKS.add(this);
            for (int i = 0; i < 2; i++) {
                Block center = loc.clone().add(0, i, 0).getBlock();
                for (BlockFace face : CARDINAL) {
                    Block side = center.getRelative(face);
                    blocks.put(side, side.getState());
                    side.setType(FENCE);
                }
                Block quartz = center.getRelative(BlockFace.UP);
                blocks.put(quartz, quartz.getState());
                quartz.setType(Material.QUARTZ_BLOCK);
            }
            armorStand = loc.getWorld().spawn(loc.add(0, 1.5, 0), ArmorStand.class);
            armorStand.setVisible(false);
            armorStand.setGravity(false);
        }, 10);
        Bukkit.getScheduler().runTaskLater(getUltraCosmetics(), () -> {
            if (getOwner() == null || getOwner().getCurrentGadget() != this) return;
            // prevent kicking
            enableFlight();
            playerVehicle = null;
            armorStand.setPassenger(getPlayer());
            playerVehicle = armorStand;
            activeTask = new RocketTask() {
                private int countdown = 3;

                @Override
                public void run() {
                    if (getOwner() == null || getPlayer() == null || !getPlayer().isOnline()) {
                        cancel();
                        return;
                    }

                    if (!isStillCurrentGadget()) {
                        cancel();
                        return;
                    }
                    if (countdown > 0) {
                        sendTitle(ChatColor.RED.toString() + ChatColor.BOLD + countdown);
                        play(XSound.BLOCK_NOTE_BLOCK_BASS, getPlayer(), 1.0f, 1.0f);
                        countdown--;
                        return;
                    }

                    stop();

                    sendTitle(MessageManager.getMessage("Gadgets.Rocket.Takeoff"));
                    play(XSound.ENTITY_GENERIC_EXPLODE, getPlayer().getLocation(), 1.0f, 1.0f);

                    final FallingBlock top = BlockUtils.spawnFallingBlock(getPlayer().getLocation().add(0, 3, 0), Material.QUARTZ_BLOCK);
                    FallingBlock base = BlockUtils.spawnFallingBlock(getPlayer().getLocation().add(0, 2, 0), Material.QUARTZ_BLOCK);
                    for (int i = 0; i < 2; i++) {
                        fallingBlocks.add(BlockUtils.spawnFallingBlock(getPlayer().getLocation().add(0, 1 + i, 1), FENCE));
                        fallingBlocks.add(BlockUtils.spawnFallingBlock(getPlayer().getLocation().add(0, 1 + i, -1), FENCE));
                        fallingBlocks.add(BlockUtils.spawnFallingBlock(getPlayer().getLocation().add(1, 1 + i, 0), FENCE));
                        fallingBlocks.add(BlockUtils.spawnFallingBlock(getPlayer().getLocation().add(-1, 1 + i, 0), FENCE));
                    }

                    fallingBlocks.add(top);
                    fallingBlocks.add(base);
                    if (fallingBlocks.get(8).getPassenger() == null) {
                        fallingBlocks.get(8).setPassenger(getPlayer());
                    }
                    top.setPassenger(getPlayer());
                    playerVehicle = top;
                    launching = true;
                    activeTask = new RocketTask() {
                        @Override
                        public void run() {
                            if (getPlayer().getLocation().getBlockY() < height - 10) return;
                            playerVehicle = null;
                            if (!isStillCurrentGadget()) {
                                cancel();
                                activeTask = null;
                                return;
                            }
                            stop();
                            activeTask = null;
                        }

                        @Override
                        public void stop() {
                            fallingBlocks.forEach(Entity::remove);
                            fallingBlocks.clear();
                            FallDamageManager.addNoFall(getPlayer());
                            play(XSound.ENTITY_GENERIC_EXPLODE, getPlayer().getLocation(), 1.0f, 1.0f);
                            Particles.EXPLOSION_HUGE.display(getPlayer().getLocation());
                            disableFlight();
                            launching = false;
                            cancel();
                        }
                    }.schedule(getUltraCosmetics(), 5, 5);
                }

                @Override
                public void stop() {
                    playerVehicle = null;
                    armorStand.remove();
                    armorStand = null;

                    for (BlockState state : blocks.values()) {
                        state.update(true);
                    }

                    blocks.clear();
                    ROCKETS_WITH_BLOCKS.remove(GadgetRocket.this);
                    sendTitle(" ");
                    cancel();
                }
            }.schedule(getUltraCosmetics(), 0, 20);
        }, 12);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected boolean checkRequirements(PlayerInteractEvent event) {
        if (activeTask != null) return false;
        height = Area.findMaxY(getPlayer().getLocation(), 1);
        if (height < 25) {
            getPlayer().sendMessage(MessageManager.getMessage("Gadgets.Rocket.Not-Enough-Space"));
            return false;
        }
        if (!getPlayer().isOnGround()) {
            getPlayer().sendMessage(MessageManager.getMessage("Gadgets.Rocket.Not-On-Ground"));
            return false;
        }
        return true;
    }

    private boolean isStillCurrentGadget() {
        return stillEquipped;
    }

    @Override
    public void onUpdate() {
        for (FallingBlock fallingBlock : fallingBlocks) {
            fallingBlock.setVelocity(new Vector(0, 0.9, 0));
        }

        if (launching && !fallingBlocks.isEmpty()) {
            Particles.FLAME.display(0.3f, 0.2f, 0.3f, getPlayer().getLocation().add(0, -3, 0), 10);
            Particles.LAVA.display(0.3f, 0.2f, 0.3f, getPlayer().getLocation().add(0, -3, 0), 10);
            play(XSound.ENTITY_BAT_LOOP, fallingBlocks.get(9).getLocation().clone().add(0, -1, 0), 1.5f, 1.0f);
            play(XSound.BLOCK_FIRE_EXTINGUISH, fallingBlocks.get(9).getLocation().clone().add(0, -1, 0), 0.025f, 1.0f);
        }
    }

    protected void cleanup() {
        for (BlockState state : blocks.values()) {
            state.update(true);
        }
        for (FallingBlock fallingBlock : fallingBlocks) {
            fallingBlock.remove();
        }
        blocks.clear();
        playerVehicle = null;
        fallingBlocks.clear();
        if (armorStand != null) {
            armorStand.remove();
        }
        disableFlight();
        launching = false;

        if (getPlayer() != null) {
            sendTitle(" ");
        }
        if (activeTask != null) {
            activeTask.cancel();
        }
    }

    @Override
    public void onClear() {
        stillEquipped = false;
        cleanup();
    }

    public boolean containsBlock(Block block) {
        return blocks.containsKey(block);
    }

    @EventHandler
    public void onDismount(EntityDismountEvent event) {
        if (event.getEntity() != getPlayer()) return;
        if (event.getDismounted() != playerVehicle) return;
        disableFlight();
        cancel();
        if (activeTask != null) {
            activeTask.stop();
            activeTask = null;
        }
    }

    private void enableFlight() {
        getPlayer().setAllowFlight(true);
    }

    private void disableFlight() {
        if (getPlayer().getGameMode() != GameMode.CREATIVE) {
            getPlayer().setAllowFlight(false);
        }
    }

    @SuppressWarnings("deprecation")
    private void sendTitle(String title) {
        getPlayer().sendTitle(title, "");
    }

    private abstract class RocketTask extends BukkitRunnable {
        public abstract void stop();

        public RocketTask schedule(UltraCosmetics ultraCosmetics, long delay, long period) {
            runTaskTimer(ultraCosmetics, delay, period);
            return this;
        }
    }
}
