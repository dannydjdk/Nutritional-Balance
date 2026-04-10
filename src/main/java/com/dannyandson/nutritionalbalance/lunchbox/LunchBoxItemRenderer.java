package com.dannyandson.nutritionalbalance.lunchbox;

import com.dannyandson.nutritionalbalance.setup.Registration;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public record LunchBoxItemRenderer() implements SpecialModelRenderer<ItemStack> {

    private static final ItemStackRenderState BODY_RENDER_STATE = new ItemStackRenderState();
    private static final ItemStackRenderState FOOD_RENDER_STATE = new ItemStackRenderState();

    @Nullable
    @Override
    public ItemStack extractArgument(ItemStack stack) {
        return stack;
    }

    @Override
    public void submit(ItemStack stack, PoseStack poseStack,
                       SubmitNodeCollector collector, int lightCoords, int overlayCoords,
                       boolean hasFoil, int outlineColor) {

        var resolver = Minecraft.getInstance().getItemModelResolver();

        // === Render the lunchbox body via hidden visual item ===
        ItemStack visualStack = Registration.LUNCHBOX_VISUAL.get().getDefaultInstance();
        resolver.updateForTopItem(BODY_RENDER_STATE, visualStack,
                ItemDisplayContext.NONE,
                Minecraft.getInstance().level, null, 0);

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        BODY_RENDER_STATE.submit(poseStack, collector, lightCoords, overlayCoords, outlineColor);
        poseStack.popPose();

        // === Render the active food item on both faces ===
        if (stack.getItem() instanceof LunchBoxItem lunchBoxItem) {
            ItemStack activeStack = lunchBoxItem.getActiveFoodItemStack(stack);
            if (activeStack != null && !activeStack.isEmpty()) {

                resolver.updateForTopItem(FOOD_RENDER_STATE, activeStack,
                        ItemDisplayContext.FIXED,
                        Minecraft.getInstance().level, null, 0);

                // Front face
                poseStack.pushPose();
                poseStack.translate(0.5, 0.35, 0.55);
                poseStack.scale(0.5f, 0.5f, 0.5f);
                FOOD_RENDER_STATE.submit(poseStack, collector, lightCoords, overlayCoords, outlineColor);
                poseStack.popPose();

                // Back face
                poseStack.pushPose();
                poseStack.translate(0.5, 0.35, 0.45);
                poseStack.mulPose(Axis.YP.rotationDegrees(180));
                poseStack.scale(0.5f, 0.5f, 0.5f);
                FOOD_RENDER_STATE.submit(poseStack, collector, lightCoords, overlayCoords, outlineColor);
                poseStack.popPose();
            }
        }
    }

    @Override
    public void getExtents(Consumer<Vector3fc> consumer) {
        consumer.accept(new Vector3f(0, 0, 0));
        consumer.accept(new Vector3f(1, 1, 1));
    }

    public record Unbaked() implements SpecialModelRenderer.Unbaked {
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(Unbaked::new);

        @Override
        public MapCodec<Unbaked> type() { return MAP_CODEC; }

        @Override
        public @Nullable SpecialModelRenderer bake(BakingContext bakingContext) {
            return new LunchBoxItemRenderer();
        }
    }
}