package com.dannyandson.nutritionalbalance.effects;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

public class ModMobAffects {

    public static class Nourished extends Effect {
        private static int color = 3949738;
        public Nourished() {
            super(EffectType.BENEFICIAL, color);
            addAttributeModifier(Attributes.MOVEMENT_SPEED, "91AEAA56-376B-4498-935B-2F7F68070635", 0.2, AttributeModifier.Operation.MULTIPLY_TOTAL);
            addAttributeModifier(Attributes.ATTACK_SPEED, "AF8B6E3F-3328-4C0A-AA36-5BA2BB9DBEF3", 0.1, AttributeModifier.Operation.MULTIPLY_TOTAL);
            addAttributeModifier(Attributes.ATTACK_DAMAGE, "648D7064-6A60-4F59-8ABE-C2C23A6DD7A9", 0.2, AttributeModifier.Operation.MULTIPLY_TOTAL);
        }
    }
    public static class MalNourished extends Effect{
        private static int color = 11546150;
        public MalNourished() {
            super(EffectType.HARMFUL, color);
            addAttributeModifier(Attributes.MOVEMENT_SPEED, "91AEAA56-376B-4498-935B-2F7F68070635",  -0.15, AttributeModifier.Operation.MULTIPLY_TOTAL);
            addAttributeModifier(Attributes.ATTACK_DAMAGE, "648D7064-6A60-4F59-8ABE-C2C23A6DD7A9", -0.15, AttributeModifier.Operation.MULTIPLY_TOTAL);
        }
    }    public static class Engorged extends Effect{
        private static int color = 11546150;
        public Engorged() {
            super(EffectType.HARMFUL, color);
            addAttributeModifier(Attributes.MOVEMENT_SPEED, "91AEAA56-376B-4498-935B-2F7F68070635", -0.15, AttributeModifier.Operation.MULTIPLY_TOTAL);
            addAttributeModifier(Attributes.ATTACK_SPEED, "AF8B6E3F-3328-4C0A-AA36-5BA2BB9DBEF3", -0.1, AttributeModifier.Operation.MULTIPLY_TOTAL);
        }
    }


}
