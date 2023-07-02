package com.dannyandson.nutritionalbalance.lunchbox;

import com.dannyandson.nutritionalbalance.Config;
import com.dannyandson.nutritionalbalance.NutritionalBalance;
import com.dannyandson.nutritionalbalance.gui.INutrientGUIScreen;
import com.dannyandson.nutritionalbalance.gui.ModWidget;
import com.dannyandson.nutritionalbalance.gui.NutrientGUIHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class LunchBoxScreen extends AbstractContainerScreen<LunchBoxMenu> implements MenuAccess<LunchBoxMenu>, INutrientGUIScreen {

    public static final ResourceLocation GUI = new ResourceLocation(NutritionalBalance.MODID, "textures/gui/lunchbox_gui.png");

    private final LunchBoxMenu lunchBoxMenu;
    private final ModWidget[] widgets = new ModWidget[Config.LUNCHBOX_SLOT_COUNT.get()];

    public LunchBoxScreen(LunchBoxMenu lunchBoxMenu, Inventory playerInventory, Component title){
        super(lunchBoxMenu,playerInventory,title);
        this.lunchBoxMenu = lunchBoxMenu;
        this.imageWidth = 250;
        this.imageHeight = 250;
        this.inventoryLabelY = this.topPos + 156;
        this.inventoryLabelX = this.leftPos + 46;
        this.titleLabelY = topPos+111;
        this.titleLabelX = this.leftPos + 46;
    }

    @Override
    protected void init() {
        super.init();

        NutrientGUIHelper.init(this,this.imageWidth, this.imageHeight/2, -this.imageHeight/4);

        for (int i = 0 ; i< Config.LUNCHBOX_SLOT_COUNT.get() ; i++){
            int finalI = i;
            addRenderableWidget(new Button(leftPos+46+(i*18),topPos+123,18,10,Component.nullToEmpty(" "), button -> toggleActive(finalI)));
            addRenderableWidget(new ModWidget(leftPos+46+(i*18),topPos+133,17,17,0xFF373737));
            addRenderableWidget(new ModWidget(leftPos+47+(i*18),topPos+134,17,17,0xFFFEFEFE));
            addRenderableWidget(new ModWidget(leftPos+47+(i*18),topPos+134,16,16,0xFF8B8B8B));

        }
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        if (this.widgets[0]==null)
            renderActiveOverlays();
    }

    @Override
    public void resize(@NotNull Minecraft p_96575_, int p_96576_, int p_96577_) {
        super.resize(p_96575_, p_96576_, p_96577_);
        renderActiveOverlays();
    }

    @Override
    public void render(@NotNull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(@NotNull PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI);

        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        this.blit(poseStack,x, y, 0, 0, this.imageWidth, this.imageHeight);

    }

    private void toggleActive(int slot){
        lunchBoxMenu.setActiveSlot(slot);
        renderActiveOverlays();
    }

    private void renderActiveOverlays(){
        for (int i = 0 ; i<Config.LUNCHBOX_SLOT_COUNT.get() ; i++){
            if (widgets[i] != null)
                removeWidget(widgets[i]);
            int color = 0xFFFFFFFF;
            String slotItem =  lunchBoxMenu.slots.get(i).getItem().getDescriptionId();
            if (lunchBoxMenu.getLunchBoxItemStack().getItem() instanceof LunchBoxItem lunchBoxItem) {
                if(lunchBoxItem.getActiveFoodItemStack(lunchBoxMenu.getLunchBoxItemStack()) != null){
                    if (lunchBoxItem.getActiveFoodItemStack(lunchBoxMenu.getLunchBoxItemStack()).getDescriptionId().equals(slotItem))
                        color = 0xFF00FF00;
                }
            }
            widgets[i] = new ModWidget(leftPos+47+i*18, topPos+124, 16, 8, color);
            addRenderableWidget(widgets[i]);
        }
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
    public ModWidget addModWidget(ModWidget modWidget) {
        return addRenderableWidget(modWidget);
    }
}
