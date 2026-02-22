package com.dannyandson.nutritionalbalance.effects;

import com.dannyandson.nutritionalbalance.Config;
import com.dannyandson.nutritionalbalance.NutritionalBalance;
import com.dannyandson.nutritionalbalance.setup.Registration;
import net.minecraft.core.Holder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

public class ModMobAffects {

    public static class Nourished extends MobEffect {
        private static int color = 3949738;

        public Nourished() {
            super(MobEffectCategory.BENEFICIAL, color);
        }

        public void setAttributes(){
            if (Config.NOURISHED_MAX_HEALTH.get() != 0d) {
                addAttributeModifier(Attributes.MAX_HEALTH, ResourceLocation.fromNamespaceAndPath(NutritionalBalance.MODID, "nourished_max_health"), Config.NOURISHED_MAX_HEALTH.get(), AttributeModifier.Operation.ADD_VALUE);
            }
            if (Config.NOURISHED_KNOCKBACK_RESISTANCE.get() != 0d) {
                addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, ResourceLocation.fromNamespaceAndPath(NutritionalBalance.MODID, "nourished_knockback_resistance"), Config.NOURISHED_KNOCKBACK_RESISTANCE.get(), AttributeModifier.Operation.ADD_VALUE);
            }
            if (Config.NOURISHED_MOVEMENT_SPEED.get() != 0d) {
                addAttributeModifier(Attributes.MOVEMENT_SPEED, ResourceLocation.fromNamespaceAndPath(NutritionalBalance.MODID, "nourished_movement_speed"), Config.NOURISHED_MOVEMENT_SPEED.get(), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
            }
            if (Config.NOURISHED_ATTACK_DAMAGE.get() != 0d) {
                addAttributeModifier(Attributes.ATTACK_DAMAGE, ResourceLocation.fromNamespaceAndPath(NutritionalBalance.MODID, "nourished_attack_damage"), Config.NOURISHED_ATTACK_DAMAGE.get(), AttributeModifier.Operation.ADD_VALUE);
            }
            if (Config.NOURISHED_ATTACK_KNOCKBACK.get() != 0d) {
                addAttributeModifier(Attributes.ATTACK_KNOCKBACK, ResourceLocation.fromNamespaceAndPath(NutritionalBalance.MODID, "nourished_attack_knockback"), Config.NOURISHED_ATTACK_KNOCKBACK.get(), AttributeModifier.Operation.ADD_VALUE);
            }
            if (Config.NOURISHED_ATTACK_SPEED.get() != 0d) {
                addAttributeModifier(Attributes.ATTACK_SPEED, ResourceLocation.fromNamespaceAndPath(NutritionalBalance.MODID, "nourished_attack_speed"), Config.NOURISHED_ATTACK_SPEED.get(), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
            }
        }
    }

    public static class MalNourished extends MobEffect {
        private static int color = 11546150;

        public MalNourished() {
            super(MobEffectCategory.HARMFUL, color);
        }

        public void setAttributes(){
            if (Config.MALNOURISHED_MAX_HEALTH.get() != 0d) {
                addAttributeModifier(Attributes.MAX_HEALTH, ResourceLocation.fromNamespaceAndPath(NutritionalBalance.MODID, "malnourished_max_health"), Config.MALNOURISHED_MAX_HEALTH.get(), AttributeModifier.Operation.ADD_VALUE);
            }
            if (Config.MALNOURISHED_KNOCKBACK_RESISTANCE.get() != 0d) {
                addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, ResourceLocation.fromNamespaceAndPath(NutritionalBalance.MODID, "malnourished_knockback_resistance"), Config.MALNOURISHED_KNOCKBACK_RESISTANCE.get(), AttributeModifier.Operation.ADD_VALUE);
            }
            if (Config.MALNOURISHED_MOVEMENT_SPEED.get() != 0d) {
                addAttributeModifier(Attributes.MOVEMENT_SPEED, ResourceLocation.fromNamespaceAndPath(NutritionalBalance.MODID, "malnourished_movement_speed"), Config.MALNOURISHED_MOVEMENT_SPEED.get(), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
            }
            if (Config.MALNOURISHED_ATTACK_DAMAGE.get() != 0d) {
                addAttributeModifier(Attributes.ATTACK_DAMAGE, ResourceLocation.fromNamespaceAndPath(NutritionalBalance.MODID, "malnourished_attack_damage"), Config.MALNOURISHED_ATTACK_DAMAGE.get(), AttributeModifier.Operation.ADD_VALUE);
            }
            if (Config.MALNOURISHED_ATTACK_KNOCKBACK.get() != 0d) {
                addAttributeModifier(Attributes.ATTACK_KNOCKBACK, ResourceLocation.fromNamespaceAndPath(NutritionalBalance.MODID, "malnourished_attack_knockback"), Config.MALNOURISHED_ATTACK_KNOCKBACK.get(), AttributeModifier.Operation.ADD_VALUE);
            }
            if (Config.MALNOURISHED_ATTACK_SPEED.get() != 0d) {
                addAttributeModifier(Attributes.ATTACK_SPEED, ResourceLocation.fromNamespaceAndPath(NutritionalBalance.MODID, "malnourished_attack_speed"), Config.MALNOURISHED_ATTACK_SPEED.get(), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
            }
        }
    }

