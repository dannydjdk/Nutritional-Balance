package com.dannyandson.nutritionalbalance.lunchbox;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public record LunchBoxItemRenderer() implements SpecialModelRenderer<ItemStack> {

    @Nullable
    @Override
    public ItemStack extractArgument(ItemStack stack) {
        return stack;
    }

    @Override
    public void submit(ItemStack stack, PoseStack poseStack,
                       SubmitNodeCollector collector, int lightCoords, int overlayCoords,
                       boolean hasFoil, int outlineColor) {
        // In 26.1, ItemRenderer is fully removed. Rendering a sub-item inside
        // SpecialModelRenderer.submit() requires the new ItemStackRenderState pipeline.
        // The base model (lunchbox.json) provides the 3D lunchbox appearance.
        // TODO: Implement food overlay rendering using ItemStackRenderState submission.
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
