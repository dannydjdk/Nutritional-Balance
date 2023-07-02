package com.dannyandson.nutritionalbalance.gui;

import net.minecraft.client.Minecraft;

public interface INutrientGUIScreen {

    int getWidth();
    int getHeight();
    ModWidget addModWidget(ModWidget modWidget);

    Minecraft getMinecraft();
}
