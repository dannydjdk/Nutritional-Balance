package com.dannyandson.nutritionalbalance.lunchbox;

import com.dannyandson.nutritionalbalance.Config;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import javax.annotation.CheckForNull;

public class LunchBoxContainer extends SimpleContainer {

    public final ItemStack lunchBox;

    @CheckForNull
    public static LunchBoxContainer get(ItemStack lunchBox){
        if (lunchBox.getItem() instanceof LunchBoxItem)
            return new LunchBoxContainer(lunchBox);
        return null;
    }

    private LunchBoxContainer(ItemStack lunchBox) {
        super(Config.LUNCHBOX_SLOT_COUNT.get());
        this.lunchBox = lunchBox;
        CustomData customData = lunchBox.get(DataComponents.CUSTOM_DATA);
        if (customData != null) {
            CompoundTag tag = customData.copyTag();
            if (tag.contains("contents", Tag.TAG_LIST)) {
                fromTag(tag.getList("contents", Tag.TAG_COMPOUND), net.minecraft.core.RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY));
            }
        }
    }

    public void save() {
        ListTag contentsTag = this.createTag(net.minecraft.core.RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY));
        CompoundTag tag;
        CustomData existingData = lunchBox.get(DataComponents.CUSTOM_DATA);
        if (existingData != null) {
            tag = existingData.copyTag();
        } else {
            tag = new CompoundTag();
        }
        tag.put("contents", contentsTag);
        lunchBox.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }
}
