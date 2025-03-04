package be.isach.ultracosmetics.cosmetics.type;

import be.isach.ultracosmetics.config.CustomConfiguration;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.deatheffects.*;
import be.isach.ultracosmetics.util.Particles;

import com.cryptomorin.xseries.XMaterial;

public class DeathEffectType extends CosmeticParticleType<DeathEffect> {

    public DeathEffectType(String configName, Particles effect, XMaterial material, Class<? extends DeathEffect> clazz, boolean supportsParticleMultiplier) {
        super(Category.DEATH_EFFECTS, configName, 0, effect, material, clazz, supportsParticleMultiplier);
        if (GENERATE_MISSING_MESSAGES) {
            MessageManager.addMessage(getConfigPath() + ".name", configName);
        }
    }

    public static void register() {
        new DeathEffectType("Explosion", Particles.EXPLOSION_HUGE, XMaterial.TNT, DeathEffectExplosion.class, false);
        new DeathEffectType("Firework", Particles.FIREWORKS_SPARK, XMaterial.FIREWORK_ROCKET, DeathEffectFirework.class, false);
        new DeathEffectType("Lightning", Particles.CRIT, XMaterial.DAYLIGHT_DETECTOR, DeathEffectLightning.class, false);
    }
}
