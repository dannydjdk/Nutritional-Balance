package com.dannyandson.nutritionalbalance.lunchbox;

import com.dannyandson.nutritionalbalance.Config;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import org.jspecify.annotations.Nullable;

public class LunchBoxContainer extends SimpleContainer {

    public final ItemStack lunchBox;

    @Nullable
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
            if (tag.contains("contents")) {
                ListTag list = tag.getListOrEmpty("contents");
                for (int i = 0; i < list.size() && i < this.getContainerSize(); i++) {
                    // Each slot stored as SNBT string
                    String snbt = list.getStringOr(i, "");
                    if (!snbt.isEmpty()) {
                        try {
                            CompoundTag itemTag = TagParser.parseCompoundFully(snbt);
                            String itemId = itemTag.getStringOr("id", "");
                            int count = itemTag.getIntOr("count", 0);
                            if (!itemId.isEmpty() && count > 0) {
                                var item = net.minecraft.core.registries.BuiltInRegistries.ITEM.getValue(
                                        net.minecraft.resources.Identifier.parse(itemId));
                                if (item != null) {
                                    this.setItem(i, new ItemStack(item, count));
                                }
                            }
                        } catch (Exception ignored) {}
                    }
                }
            }
        }
    }

    public void save() {
        ListTag contentsTag = new ListTag();
        for (int i = 0; i < this.getContainerSize(); i++) {
            ItemStack stack = this.getItem(i);
            if (!stack.isEmpty()) {
                CompoundTag itemTag = new CompoundTag();
                itemTag.putString("id", BuiltInRegistries.ITEM.getKey(stack.getItem()).toString());
                itemTag.putInt("count", stack.getCount());
                contentsTag.add(StringTag.valueOf(itemTag.toString()));
            } else {
                contentsTag.add(StringTag.valueOf(""));
            }
        }
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
