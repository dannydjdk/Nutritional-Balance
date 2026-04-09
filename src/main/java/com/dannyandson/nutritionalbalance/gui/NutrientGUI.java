package com.dannyandson.nutritionalbalance.gui;

import com.dannyandson.nutritionalbalance.NutritionalBalance;
import com.dannyandson.nutritionalbalance.keybinding.ModKeyBindings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class NutrientGUI extends Screen implements INutrientGUIScreen {

    private static final int WIDTH = 250;
    private static final int HEIGHT = 155;
    private static final Identifier GUI = Identifier.fromNamespaceAndPath(NutritionalBalance.MODID, "textures/gui/nutrient_gui.png");

    public NutrientGUI() {
        super(Component.translatable("nutritionalbalance.nutrients"));
    }

    @Override
    protected void init() {
        NutrientGUIHelper.init(this, WIDTH, HEIGHT);
        int relX = (this.width - WIDTH) / 2;
        int relY = (this.height - HEIGHT) / 2;
        addRenderableWidget(ModWidget.buildButton(relX + 85, relY + 128, 80, 20, Component.translatable("nutritionalbalance.close"), button -> close()));
    }

    public ModWidget addModWidget(ModWidget modWidget) { return addRenderableWidget(modWidget); }
    @Override public int getWidth() { return this.width; }
    @Override public int getHeight() { return this.height; }

    @Override
    public boolean keyPressed(KeyEvent event) {
        int keyCode = event.key();
        if (keyCode == ModKeyBindings.keyBindings.get("nutrientgui").getKey().getValue() ||
                keyCode == minecraft.options.keyInventory.getKey().getValue()) {
            close();
            return true;
        }
        return super.keyPressed(event);
    }

    private void close() { minecraft.setScreen(null); }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        guiGraphics.fill(0, 0, this.width, this.height, 0xC0101010);
        int relX = (this.width - WIDTH) / 2;
        int relY = (this.height - HEIGHT) / 2;
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, GUI, relX, relY, 0, 0, WIDTH, HEIGHT, 256, 256);
    }

    public static void open() { Minecraft.getInstance().setScreen(new NutrientGUI()); }
}
