package com.dannyandson.nutritionalbalance.lunchbox;

import com.dannyandson.nutritionalbalance.NutritionalBalance;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class LunchBoxItemRenderer extends BlockEntityWithoutLevelRenderer {

    public static final ModelResourceLocation LUNCHBOX_3D_MODEL = ModelResourceLocation.standalone(
            ResourceLocation.fromNamespaceAndPath(NutritionalBalance.MODID, "item/lunchbox_3d")
    );

    private BakedModel baseModel;

    public LunchBoxItemRenderer(BlockEntityRenderDispatcher p_172550_, EntityModelSet p_172551_) {
        super(p_172550_, p_172551_);
    }

    private BakedModel getBaseModel() {
        if (baseModel == null) {
            baseModel = Minecraft.getInstance().getModelManager().getModel(LUNCHBOX_3D_MODEL);
        }
        return baseModel;
    }

    @Override
    public void renderByItem(ItemStack stack, @NotNull ItemDisplayContext context, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        BakedModel model = getBaseModel();

        // Render the 3D lunchbox body.
        // Using NONE context avoids double-applying display transforms
        // (the game already applied lunchbox.json's transforms before calling this BEWLR).
        poseStack.pushPose();
        // item/generated geometry spans [0,1] in X and Y; shift to center at origin
        poseStack.translate(0.5, 0.5, 0.5);
        itemRenderer.render(stack, ItemDisplayContext.NONE, false, poseStack, bufferSource, combinedLight, combinedOverlay, model);
        poseStack.popPose();

        // Render the selected food item on both faces of the lunchbox
        if (stack.getItem() instanceof LunchBoxItem lunchBoxItem) {
            ItemStack activeStack = lunchBoxItem.getActiveFoodItemStack(stack);
            if (activeStack != null) {
                // In GUI, the BEWLR inherits 3D directional lighting from the lunchbox,
                // but food items normally use flat lighting. We must flush the batched
                // buffer before changing the lighting state, otherwise the state change
                // has no effect (vertices are drawn later when the buffer flushes).
                boolean isGui = (context == ItemDisplayContext.GUI);
                if (isGui && bufferSource instanceof MultiBufferSource.BufferSource immediate) {
                    immediate.endBatch();
                    com.mojang.blaze3d.platform.Lighting.setupForFlatItems();
                }

                // Front face
                poseStack.pushPose();
                poseStack.translate(0.5, 0.35, 0.55);
                poseStack.scale(0.5f, 0.5f, 0.5f);
                itemRenderer.renderStatic(activeStack, ItemDisplayContext.FIXED, combinedLight, combinedOverlay, poseStack, bufferSource, Minecraft.getInstance().level, 0);
                poseStack.popPose();

                // Back face
                poseStack.pushPose();
                poseStack.translate(0.5, 0.35, 0.45);
                poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(180));
                poseStack.scale(0.5f, 0.5f, 0.5f);
                itemRenderer.renderStatic(activeStack, ItemDisplayContext.FIXED, combinedLight, combinedOverlay, poseStack, bufferSource, Minecraft.getInstance().level, 0);
                poseStack.popPose();

                // Flush food vertices under flat lighting, then restore 3D lighting
                if (isGui && bufferSource instanceof MultiBufferSource.BufferSource immediate2) {
                    immediate2.endBatch();
                    com.mojang.blaze3d.platform.Lighting.setupFor3DItems();
                }
            }
        }
    }
}