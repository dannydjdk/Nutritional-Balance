package com.dannyandson.nutritionalbalance.effects;

import com.dannyandson.nutritionalbalance.Config;
import com.dannyandson.nutritionalbalance.NutritionalBalance;
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
                addAttributeModifier(Attributes.MAX_HEALTH, "f812307e-2e1b-11ec-8d3d-0242ac130003", Config.NOURISHED_MAX_HEALTH.get(), AttributeModifier.Operation.ADDITION);
            }
            if (Config.NOURISHED_KNOCKBACK_RESISTANCE.get() != 0d) {
                addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, "f8123510-2e1b-11ec-8d3d-0242ac130003", Config.NOURISHED_KNOCKBACK_RESISTANCE.get(), AttributeModifier.Operation.ADDITION);
            }
            if (Config.NOURISHED_MOVEMENT_SPEED.get() != 0d) {
                addAttributeModifier(Attributes.MOVEMENT_SPEED, "f812363c-2e1b-11ec-8d3d-0242ac130003", Config.NOURISHED_MOVEMENT_SPEED.get(), AttributeModifier.Operation.MULTIPLY_TOTAL);
            }
            if (Config.NOURISHED_ATTACK_DAMAGE.get() != 0d) {
                addAttributeModifier(Attributes.ATTACK_DAMAGE, "f812363c-2e1b-11ec-8d3d-0242ac130003", Config.NOURISHED_ATTACK_DAMAGE.get(), AttributeModifier.Operation.ADDITION);
            }
            if (Config.NOURISHED_ATTACK_KNOCKBACK.get() != 0d) {
                addAttributeModifier(Attributes.ATTACK_KNOCKBACK, "f812363c-2e1b-11ec-8d3d-0242ac130003", Config.NOURISHED_ATTACK_KNOCKBACK.get(), AttributeModifier.Operation.ADDITION);
            }
            if (Config.NOURISHED_ATTACK_SPEED.get() != 0d) {
                addAttributeModifier(Attributes.ATTACK_SPEED, "f812363c-2e1b-11ec-8d3d-0242ac130003", Config.NOURISHED_ATTACK_SPEED.get(), AttributeModifier.Operation.MULTIPLY_TOTAL);
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
                addAttributeModifier(Attributes.MAX_HEALTH, "f812307e-2e1b-11ec-8d3d-0242ac130004", Config.MALNOURISHED_MAX_HEALTH.get(), AttributeModifier.Operation.ADDITION);
            }
            if (Config.MALNOURISHED_KNOCKBACK_RESISTANCE.get() != 0d) {
                addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, "f8123510-2e1b-11ec-8d3d-0242ac130004", Config.MALNOURISHED_KNOCKBACK_RESISTANCE.get(), AttributeModifier.Operation.ADDITION);
            }
            if (Config.MALNOURISHED_MOVEMENT_SPEED.get() != 0d) {
                addAttributeModifier(Attributes.MOVEMENT_SPEED, "f812363c-2e1b-11ec-8d3d-0242ac130004", Config.MALNOURISHED_MOVEMENT_SPEED.get(), AttributeModifier.Operation.MULTIPLY_TOTAL);
            }
            if (Config.MALNOURISHED_ATTACK_DAMAGE.get() != 0d) {
                addAttributeModifier(Attributes.ATTACK_DAMAGE, "f812363c-2e1b-11ec-8d3d-0242ac130004", Config.MALNOURISHED_ATTACK_DAMAGE.get(), AttributeModifier.Operation.ADDITION);
            }
            if (Config.MALNOURISHED_ATTACK_KNOCKBACK.get() != 0d) {
                addAttributeModifier(Attributes.ATTACK_KNOCKBACK, "f812363c-2e1b-11ec-8d3d-0242ac130004", Config.MALNOURISHED_ATTACK_KNOCKBACK.get(), AttributeModifier.Operation.ADDITION);
            }
            if (Config.MALNOURISHED_ATTACK_SPEED.get() != 0d) {
                addAttributeModifier(Attributes.ATTACK_SPEED, "f812363c-2e1b-11ec-8d3d-0242ac130004", Config.MALNOURISHED_ATTACK_SPEED.get(), AttributeModifier.Operation.MULTIPLY_TOTAL);
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
                addAttributeModifier(Attributes.MAX_HEALTH, "f812307e-2e1b-11ec-8d3d-0242ac130005", Config.ENGORGED_MAX_HEALTH.get(), AttributeModifier.Operation.ADDITION);
            }
            if (Config.ENGORGED_KNOCKBACK_RESISTANCE.get() != 0d) {
                addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, "f8123510-2e1b-11ec-8d3d-0242ac130005", Config.ENGORGED_KNOCKBACK_RESISTANCE.get(), AttributeModifier.Operation.ADDITION);
            }
            if (Config.ENGORGED_MOVEMENT_SPEED.get() != 0d) {
                addAttributeModifier(Attributes.MOVEMENT_SPEED, "f812363c-2e1b-11ec-8d3d-0242ac130005", Config.ENGORGED_MOVEMENT_SPEED.get(), AttributeModifier.Operation.MULTIPLY_TOTAL);
            }
            if (Config.ENGORGED_ATTACK_DAMAGE.get() != 0d) {
                addAttributeModifier(Attributes.ATTACK_DAMAGE, "f812363c-2e1b-11ec-8d3d-0242ac130005", Config.ENGORGED_ATTACK_DAMAGE.get(), AttributeModifier.Operation.ADDITION);
            }
            if (Config.ENGORGED_ATTACK_KNOCKBACK.get() != 0d) {
                addAttributeModifier(Attributes.ATTACK_KNOCKBACK, "f812363c-2e1b-11ec-8d3d-0242ac130005", Config.ENGORGED_ATTACK_KNOCKBACK.get(), AttributeModifier.Operation.ADDITION);
            }
            if (Config.ENGORGED_ATTACK_SPEED.get() != 0d) {
                addAttributeModifier(Attributes.ATTACK_SPEED, "f812363c-2e1b-11ec-8d3d-0242ac130005", Config.ENGORGED_ATTACK_SPEED.get(), AttributeModifier.Operation.MULTIPLY_TOTAL);
            }
        }
    }

    public static void resetPlayerEffects(Player player){
        if (player.getEffect(NutritionalBalance.NOURISHED_EFFECT.get()) != null)
            player.removeEffect(NutritionalBalance.NOURISHED_EFFECT.get());
        if (player.getEffect(NutritionalBalance.MALNOURISHED_EFFECT.get()) != null)
            player.removeEffect(NutritionalBalance.MALNOURISHED_EFFECT.get());
        if (player.getEffect(NutritionalBalance.ENGORGED_EFFECT.get()) != null)
            player.removeEffect(NutritionalBalance.ENGORGED_EFFECT.get());

    }

}
