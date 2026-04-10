package com.dannyandson.nutritionalbalance.lunchbox;

import com.dannyandson.nutritionalbalance.network.LunchBoxActiveItemSync;
import com.dannyandson.nutritionalbalance.network.ModNetworkHandler;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

public class LunchBoxItem extends Item {

    public LunchBoxItem(Item.Properties props) {
        super(props);
    }

    /**
     * Sync FOOD and CONSUMABLE data components from the active food item onto the lunchbox stack.
     * This makes the lunchbox appear as food to any mod checking data components,
     * replacing the old getFoodProperties() override that was removed in 26.1.
     */
    public void syncFoodComponents(ItemStack lunchBoxStack) {
        ItemStack activeStack = getActiveFoodItemStack(lunchBoxStack);
        if (activeStack != null && !activeStack.isEmpty()) {
            // Copy FOOD component from active food
            FoodProperties food = activeStack.get(DataComponents.FOOD);
            if (food != null) {
                lunchBoxStack.set(DataComponents.FOOD, food);
            } else {
                lunchBoxStack.remove(DataComponents.FOOD);
            }
            // Copy CONSUMABLE component from active food (controls eat animation/duration)
            Consumable consumable = activeStack.get(DataComponents.CONSUMABLE);
            if (consumable != null) {
                lunchBoxStack.set(DataComponents.CONSUMABLE, consumable);
            } else {
                lunchBoxStack.remove(DataComponents.CONSUMABLE);
            }
        } else {
            // No active food — clear food components
            lunchBoxStack.remove(DataComponents.FOOD);
            lunchBoxStack.remove(DataComponents.CONSUMABLE);
        }
    }

