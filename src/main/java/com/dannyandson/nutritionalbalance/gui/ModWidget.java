package com.dannyandson.nutritionalbalance.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class ModWidget extends AbstractWidget {

    public enum HAlignment { LEFT, CENTER, RIGHT }
    public enum VAlignment { TOP, MIDDLE, BOTTOM }

    private HAlignment halignment = HAlignment.LEFT;
    private VAlignment valignment = VAlignment.TOP;
    private float scale = 1.0f;
    private int color;
    private int bgcolor=-1;
    private int textWidth;
    private int textHeight;
    private Component toolTipTextComponent;
    private Identifier texture;

    public ModWidget(int x, int y, int width, int height, Component title, int textColor, int bgColor) {
        super(x, y, width, height, title);
        this.color=textColor;
        this.bgcolor=bgColor;
        if (title.getString().length()>0) {
            this.textWidth = Minecraft.getInstance().font.width(getMessage());
            this.textHeight = Minecraft.getInstance().font.lineHeight;
        }
    }
    public ModWidget(int x, int y, int width, int height, Component title, int textColor) { this(x,y,width,height,title,textColor,-1); }
    public ModWidget(int x, int y, int width, int height, Component title) { this(x,y,width,height,title,0xFFFFFFFF,-1); }
    public ModWidget(int x, int y, int width, int height, int bgColor) { this(x,y,width,height,Component.nullToEmpty(""),0xFFFFFFFF,bgColor); }
    public ModWidget(int x, int y, int width, int height, Identifier texture) {
        this(x,y,width,height,Component.nullToEmpty(""),0xFFFFFFFF,-1);
        this.texture = texture;
    }

    public ModWidget setTextHAlignment(HAlignment alignment) { this.halignment = alignment; return this; }
    public ModWidget setTextVAlignment(VAlignment alignment) { this.valignment = alignment; return this; }
    public ModWidget setToolTip(Component textComponent) { this.toolTipTextComponent = textComponent; return this; }

    @Override
    public void onClick(MouseButtonEvent event, boolean doubleClick) { }

    @Override
    public void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (visible) {
            if (texture != null) {
                guiGraphics.blit(RenderPipelines.GUI_TEXTURED, texture, this.getX(), this.getY(), 0, 0, width, height, width, height);
            } else {
                int drawX, drawY;
                Font fr = Minecraft.getInstance().font;

                switch (halignment) {
                    case CENTER: drawX = getX() + (int)((width - textWidth) / 2 * scale); break;
                    case RIGHT: drawX = getX() + (int)((width - textWidth) * scale); break;
                    default: drawX = getX(); break;
                }
                switch (valignment) {
                    case MIDDLE: drawY = getY() + (int)((height - textHeight) / 2 * scale); break;
                    case BOTTOM: drawY = getY() + (int)((height - textHeight) * scale); break;
                    default: drawY = getY(); break;
                }

                // In 26.1, GuiGraphicsExtractor.pose() returns Matrix3x2fStack, not PoseStack.
                // Scale transforms are not supported. Draw text directly (scale=1.0 only for now).
                guiGraphics.text(fr, getMessage().getVisualOrderText(), drawX, drawY, this.color, false);
            }
            if (bgcolor != -1) {
                guiGraphics.fill(getX(), getY(), getX() + width, getY() + height, bgcolor);
            }
            if (this.toolTipTextComponent != null && mouseX >= getX() && mouseX <= getX() + width && mouseY >= getY() && mouseY <= getY() + height)
                this.renderHoverToolTip(guiGraphics, mouseX, mouseY);
        }
    }

    public void renderHoverToolTip(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        if (this.toolTipTextComponent != null) {
            Font fr = Minecraft.getInstance().font;
            int width = fr.width(this.toolTipTextComponent);
            int height = fr.lineHeight;
            guiGraphics.fill(mouseX, mouseY+10, mouseX + width + 4, mouseY +10 + height + 4, 0xCC000000);
            guiGraphics.fill(mouseX + 1, mouseY + 11, mouseX + width + 3, mouseY + 10 + height + 3, 0x66EEEEEE);
            guiGraphics.text(fr,this.toolTipTextComponent.getVisualOrderText(), mouseX + 3, mouseY + 13, 0xFFFEFEFE);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput p_259858_) { }

    public static Button buildButton(Integer xPos, Integer yPos, Integer width, Integer height, Component component, Button.OnPress onPress){
        return Button.builder(component,onPress).pos(xPos, yPos).size(width, height).build();
    }
}
