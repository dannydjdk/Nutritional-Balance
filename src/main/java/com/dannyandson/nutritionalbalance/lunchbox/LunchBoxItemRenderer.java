package com.dannyandson.nutritionalbalance.lunchbox;

import com.dannyandson.nutritionalbalance.NutritionalBalance;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public class LunchBoxItemRenderer extends BlockEntityWithoutLevelRenderer {

    private ResourceLocation LUNCHBOX = new ResourceLocation(NutritionalBalance.MODID, "item/lunchbox");


    public LunchBoxItemRenderer(BlockEntityRenderDispatcher p_172550_, EntityModelSet p_172551_) {
        super(p_172550_, p_172551_);
    }

    @Override
    public void renderByItem(ItemStack stack, @NotNull ItemDisplayContext context, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        poseStack.pushPose();
        TextureAtlasSprite sprite =  Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(LUNCHBOX);
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.translucent());
        Matrix4f matrix4f = poseStack.last().pose();

        poseStack.mulPose(Axis.ZP.rotationDegrees(180));
        poseStack.translate(-1,-1,.5);
        poseStack.mulPose(Axis.XP.rotationDegrees(0));

        buffer.vertex(matrix4f, 0,0,0).color(0xFFFFFFFF).uv(sprite.getU0(), sprite.getV0()).uv2(combinedLight).normal(1,0,0).endVertex();
        buffer.vertex(matrix4f, 0,1,0).color(0xFFFFFFFF).uv(sprite.getU0(), sprite.getV1()).uv2(combinedLight).normal(1,0,0).endVertex();
        buffer.vertex(matrix4f, 1,1,0).color(0xFFFFFFFF).uv(sprite.getU1(), sprite.getV1()).uv2(combinedLight).normal(1,0,0).endVertex();
        buffer.vertex(matrix4f, 1,0,0).color(0xFFFFFFFF).uv(sprite.getU1(), sprite.getV0()).uv2(combinedLight).normal(1,0,0).endVertex();

        poseStack.mulPose(Axis.YP.rotationDegrees(180));
        poseStack.translate(-1,0,0);

        buffer.vertex(matrix4f, 0,0,0).color(0xFFFFFFFF).uv(sprite.getU0(), sprite.getV0()).uv2(combinedLight).normal(1,0,0).endVertex();
        buffer.vertex(matrix4f, 0,1,0).color(0xFFFFFFFF).uv(sprite.getU0(), sprite.getV1()).uv2(combinedLight).normal(1,0,0).endVertex();
        buffer.vertex(matrix4f, 1,1,0).color(0xFFFFFFFF).uv(sprite.getU1(), sprite.getV1()).uv2(combinedLight).normal(1,0,0).endVertex();
        buffer.vertex(matrix4f, 1,0,0).color(0xFFFFFFFF).uv(sprite.getU1(), sprite.getV0()).uv2(combinedLight).normal(1,0,0).endVertex();


        if (stack.getItem() instanceof LunchBoxItem lunchBoxItem) {
            ItemStack activeStack = lunchBoxItem.getActiveFoodItemStack(stack);
            if (activeStack!=null) {
                ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

                poseStack.mulPose(Axis.ZP.rotationDegrees(180));
                poseStack.translate(-.5,-.65,0);
                poseStack.scale(.6f,.6f,.6f);
                //poseStack.mulPose(Axis.XP.rotationDegrees(45));

                itemRenderer.renderStatic(activeStack, ItemDisplayContext.FIXED, combinedLight, combinedOverlay, poseStack, bufferSource, Minecraft.getInstance().level, 0);
            }
        }
        poseStack.popPose();

    }
}
