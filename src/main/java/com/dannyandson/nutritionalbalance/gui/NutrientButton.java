package com.dannyandson.nutritionalbalance.gui;

import com.dannyandson.nutritionalbalance.Config;
import com.dannyandson.nutritionalbalance.NutritionalBalance;
import com.dannyandson.nutritionalbalance.network.GUITrigger;
import com.dannyandson.nutritionalbalance.network.ModNetworkHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class NutrientButton extends AbstractWidget {

    private ResourceLocation buttonGUI = ResourceLocation.fromNamespaceAndPath(NutritionalBalance.MODID, "textures/gui/nutrient_button.png");
    private ResourceLocation buttonGUIHover = ResourceLocation.fromNamespaceAndPath(NutritionalBalance.MODID, "textures/gui/nutrient_button_hover.png");
    private InventoryScreen gui;

    public NutrientButton(InventoryScreen gui, Component title) {
        super(gui.getGuiLeft(), gui.getGuiTop(), 0, 0, title);
        this.gui = gui;
        updateLocation();
    }

    private void updateLocation() {
        this.setX(gui.getGuiLeft() + Config.NUTRIENT_BUTTON_X.get());
        this.setY(gui.getGuiTop() + Config.NUTRIENT_BUTTON_Y.get());
        this.width = 20;
        this.height = 18;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        ModNetworkHandler.sendToServer(new GUITrigger());
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (visible) {
            updateLocation();
            ResourceLocation texture;
            if (mouseX > getX() && mouseX < getX() + width && mouseY > getY() && mouseY < getY() + height) {
                texture = buttonGUIHover;
            } else {
                texture = buttonGUI;
            }
            guiGraphics.blit(texture, getX(), getY(), 0, 0, width, height, 256, 256);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput p_259858_) {
    }
}