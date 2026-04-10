package com.dannyandson.nutritionalbalance.lunchbox;

import com.dannyandson.nutritionalbalance.Config;
import com.dannyandson.nutritionalbalance.NutritionalBalance;
import com.dannyandson.nutritionalbalance.gui.INutrientGUIScreen;
import com.dannyandson.nutritionalbalance.gui.ModWidget;
import com.dannyandson.nutritionalbalance.gui.NutrientGUIHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class LunchBoxScreen extends AbstractContainerScreen<LunchBoxMenu> implements MenuAccess<LunchBoxMenu>, INutrientGUIScreen {

    public static final Identifier GUI = Identifier.fromNamespaceAndPath(NutritionalBalance.MODID, "textures/gui/lunchbox_gui.png");
    public static final Identifier GUI_SLOT = Identifier.fromNamespaceAndPath(NutritionalBalance.MODID, "textures/gui/slot.png");

    private final LunchBoxMenu lunchBoxMenu;
    private final ModWidget[] widgets = new ModWidget[Config.LUNCHBOX_SLOT_COUNT.get()];

    public LunchBoxScreen(LunchBoxMenu lunchBoxMenu, Inventory playerInventory, Component title) {
        super(lunchBoxMenu, playerInventory, title, 250, 250);
        this.lunchBoxMenu = lunchBoxMenu;
    }

    @Override
    protected void init() {
        super.init();
        NutrientGUIHelper.init(this, this.imageWidth, this.imageHeight / 2, -this.imageHeight / 4);
        for (int i = 0; i < Config.LUNCHBOX_SLOT_COUNT.get(); i++) {
            int finalI = i;
            addRenderableWidget(ModWidget.buildButton(leftPos + 46 + (i * 18), topPos + 123, 18, 10, Component.nullToEmpty(" "), button -> toggleActive(finalI)));
            // Use addRenderableOnly for purely visual slot textures — addRenderableWidget
            // would consume click events before the actual container slots underneath
            addRenderableOnly(new ModWidget(leftPos + 46 + (i * 18), topPos + 133, 18, 18, GUI_SLOT));
        }
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        if (this.widgets[0] == null)
            renderActiveOverlays();
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, GUI, x, y, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
    }

    private void toggleActive(int slot) {
        lunchBoxMenu.setActiveSlot(slot);
        renderActiveOverlays();
    }

    private void renderActiveOverlays() {
        for (int i = 0; i < Config.LUNCHBOX_SLOT_COUNT.get(); i++) {
            if (widgets[i] != null)
                removeWidget(widgets[i]);
            int color = 0xFFFFFFFF;
            String slotItem = lunchBoxMenu.slots.get(i).getItem().getItem().getDescriptionId();
            if (lunchBoxMenu.getLunchBoxItemStack().getItem() instanceof LunchBoxItem lunchBoxItem) {
                if (lunchBoxItem.getActiveFoodItemStack(lunchBoxMenu.getLunchBoxItemStack()) != null) {
                    if (lunchBoxItem.getActiveFoodItemStack(lunchBoxMenu.getLunchBoxItemStack()).getItem().getDescriptionId().equals(slotItem))
                        color = 0xFF00FF00;
                }
            }
            widgets[i] = new ModWidget(leftPos + 47 + i * 18, topPos + 124, 16, 8, color);
            // Active indicator overlays are also purely visual
            addRenderableOnly(widgets[i]);
        }
    }

    @Override
    public int getWidth() { return this.width; }
    @Override
    public int getHeight() { return this.height; }
    @Override
    public ModWidget addModWidget(ModWidget modWidget) { return addRenderableWidget(modWidget); }
}