    public static class Engorged extends MobEffect {
        private static int color = 11546150;

        public Engorged() {
            super(MobEffectCategory.HARMFUL, color);
        }

        public void setAttributes(){
            if (Config.ENGORGED_MAX_HEALTH.get() != 0d) {
                addAttributeModifier(Attributes.MAX_HEALTH, ResourceLocation.fromNamespaceAndPath(NutritionalBalance.MODID, "engorged_max_health"), Config.ENGORGED_MAX_HEALTH.get(), AttributeModifier.Operation.ADD_VALUE);
            }
            if (Config.ENGORGED_KNOCKBACK_RESISTANCE.get() != 0d) {
                addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, ResourceLocation.fromNamespaceAndPath(NutritionalBalance.MODID, "engorged_knockback_resistance"), Config.ENGORGED_KNOCKBACK_RESISTANCE.get(), AttributeModifier.Operation.ADD_VALUE);
            }
            if (Config.ENGORGED_MOVEMENT_SPEED.get() != 0d) {
                addAttributeModifier(Attributes.MOVEMENT_SPEED, ResourceLocation.fromNamespaceAndPath(NutritionalBalance.MODID, "engorged_movement_speed"), Config.ENGORGED_MOVEMENT_SPEED.get(), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
            }
            if (Config.ENGORGED_ATTACK_DAMAGE.get() != 0d) {
                addAttributeModifier(Attributes.ATTACK_DAMAGE, ResourceLocation.fromNamespaceAndPath(NutritionalBalance.MODID, "engorged_attack_damage"), Config.ENGORGED_ATTACK_DAMAGE.get(), AttributeModifier.Operation.ADD_VALUE);
            }
            if (Config.ENGORGED_ATTACK_KNOCKBACK.get() != 0d) {
                addAttributeModifier(Attributes.ATTACK_KNOCKBACK, ResourceLocation.fromNamespaceAndPath(NutritionalBalance.MODID, "engorged_attack_knockback"), Config.ENGORGED_ATTACK_KNOCKBACK.get(), AttributeModifier.Operation.ADD_VALUE);
            }
            if (Config.ENGORGED_ATTACK_SPEED.get() != 0d) {
                addAttributeModifier(Attributes.ATTACK_SPEED, ResourceLocation.fromNamespaceAndPath(NutritionalBalance.MODID, "engorged_attack_speed"), Config.ENGORGED_ATTACK_SPEED.get(), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
            }
        }
    }

    public static void resetPlayerEffects(Player player){
        Holder<MobEffect> nourishedHolder = BuiltInRegistries.MOB_EFFECT.wrapAsHolder(Registration.NOURISHED_EFFECT.get());
        Holder<MobEffect> malnourishedHolder = BuiltInRegistries.MOB_EFFECT.wrapAsHolder(Registration.MALNOURISHED_EFFECT.get());
        Holder<MobEffect> engorgedHolder = BuiltInRegistries.MOB_EFFECT.wrapAsHolder(Registration.ENGORGED_EFFECT.get());
        if (player.getEffect(nourishedHolder) != null)
            player.removeEffect(nourishedHolder);
        if (player.getEffect(malnourishedHolder) != null)
            player.removeEffect(malnourishedHolder);
        if (player.getEffect(engorgedHolder) != null)
            player.removeEffect(engorgedHolder);
    }

    public static void loadModEffects() {
        Registration.NOURISHED_EFFECT.get().setAttributes();
        Registration.MALNOURISHED_EFFECT.get().setAttributes();
        Registration.ENGORGED_EFFECT.get().setAttributes();
        NutritionalBalance.modEffectsLoaded = true;
    }
}
