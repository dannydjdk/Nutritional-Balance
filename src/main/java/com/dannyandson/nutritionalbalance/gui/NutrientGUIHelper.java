package com.dannyandson.nutritionalbalance.gui;

import com.dannyandson.nutritionalbalance.Config;
import com.dannyandson.nutritionalbalance.api.INutritionalBalancePlayer;
import com.dannyandson.nutritionalbalance.api.IPlayerNutrient;
import com.dannyandson.nutritionalbalance.nutrients.PlayerNutritionData;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class NutrientGUIHelper {

    public static void init(INutrientGUIScreen screen, int guiWidth, int guiHeight) {
        init(screen, guiWidth, guiHeight, 0);
    }

    public static void init(INutrientGUIScreen screen, int guiWidth, int guiHeight, int yOffset) {

        int screenWidth = screen.getWidth();
        int screenHeight = screen.getHeight();
        INutritionalBalancePlayer iNutritionalBalancePlayer = PlayerNutritionData.getWorldNutritionData().getNutritionalBalancePlayer(screen.getMinecraft().player);

        //max height of columns (not including border)
        int maxColumnHeight = 7;
        //width of labels
        int labelWidth = 50;
        //width of columns (not including border)
        int columnWidth = guiWidth - labelWidth - 30;
        //colors (in hex aRGB) - consider making these configurable
        int colorColumnBackground = 0x00000000;
        int colorNutrientValueFill = 0xFF0033FF;
        int colorTargetIndicator = 0x9900FF00;
        int colorUnsafeIndicator = 0x99FF0000;

        int relX = (screenWidth - guiWidth) / 2;
        int relY = ((screenHeight - guiHeight) / 2) + yOffset;

        screen.addModWidget(new ModWidget(relX, relY + 5, guiWidth, 10, Component.translatable("nutritionalbalance.nutrients"), 0xFF000000).setTextHAlignment(ModWidget.HAlignment.CENTER).setTextVAlignment(ModWidget.VAlignment.TOP));

        MutableComponent message;
        int messageColor;
        if (iNutritionalBalancePlayer.getCachedStatus() == IPlayerNutrient.NutrientStatus.ON_TARGET) {
            message = Component.translatable("nutritionalbalance.nutrientstatus.details.ON_TARGET");
            messageColor = 0xFF008800;
        } else if (iNutritionalBalancePlayer.getCachedStatus() == IPlayerNutrient.NutrientStatus.ENGORGED) {
            message = Component.translatable("nutritionalbalance.nutrientstatus.details.ENGORGED");
            messageColor = 0xFF880000;
        } else if (iNutritionalBalancePlayer.getCachedStatus() == IPlayerNutrient.NutrientStatus.MALNOURISHED) {
            message = Component.translatable("nutritionalbalance.nutrientstatus.details.MALNOURISHED");
            messageColor = 0xFF880000;
        } else {
            message = Component.translatable("nutritionalbalance.nutrientstatus.details.SAFE");
            messageColor = 0xFF004400;
        }

        screen.addModWidget(new ModWidget(relX, relY + 18, guiWidth, 10, message, messageColor).setTextHAlignment(ModWidget.HAlignment.CENTER).setTextVAlignment(ModWidget.VAlignment.TOP));

        //determine how much space is available to draw each nutrient column
        int nutrientcount = iNutritionalBalancePlayer.getPlayerNutrients().size();
        if (nutrientcount > 0) {
            int nBlocksOffset = 35;
            int nBlockHeight = (guiHeight - nBlocksOffset - 30) / nutrientcount;
            //calculate possible height of columns (not including border)
            int columnHeight = Math.min(maxColumnHeight, nBlockHeight - 2);

            //columnBlockRelX stores the x value of the column block currently being drawn
            int columnBlockRelX = relX + 10;
            //columnDrawY stores the Y offset at which nutrient columns will be drawn (starting with bottom nutrient)
            int columnDrawY = relY + nBlocksOffset + (nBlockHeight * (nutrientcount - 1));

            int engorged_bottom = Math.round((Config.NUTRIENT_ENGORGED.get().floatValue()) / Config.NUTRIENT_MAX.get().floatValue() * columnWidth);
            int target_top = Math.round((Config.NUTRIENT_TARGET_HIGH.get().floatValue()) / Config.NUTRIENT_MAX.get().floatValue() * columnWidth);
            int target_bottom = Math.round((Config.NUTRIENT_LOW_TARGET.get().floatValue()) / Config.NUTRIENT_MAX.get().floatValue() * columnWidth);
            int malnourished_top = Math.round((Config.NUTRIENT_MALNOURISHED.get().floatValue()) / Config.NUTRIENT_MAX.get().floatValue() * columnWidth);


            //loop through player's nutrients and draw the columns
            for (IPlayerNutrient playerNutrient : iNutritionalBalancePlayer.getPlayerNutrients()) {
                //x value to begin drawing the column
                int columnRelY = columnDrawY;

                //percentage of players current nutrient level of max nutrient level
                int nutrientValueWidth = Math.round(playerNutrient.getValue() / Config.NUTRIENT_MAX.get().floatValue() * columnWidth);

                //Check if this a nonessential nutrient
                boolean nonEssentialNutrient = false;
                for (String badNutrientName : Config.BAD_NUTRIENTS.get()) {
                    if (badNutrientName.equals(playerNutrient.getNutrientName()))
                        nonEssentialNutrient = true;
                }
                //Check if this is a good nutrient (one that does not cause engorgement)
                boolean goodNutrient = false;
                for (String goodNutrientName : Config.GOOD_NUTRIENTS.get()) {
                    if (goodNutrientName.equals(playerNutrient.getNutrientName()))
                        goodNutrient = true;
                }

                //Draw columns and fill them with player nutrient values

                //draw border of nutrient column (solid block that will be overlain with background)
                screen.addModWidget(new ModWidget(columnBlockRelX + labelWidth - 1, columnRelY - 1, columnWidth + 2, columnHeight + 2, 0xFF000000));
                //draw background of nutrient column
                screen.addModWidget(new ModWidget(columnBlockRelX + labelWidth, columnRelY, columnWidth, columnHeight, colorColumnBackground));
                //draw nutrient label
                screen.addModWidget(new ModWidget(columnBlockRelX, columnDrawY, labelWidth, 10, Component.translatable("nutritionalbalance.nutrient." + playerNutrient.getNutrientName()), 0xFF000000));

                //fill nutrient column based on player's level of this nutrient
                screen.addModWidget(new ModWidget(columnBlockRelX + labelWidth, columnRelY, nutrientValueWidth, columnHeight, colorNutrientValueFill))
                        .setToolTip(Component.nullToEmpty((Math.round(playerNutrient.getValue() * 10f) / 10f) + "NU"));

                //Draw lines and shading to indicate targets and "unsafe" ranges
                if (nonEssentialNutrient) {
                    //for nonessential nutrients draw target line at bottom
                    screen.addModWidget(new ModWidget(columnBlockRelX + labelWidth, columnRelY - 1, 1, columnHeight + 2, colorTargetIndicator))
                            .setToolTip(Component.nullToEmpty("Target: 0NU"));
                } else {
                    //for essential nutrients (not nonessential)
                    //draw line at malnourishment border
                    screen.addModWidget(new ModWidget(columnBlockRelX + labelWidth + malnourished_top, columnRelY - 1, 1, columnHeight + 2, colorUnsafeIndicator))
                            .setToolTip(Component.nullToEmpty("Malnourishment: " + Config.NUTRIENT_MALNOURISHED.get().toString() + "NU"));
                    //draw line at bottom of target
                    screen.addModWidget(new ModWidget(columnBlockRelX + labelWidth + target_bottom, columnRelY - 1, 1, columnHeight + 2, colorTargetIndicator))
                            .setToolTip(Component.nullToEmpty("Target: " + Config.NUTRIENT_LOW_TARGET.get().toString() + "NU"));
                }
                if (goodNutrient)
                    //for good nutrients, draw target line at top
                    screen.addModWidget(new ModWidget(columnBlockRelX + labelWidth + columnWidth, columnRelY - 1, 1, columnHeight + 2, colorTargetIndicator))
                            .setToolTip(Component.nullToEmpty("Target Cap: ∞NU"));
                else {
                    //for not good nutrients (but not necessarily bad)
                    //draw line at lower engorgement border
                    screen.addModWidget(new ModWidget(columnBlockRelX + labelWidth + engorged_bottom, columnRelY - 1, 1, columnHeight + 1, colorUnsafeIndicator))
                            .setToolTip(Component.nullToEmpty("Engorgement: " + Config.NUTRIENT_ENGORGED.get().toString() + "NU"));
                    //draw line at top of target
                    screen.addModWidget(new ModWidget(columnBlockRelX + labelWidth + target_top, columnRelY - 1, 1, columnHeight + 2, colorTargetIndicator))
                            .setToolTip(Component.nullToEmpty("Target Cap: " + Config.NUTRIENT_TARGET_HIGH.get().toString() + "NU"));
                }


                columnDrawY -= nBlockHeight;
            }

        }
    }

}
