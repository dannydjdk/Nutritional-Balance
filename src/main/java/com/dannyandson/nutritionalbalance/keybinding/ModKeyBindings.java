package com.dannyandson.nutritionalbalance.keybinding;

import com.dannyandson.nutritionalbalance.NutritionalBalance;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(value = Dist.CLIENT, modid = NutritionalBalance.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModKeyBindings {
    public static Map<String, KeyMapping> keyBindings = new HashMap<>();

    @SubscribeEvent
    public static void register(RegisterKeyMappingsEvent event)
    {
        KeyMapping nutritionguikeybind =  new KeyMapping("key." + NutritionalBalance.MODID + ".opennutrientgui", 78, "Nutritional Balance");
        keyBindings.put("nutrientgui",nutritionguikeybind);
        event.register(nutritionguikeybind);
    }
}
