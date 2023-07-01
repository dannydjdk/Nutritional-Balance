package com.dannyandson.nutritionalbalance.gui;

import com.dannyandson.nutritionalbalance.NutritionalBalance;
import com.dannyandson.nutritionalbalance.keybinding.ModKeyBindings;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class NutrientGUI extends Screen implements INutrientGUIScreen{

    private static final int WIDTH = 250;
    private static final int HEIGHT = 155;


    private ResourceLocation GUI = new ResourceLocation(NutritionalBalance.MODID, "textures/gui/nutrient_gui.png");


    public NutrientGUI() {
        super(Component.translatable("nutritionalbalance.nutrients"));
    }

    @Override
    protected void init() {

        NutrientGUIHelper.init(this,WIDTH,HEIGHT);
        int relX = (this.width - WIDTH) / 2;
        int relY = (this.height - HEIGHT) / 2;
        addRenderableWidget(ModWidget.buildButton(relX + 85, relY + 128, 80, 20, Component.translatable("nutritionalbalance.close"), button -> close()));

    }

    public ModWidget addModWidget(ModWidget modWidget){
        return addRenderableWidget(modWidget);
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean keyPressed(int p_96552_, int p_96553_, int p_96554_) {
        if (
                p_96552_== ModKeyBindings.keyBindings.get("nutrientgui").getKey().getValue() ||
                        p_96552_ == minecraft.options.keyInventory.getKey().getValue()
        ){
            close();
            return true;
        }
        return super.keyPressed(p_96552_, p_96553_, p_96554_);
    }

    private void close() {
        minecraft.setScreen(null);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.setShaderTexture(0, GUI);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bindForSetup(GUI);
        int relX = (this.width - WIDTH) / 2;
        int relY = (this.height - HEIGHT) / 2;

        guiGraphics.blit(GUI,relX, relY, 0, 0, WIDTH, HEIGHT);

        super.render(guiGraphics,mouseX, mouseY, partialTicks);
    }

    public static void open() {
        Minecraft.getInstance().setScreen(new NutrientGUI());
    }
}