    @Override
    public @NotNull InteractionResult use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() instanceof LunchBoxItem) {
            ItemStack activeStack = getActiveFoodItemStack(stack);
            if (activeStack != null && !player.isSecondaryUseActive()) {
                // Ensure food components are synced before eating check
                syncFoodComponents(stack);
                FoodProperties food = stack.get(DataComponents.FOOD);
                if (food != null && player.canEat(food.canAlwaysEat())) {
                    player.startUsingItem(hand);
                    return InteractionResult.CONSUME;
                } else {
                    return InteractionResult.FAIL;
                }
            } else if (!level.isClientSide() && !(player.containerMenu instanceof LunchBoxMenu) && hand == InteractionHand.MAIN_HAND) {
                LunchBoxContainer container = LunchBoxContainer.get(stack);
                player.openMenu(new SimpleMenuProvider((containerId, playerInventory, playerEntity) ->
                        new LunchBoxMenu(containerId, playerInventory, container),
                        Component.translatable(this.getDescriptionId())));
            }
            return InteractionResult.PASS;
        }
        return super.use(level, player, hand);
    }

    @Override
    public @NotNull ItemStack finishUsingItem(ItemStack stack, @NotNull Level level, @NotNull LivingEntity entity) {
        if (stack.getItem() instanceof LunchBoxItem) {
            if (entity instanceof Player) {
                Integer activeSlot = getActiveFoodItemSlot(stack);
                if (activeSlot != null) {
                    LunchBoxContainer container = LunchBoxContainer.get(stack);
                    ItemStack selectedStack = container.getItem(activeSlot);
                    if (!selectedStack.isEmpty()) {
                        ItemStack resultStack = selectedStack.finishUsingItem(level, entity);
                        container.setItem(activeSlot, resultStack);
                        container.save();
                        // Re-sync food components after consumption (food may be depleted)
                        syncFoodComponents(stack);
                    }
                }
            }
            return stack;
        }
        return super.finishUsingItem(stack, level, entity);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        // Fallback in case CONSUMABLE component isn't present
        if (stack.getItem() instanceof LunchBoxItem) {
            ItemStack activeStack = getActiveFoodItemStack(stack);
            if (activeStack != null)
                return activeStack.getUseDuration(entity);
        }
        return 32;
    }

    @Override
    public @NotNull ItemUseAnimation getUseAnimation(ItemStack stack) {
        // Fallback in case CONSUMABLE component isn't present
        if (stack.getItem() instanceof LunchBoxItem) {
            ItemStack activeStack = getActiveFoodItemStack(stack);
            if (activeStack != null)
                return activeStack.getUseAnimation();
        }
        return super.getUseAnimation(stack);
    }

    @Override
    public @NotNull Component getName(ItemStack stack) {
        if (stack.getItem() instanceof LunchBoxItem) {
            ItemStack activeStack = getActiveFoodItemStack(stack);
            if (activeStack != null)
                return Component.translatable(this.getDescriptionId()).append(" (").append(activeStack.getItem().getName(activeStack)).append(")");
        }
        return super.getName(stack);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, TooltipContext context,
                                TooltipDisplay display, Consumer<Component> textConsumer,
                                TooltipFlag flags) {
        var window = Minecraft.getInstance().getWindow();
        boolean shiftDown = InputConstants.isKeyDown(window, GLFW.GLFW_KEY_LEFT_SHIFT)
                || InputConstants.isKeyDown(window, GLFW.GLFW_KEY_RIGHT_SHIFT);
        if (shiftDown) {
            textConsumer.accept(Component.translatable("message.item.lunchbox").withStyle(ChatFormatting.GRAY));
        } else
            textConsumer.accept(Component.translatable("nutritionalbalance.tooltip.press_shift").withStyle(ChatFormatting.DARK_GRAY));
    }

    public void setActiveFood(ItemStack lunchBoxStack, ItemStack targetItemStack) {
        setActiveFood(lunchBoxStack, targetItemStack, false);
    }

    public void setActiveFood(ItemStack lunchBoxStack, ItemStack targetItemStack, Boolean sync) {
        if (lunchBoxStack.getItem() instanceof LunchBoxItem && targetItemStack.has(DataComponents.FOOD)) {
            setActiveFood(lunchBoxStack, targetItemStack.getItem().getDescriptionId());
            if (sync)
                ModNetworkHandler.sendToServer(new LunchBoxActiveItemSync(targetItemStack.getItem().getDescriptionId()));
        }
    }

    public void setActiveFood(ItemStack lunchBoxStack, String descriptionId) {
        CompoundTag tag;
        CustomData existingData = lunchBoxStack.get(DataComponents.CUSTOM_DATA);
        if (existingData != null) {
            tag = existingData.copyTag();
        } else {
            tag = new CompoundTag();
        }
        tag.putString("active", descriptionId);
        lunchBoxStack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        // Sync food components so the lunchbox looks like food to other mods
        syncFoodComponents(lunchBoxStack);
    }

    @Nullable
    public Integer getActiveFoodItemSlot(ItemStack lunchBoxStack) {
        if (lunchBoxStack.getItem() instanceof LunchBoxItem) {
            CustomData customData = lunchBoxStack.get(DataComponents.CUSTOM_DATA);
            if (customData != null) {
                CompoundTag tag = customData.copyTag();
                if (tag.contains("active")) {
                    LunchBoxContainer container = LunchBoxContainer.get(lunchBoxStack);
                    String activeStack = tag.getStringOr("active", "");
                    for (int i = 0; i < container.getContainerSize(); i++) {
                        if (container.getItem(i).getItem().getDescriptionId().equals(activeStack))
                            return i;
                    }
                }
            }
        }
        return null;
    }

    @Nullable
    public ItemStack getItemStack(ItemStack lunchBoxStack, int slot) {
        if (lunchBoxStack.getItem() instanceof LunchBoxItem) {
            LunchBoxContainer container = LunchBoxContainer.get(lunchBoxStack);
            return container.getItem(slot);
        }
        return null;
    }

    @Nullable
    public ItemStack getActiveFoodItemStack(ItemStack lunchBoxStack) {
        Integer slot = getActiveFoodItemSlot(lunchBoxStack);
        if (slot != null)
            return getItemStack(lunchBoxStack, slot);
        return null;
    }
}