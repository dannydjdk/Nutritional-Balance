package com.dannyandson.nutritionalbalance.keybinding;

import com.dannyandson.nutritionalbalance.NutritionalBalance;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import java.util.HashMap;
import java.util.Map;

public class ModKeyBindings {
    public static Map<String,KeyBinding> keyBindings = new HashMap<>();

    public static void register()
    {
        KeyBinding nutritionguikeybind =  new KeyBinding("key." + NutritionalBalance.MODID + ".opennutrientgui", 78, "Healthy Diet");
        keyBindings.put("nutrientgui",nutritionguikeybind);
        ClientRegistry.registerKeyBinding(nutritionguikeybind);
    }

}
