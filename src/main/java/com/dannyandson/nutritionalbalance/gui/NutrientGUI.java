package com.dannyandson.nutritionalbalance.gui;

import com.dannyandson.nutritionalbalance.Config;
import com.dannyandson.nutritionalbalance.NutritionalBalance;
import com.dannyandson.nutritionalbalance.capabilities.CapabilityNutritionalBalancePlayer;
import com.dannyandson.nutritionalbalance.api.INutritionalBalancePlayer;
import com.dannyandson.nutritionalbalance.api.IPlayerNutrient;
import com.dannyandson.nutritionalbalance.keybinding.ModKeyBindings;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;

public class NutrientGUI extends Screen {

    private static final int WIDTH = 250;
    private static final int HEIGHT = 155;

    private INutritionalBalancePlayer inutritionalbalancePlayer;

    private ResourceLocation GUI = new ResourceLocation(NutritionalBalance.MODID, "textures/gui/nutrient_gui.png");


    public NutrientGUI() {
        super(new TranslationTextComponent("nutritionalbalance.nutrients"));
    }

    @Override
    protected void init() {
        //max height of columns (not including border)
        int maxColumnHeight = 7;
        //width of labels
        int labelWidth = 50;
        //width of columns (not including border)
        int columnWidth = this.WIDTH - labelWidth - 30;
        //colors (in hex aRGB) - consider making these configurable
        int colorColumnBackground = 0x00000000;
        int colorNutrientValueFill = 0xFF0033FF;
        int colorTargetIndicator = 0x9900FF00;
        int colorUnsafeIndicator = 0x99FF0000;

        int relX = (this.width - WIDTH) / 2;
        int relY = (this.height - HEIGHT) / 2;

        this.minecraft.player.getCapability(CapabilityNutritionalBalancePlayer.HEALTHY_DIET_PLAYER_CAPABILITY).ifPresent(inutritionalbalancePlayer -> {
            this.inutritionalbalancePlayer = inutritionalbalancePlayer;
        });


        addButton(new Button(relX + 85, relY + 128, 80, 20, new TranslationTextComponent("nutritionalbalance.close"), button -> close()));
        addButton(new ModWidget(relX,relY+5,WIDTH,10, new TranslationTextComponent("nutritionalbalance.nutrients"),0xFF000000).setTextHAlignment(ModWidget.HAlignment.CENTER).setTextVAlignment(ModWidget.VAlignment.TOP));

        TranslationTextComponent message;
        int messageColor;
        if (inutritionalbalancePlayer.getCachedStatus()== IPlayerNutrient.NutrientStatus.ON_TARGET){
            message = new TranslationTextComponent("nutritionalbalance.nutrientstatus.details.ON_TARGET");
            messageColor = 0xFF008800;
        } else if (inutritionalbalancePlayer.getCachedStatus()== IPlayerNutrient.NutrientStatus.ENGORGED){
            message = new TranslationTextComponent("nutritionalbalance.nutrientstatus.details.ENGORGED");
            messageColor = 0xFF880000;
        } else if (inutritionalbalancePlayer.getCachedStatus()== IPlayerNutrient.NutrientStatus.MALNOURISHED){
            message = new TranslationTextComponent("nutritionalbalance.nutrientstatus.details.MALNOURISHED");
            messageColor = 0xFF880000;
        } else {
            message = new TranslationTextComponent("nutritionalbalance.nutrientstatus.details.SAFE");
            messageColor = 0xFF004400;
        }

        addButton(new ModWidget(relX,relY+18,WIDTH,10, message,messageColor).setTextHAlignment(ModWidget.HAlignment.CENTER).setTextVAlignment(ModWidget.VAlignment.TOP));


        //addButton(new ModWidget(relX+205,relY+25,WIDTH,120, ITextComponent.nullToEmpty("Get all nutrients between the blue lines for a healthy diet.\nStay away from the red lines."),0xFF000000).setTextHAlignment(ModWidget.HAlignment.LEFT));

        //determine how much space is available to draw each nutrient column
        int nutrientcount = inutritionalbalancePlayer.getPlayerNutrients().size();
        if (nutrientcount>0) {
            int nBlocksOffset = 35;
            int nBlockHeight = (HEIGHT - nBlocksOffset - 30) / nutrientcount;
            //calculate possible height of columns (not including border)
            int columnHeight = Math.min(maxColumnHeight, nBlockHeight - 2);

            //columnBlockRelX stores the x value of the column block currently being drawn
            int columnBlockRelX = relX + 10;
            //columnDrawY stores the Y offset at which nutrient columns will be drawn (starting with bottom nutrient)
            int columnDrawY = relY +  nBlocksOffset + (nBlockHeight * (nutrientcount - 1));

            int engorged_bottom = Math.round((Config.NUTRIENT_ENGORGED.get().floatValue()) / Config.NUTRIENT_MAX.get().floatValue() * columnWidth);
            int target_top = Math.round((Config.NUTRIENT_TARGET_HIGH.get().floatValue()) / Config.NUTRIENT_MAX.get().floatValue() * columnWidth);
            int target_bottom = Math.round((Config.NUTRIENT_LOW_TARGET.get().floatValue()) / Config.NUTRIENT_MAX.get().floatValue() * columnWidth);
            int malnourished_top = Math.round((Config.NUTRIENT_MALNOURISHED.get().floatValue()) / Config.NUTRIENT_MAX.get().floatValue() * columnWidth);


            //loop through player's nutrients and draw the columns
            for (IPlayerNutrient playerNutrient : inutritionalbalancePlayer.getPlayerNutrients()) {
                //x value to begin drawing the column
                int columnRelY = columnDrawY;

                //percentage of players current nutrient level of max nutrient level
                int nutrientValueWidth = Math.round(playerNutrient.getValue() / Config.NUTRIENT_MAX.get().floatValue() * columnWidth);

                //Check if this a nonessential nutrient
                boolean nonEssentialNutrient = false;
                for (String badNutrientName : ((List<String>) Config.BAD_NUTRIENTS.get())) {
                    if (badNutrientName.equals(playerNutrient.getNutrientName()))
                        nonEssentialNutrient = true;
                }
                //Check if this is a good nutrient (one that does not cause engorgement)
                boolean goodNutrient = false;
                for (String goodNutrientName : ((List<String>) Config.GOOD_NUTRIENTS.get())) {
                    if (goodNutrientName.equals(playerNutrient.getNutrientName()))
                        goodNutrient = true;
                }

                //Draw columns and fill them with player nutrient values

                //draw border of nutrient column (solid block that will be overlain with background)
                addButton(new ModWidget(columnBlockRelX + labelWidth - 1, columnRelY-1, columnWidth + 2, columnHeight + 2, 0xFF000000));
                //draw background of nutrient column
                addButton(new ModWidget(columnBlockRelX + labelWidth, columnRelY, columnWidth, columnHeight, colorColumnBackground));
                //draw nutrient label
                addButton(new ModWidget(columnBlockRelX, columnDrawY, labelWidth, 10, new TranslationTextComponent("nutritionalbalance.nutrient." + playerNutrient.getNutrientName()), 0xFF000000));

                //fill nutrient column based player's level of this nutrient
                addButton(new ModWidget(columnBlockRelX + labelWidth, columnRelY, nutrientValueWidth, columnHeight, colorNutrientValueFill))
                        .setToolTip(ITextComponent.nullToEmpty( (Math.round(playerNutrient.getValue()*10f)/10f) + "NU"));

                //Draw lines and shading to indicate targets and "unsafe" ranges
                if (nonEssentialNutrient) {
                    //for nonessential nutrients draw target line at bottom
                    addButton(new ModWidget(columnBlockRelX + labelWidth, columnRelY-1, 1, columnHeight+2, colorTargetIndicator))
                            .setToolTip(ITextComponent.nullToEmpty("Target: 0NU"));
                } else {
                    //for essential nutrients (not nonessential)
                    //draw line at malnourishment border
                    addButton(new ModWidget(columnBlockRelX + labelWidth + malnourished_top, columnRelY-1, 1, columnHeight+2, colorUnsafeIndicator))
                        .setToolTip(ITextComponent.nullToEmpty("Malnourishment: " + Config.NUTRIENT_MALNOURISHED.get().toString() + "NU"));
                    //draw line at bottom of target
                    addButton(new ModWidget(columnBlockRelX + labelWidth + target_bottom, columnRelY-1, 1, columnHeight+2, colorTargetIndicator))
                            .setToolTip(ITextComponent.nullToEmpty("Target: " + Config.NUTRIENT_LOW_TARGET.get().toString() + "NU"));
                }
                if (goodNutrient)
                    //for good nutrients, draw target line at top
                    addButton(new ModWidget(columnBlockRelX + labelWidth + columnWidth, columnRelY-1, 1, columnHeight+2, colorTargetIndicator))
                            .setToolTip(ITextComponent.nullToEmpty("Target Cap: ∞NU"));
                else {
                    //for not good nutrients (but not necessarily bad)
                    //draw line at lower engorgement border
                    addButton(new ModWidget(columnBlockRelX + labelWidth + engorged_bottom, columnRelY-1, 1, columnHeight+1, colorUnsafeIndicator))
                            .setToolTip(ITextComponent.nullToEmpty("Engorgement: " + Config.NUTRIENT_ENGORGED.get().toString() + "NU"));
                    //draw line at top of target
                    addButton(new ModWidget(columnBlockRelX + labelWidth + target_top, columnRelY-1, 1, columnHeight+2, colorTargetIndicator))
                            .setToolTip(ITextComponent.nullToEmpty("Target Cap: " + Config.NUTRIENT_TARGET_HIGH.get().toString() + "NU"));
                }


                columnDrawY -= nBlockHeight;
            }

        }
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
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.blendColor(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bind(GUI);
        int relX = (this.width - WIDTH) / 2;
        int relY = (this.height - HEIGHT) / 2;
        this.blit(matrixStack,relX, relY, 0, 0, WIDTH, HEIGHT);

        super.render(matrixStack,mouseX, mouseY, partialTicks);
    }

    public static void open() {
        Minecraft.getInstance().setScreen(new NutrientGUI());
    }
}
