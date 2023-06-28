package com.dannyandson.nutritionalbalance.lunchbox;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

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
        super(5);
        this.lunchBox = lunchBox;
        if (lunchBox.hasTag()) {
            fromTag(lunchBox.getTag().getList("contents", Tag.TAG_COMPOUND));
        }
    }

    public void save() {
        ListTag contentsTag = this.createTag();
        if (!lunchBox.hasTag())
            lunchBox.setTag(new CompoundTag());
        lunchBox.getTag().put("contents",contentsTag);
    }

}