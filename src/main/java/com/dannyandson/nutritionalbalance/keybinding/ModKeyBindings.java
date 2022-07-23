package com.dannyandson.nutritionalbalance.keybinding;

import com.dannyandson.nutritionalbalance.NutritionalBalance;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = NutritionalBalance.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
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
