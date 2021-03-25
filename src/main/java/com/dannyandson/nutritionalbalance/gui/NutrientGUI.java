package com.dannyandson.nutritionalbalance.gui;

import com.dannyandson.nutritionalbalance.Config;
import com.dannyandson.nutritionalbalance.NutritionalBalance;
import com.dannyandson.nutritionalbalance.capabilities.CapabilityNutritionalBalancePlayer;
import com.dannyandson.nutritionalbalance.api.INutritionalBalancePlayer;
import com.dannyandson.nutritionalbalance.api.IPlayerNutrient;
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
        //max width of columns (not including border)
        int maxColumnWidth=10;
        //height of columns (not including border)
        int columnHeight = 95;
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


        addButton(new Button(relX + 85, relY + 130, 80, 20, new TranslationTextComponent("nutritionalbalance.close"), button -> close()));
        addButton(new ModWidget(relX,relY+5,WIDTH,10, new TranslationTextComponent("nutritionalbalance.nutrients"),0xFF000000).setTextHAlignment(ModWidget.HAlignment.CENTER).setTextVAlignment(ModWidget.VAlignment.TOP));


        //addButton(new ModWidget(relX+205,relY+25,WIDTH,120, ITextComponent.getTextComponentOrEmpty("Get all nutrients between the blue lines for a healthy diet.\nStay away from the red lines."),0xFF000000).setTextHAlignment(ModWidget.HAlignment.LEFT));

        //determine how much space is available to draw each nutrient column
        int nutrientcount = inutritionalbalancePlayer.getPlayerNutrients().size();
        if (nutrientcount>0) {
            int nBlockWidth = (WIDTH - 10) / nutrientcount;
            //calculate possible width of columns (not including border)
            int columnWidth = Math.min(maxColumnWidth, nBlockWidth - 2);

            //columnBlockRelX stores the x value of the column block currently being drawn
            int columnBlockRelX = relX + 5 + (nBlockWidth*(nutrientcount-1));
            //columnDrawY stores the Y offset at which nutrient columns will be drawn
            int columnDrawY = relY + 19;

            int engorged_bottom = Math.round((Config.NUTRIENT_MAX.get().floatValue() - Config.NUTRIENT_ENGORGED.get().floatValue()) / Config.NUTRIENT_MAX.get().floatValue() * columnHeight);
            int target_top = Math.round((Config.NUTRIENT_MAX.get().floatValue() - Config.NUTRIENT_TARGET_HIGH.get().floatValue()) / Config.NUTRIENT_MAX.get().floatValue() * columnHeight);
            int target_bottom = Math.round((Config.NUTRIENT_MAX.get().floatValue() - Config.NUTRIENT_LOW_TARGET.get().floatValue()) / Config.NUTRIENT_MAX.get().floatValue() * columnHeight);
            int malnourished_top = Math.round((Config.NUTRIENT_MAX.get().floatValue() - Config.NUTRIENT_MALNOURISHED.get().floatValue()) / Config.NUTRIENT_MAX.get().floatValue() * columnHeight);


            //loop through player's nutrients and draw the columns
            for (IPlayerNutrient playerNutrient : inutritionalbalancePlayer.getPlayerNutrients()) {
                //x value to begin drawing the column
                int columnRelX = columnBlockRelX + ((nBlockWidth - columnWidth) / 2);

                //percentage of players current nutrient level of max nutrient level
                int nutrientValueHeight = Math.round(playerNutrient.getValue() / Config.NUTRIENT_MAX.get().floatValue() * columnHeight);

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
                addButton(new ModWidget(columnRelX - 1, columnDrawY - 1, columnWidth + 2, columnHeight + 2, 0xFF000000));
                //draw background of nutrient column
                addButton(new ModWidget(columnRelX, columnDrawY, columnWidth, columnHeight, colorColumnBackground));
                //draw nutrient label
                addButton(new ModWidget(columnBlockRelX, columnDrawY + columnHeight + 2, nBlockWidth, 10, new TranslationTextComponent("nutritionalbalance.nutrient." + playerNutrient.getNutrientName()), 0xFF000000)
                        .setTextHAlignment(ModWidget.HAlignment.CENTER));
                //fill nutrient column based player's level of this nutrient
                addButton(new ModWidget(columnRelX, columnDrawY + columnHeight - nutrientValueHeight, columnWidth, nutrientValueHeight, colorNutrientValueFill))
                        .setToolTip(ITextComponent.getTextComponentOrEmpty( (Math.round(playerNutrient.getValue()*10f)/10f) + "NU"));

                //Draw lines and shading to indicate targets and "unsafe" ranges
                if (nonEssentialNutrient) {
                    //for nonessential nutrients draw target line at bottom
                    addButton(new ModWidget(columnRelX, columnDrawY + columnHeight - 1, columnWidth, 1, colorTargetIndicator))
                            .setToolTip(ITextComponent.getTextComponentOrEmpty("Target: 0NU"));
                } else {
                    //for essential nutrients (not nonessential)
                    //draw line at malnourishment border
                    addButton(new ModWidget(columnRelX, columnDrawY + malnourished_top, columnWidth, 1, colorUnsafeIndicator))
                        .setToolTip(ITextComponent.getTextComponentOrEmpty("Malnourishment: " + Config.NUTRIENT_MALNOURISHED.get().toString() + "NU"));
                    //draw line at bottom of target
                    addButton(new ModWidget(columnRelX, columnDrawY + target_bottom, columnWidth, 1, colorTargetIndicator))
                            .setToolTip(ITextComponent.getTextComponentOrEmpty("Target: " + Config.NUTRIENT_LOW_TARGET.get().toString() + "NU"));
                }
                if (goodNutrient)
                    //for good nutrients, draw target line at top
                    addButton(new ModWidget(columnRelX, columnDrawY, columnWidth, 1, colorTargetIndicator))
                            .setToolTip(ITextComponent.getTextComponentOrEmpty("Target Cap: ∞NU"));
                else {
                    //for not good nutrients (but not necessarily bad)
                    //draw line at lower engorgement border
                    addButton(new ModWidget(columnRelX, columnDrawY + engorged_bottom, columnWidth, 1, colorUnsafeIndicator))
                            .setToolTip(ITextComponent.getTextComponentOrEmpty("Engorgement: " + Config.NUTRIENT_ENGORGED.get().toString() + "NU"));
                    //draw line at top of target
                    addButton(new ModWidget(columnRelX, columnDrawY + target_top, columnWidth, 1, colorTargetIndicator))
                            .setToolTip(ITextComponent.getTextComponentOrEmpty("Target Cap: " + Config.NUTRIENT_TARGET_HIGH.get().toString() + "NU"));
                }


                columnBlockRelX -= nBlockWidth;
            }

        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void close() {
        minecraft.displayGuiScreen(null);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.blendColor(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(GUI);
        int relX = (this.width - WIDTH) / 2;
        int relY = (this.height - HEIGHT) / 2;
        this.blit(matrixStack,relX, relY, 0, 0, WIDTH, HEIGHT);

        /*
        drawString(
                matrixStack,
                new FontRenderer(),
                "",
                0,
                0,0
        );
         */


        super.render(matrixStack,mouseX, mouseY, partialTicks);
    }

    public static void open() {
        Minecraft.getInstance().displayGuiScreen(new NutrientGUI());
    }
}
