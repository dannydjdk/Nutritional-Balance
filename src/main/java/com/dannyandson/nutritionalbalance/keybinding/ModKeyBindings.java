package com.dannyandson.nutritionalbalance.keybinding;

import com.dannyandson.nutritionalbalance.NutritionalBalance;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.fmlclient.registry.ClientRegistry;

import java.util.HashMap;
import java.util.Map;

public class ModKeyBindings {
    public static Map<String, KeyMapping> keyBindings = new HashMap<>();

    public static void register()
    {
        KeyMapping nutritionguikeybind =  new KeyMapping("key." + NutritionalBalance.MODID + ".opennutrientgui", 78, "Healthy Diet");
        keyBindings.put("nutrientgui",nutritionguikeybind);
        ClientRegistry.registerKeyBinding(nutritionguikeybind);
    }

}
