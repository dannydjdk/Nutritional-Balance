package com.dannyandson.nutritionalbalance.gui;

import com.dannyandson.nutritionalbalance.Config;
import com.dannyandson.nutritionalbalance.NutritionalBalance;
import com.dannyandson.nutritionalbalance.network.GUITrigger;
import com.dannyandson.nutritionalbalance.network.ModNetworkHandler;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class NutrientButton extends Widget {

    private ResourceLocation buttonGUI = new ResourceLocation(NutritionalBalance.MODID,"textures/gui/nutrient_button.png");
    private ResourceLocation buttonGUIHover = new ResourceLocation(NutritionalBalance.MODID,"textures/gui/nutrient_button_hover.png");
    private InventoryScreen gui;

    public NutrientButton(InventoryScreen gui, ITextComponent title) {
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
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTick) {
        if (visible) {
            updateLocation();
            RenderSystem.blendColor(1.0F, 1.0F, 1.0F, 1.0F);
            if (mouseX>x && mouseX<x+width && mouseY>y && mouseY<y+height)
                Minecraft.getInstance().getTextureManager().bind(buttonGUIHover);
            else
                Minecraft.getInstance().getTextureManager().bind(buttonGUI);
            this.blit(matrixStack,x, y, 0, 0, width, height);
        }

    }
}
