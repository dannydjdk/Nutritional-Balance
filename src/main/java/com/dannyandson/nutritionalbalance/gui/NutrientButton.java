package com.dannyandson.nutritionalbalance.gui;

import com.dannyandson.nutritionalbalance.Config;
import com.dannyandson.nutritionalbalance.NutritionalBalance;
import com.dannyandson.nutritionalbalance.network.GUITrigger;
import com.dannyandson.nutritionalbalance.network.ModNetworkHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class NutrientButton extends AbstractWidget {

    private ResourceLocation buttonGUI = new ResourceLocation(NutritionalBalance.MODID,"textures/gui/nutrient_button.png");
    private ResourceLocation buttonGUIHover = new ResourceLocation(NutritionalBalance.MODID,"textures/gui/nutrient_button_hover.png");
    private InventoryScreen gui;

    public NutrientButton(InventoryScreen gui, Component title) {
        super(gui.getGuiLeft(),gui.getGuiTop(), 0, 0, title);
        this.gui=gui;
        updateLocation();
    }

    //need to update the location on render to account for recipe book toggling
    private void updateLocation()
    {
        this.x = gui.getGuiLeft() + Config.NUTRIENT_BUTTON_X.get();
        this.y = gui.getGuiTop() + Config.NUTRIENT_BUTTON_Y.get();
        this.width = 20;
        this.height =18;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        ModNetworkHandler.sendToServer(new GUITrigger());
    }

    @Override
    public void renderButton(PoseStack matrixStack, int mouseX, int mouseY, float partialTick) {
        if (visible) {
            updateLocation();
            if (mouseX>x && mouseX<x+width && mouseY>y && mouseY<y+height) {
                RenderSystem.setShaderTexture(0, buttonGUIHover);
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                Minecraft.getInstance().getTextureManager().bindForSetup(buttonGUIHover);
            }
            else {
                RenderSystem.setShaderTexture(0, buttonGUI);
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                Minecraft.getInstance().getTextureManager().bindForSetup(buttonGUI);
            }
            this.blit(matrixStack,x, y, 0, 0, width, height);
        }

    }

    @Override
    public void updateNarration(NarrationElementOutput p_169152_) {

    }
}
