package com.dannyandson.nutritionalbalance.lunchbox;

import com.dannyandson.nutritionalbalance.network.LunchBoxActiveItemSync;
import com.dannyandson.nutritionalbalance.network.ModNetworkHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.IItemRenderProperties;
import net.minecraftforge.event.ForgeEventFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.CheckForNull;
import java.util.List;
import java.util.function.Consumer;

public class LunchBoxItem extends Item {

    public LunchBoxItem() {
        super(new Properties().tab(CreativeModeTab.TAB_FOOD));
    }

    @Override
    public @Nullable FoodProperties getFoodProperties(ItemStack stack, @Nullable LivingEntity entity) {
        if (stack.getItem() instanceof LunchBoxItem){
            ItemStack activeStack = getActiveFoodItemStack(stack);
            if (activeStack != null)
                return activeStack.getFoodProperties(entity);
            return null;
        }
        return super.getFoodProperties(stack,entity);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() instanceof LunchBoxItem) {
            ItemStack activeStack = getActiveFoodItemStack(stack);
            if (activeStack != null && !player.isSecondaryUseActive()) {
                FoodProperties activeFoodProperties = activeStack.getItem().getFoodProperties();
                if (
                        !activeStack.isEmpty() &&
                                activeFoodProperties != null &&
                                activeStack.isEdible() &&
                                !player.getCooldowns().isOnCooldown(activeStack.getItem()) &&
                                player.canEat(activeFoodProperties.canAlwaysEat())
                ) {
                    player.startUsingItem(hand);
                    return InteractionResultHolder.consume(stack);
                } else {
                    return InteractionResultHolder.fail(stack);
                }

            } else if (!level.isClientSide() && !(player.containerMenu instanceof LunchBoxMenu) && hand==InteractionHand.MAIN_HAND){
                LunchBoxContainer container = LunchBoxContainer.get(stack);

                player.openMenu(new SimpleMenuProvider((containerId, playerInventory, playerEntity) -> new LunchBoxMenu(containerId, playerInventory, container), new TranslatableComponent(this.getDescriptionId(stack))));
            }
            return InteractionResultHolder.pass(stack);
        }
        return super.use(level, player, hand);
    }

    @Override
    public @NotNull ItemStack finishUsingItem(ItemStack stack, @NotNull Level level, @NotNull LivingEntity entity) {
        if (stack.getItem() instanceof LunchBoxItem){

            if (entity instanceof Player) {
                Integer activeSlot = getActiveFoodItemSlot(stack);
                if (activeSlot != null) {
                    LunchBoxContainer container = LunchBoxContainer.get(stack);
                    ItemStack selectedStack = container.getItem(activeSlot);

                    if (!selectedStack.isEmpty()) {
                        ItemStack resultStack = ForgeEventFactory.onItemUseFinish(entity, selectedStack.copy(), entity.getUseItemRemainingTicks(), selectedStack.finishUsingItem(level, entity));
                        container.setItem(activeSlot,resultStack);
                        container.save();
                    }
                }
            }
            return stack;
        }
        return super.finishUsingItem(stack, level, entity);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        if (stack.getItem() instanceof LunchBoxItem) {
            ItemStack activeStack = getActiveFoodItemStack(stack);
            if (activeStack != null)
                return activeStack.getUseDuration();
        }
        return 32;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(ItemStack stack) {
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
                return new TranslatableComponent(this.getDescriptionId(stack)).append(" (").append(activeStack.getItem().getName(activeStack)).append(")") ;
        }
        return super.getName(stack);
    }
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @javax.annotation.Nullable Level world, List<Component> list, TooltipFlag flags) {
        if (Screen.hasShiftDown()) {
            list.add(new TranslatableComponent("message.item.lunchbox").withStyle(ChatFormatting.GRAY));
        } else
            list.add(new TranslatableComponent("nutritionalbalance.tooltip.press_shift").withStyle(ChatFormatting.DARK_GRAY));
    }
   @Override
    public int getItemStackLimit(ItemStack stack) {
        return 1;
    }

    public void setActiveFood(ItemStack lunchBoxStack, ItemStack targetItemStack) {
        setActiveFood(lunchBoxStack, targetItemStack, false);
    }

    public void setActiveFood(ItemStack lunchBoxStack, ItemStack targetItemStack, Boolean sync) {
        if (lunchBoxStack.getItem() instanceof LunchBoxItem && targetItemStack.isEdible()) {
            setActiveFood(lunchBoxStack, targetItemStack.getDescriptionId());
            if (sync)
                ModNetworkHandler.sendToServer(new LunchBoxActiveItemSync(targetItemStack.getDescriptionId()));
        }
    }

    public void setActiveFood(ItemStack lunchBoxStack, String descriptionId){
        if (!lunchBoxStack.hasTag())
            lunchBoxStack.setTag(new CompoundTag());
        lunchBoxStack.getTag().putString("active", descriptionId);
    }

    @CheckForNull
    public Integer getActiveFoodItemSlot(ItemStack lunchBoxStack) {
        if (lunchBoxStack.getItem() instanceof LunchBoxItem && lunchBoxStack.hasTag() && lunchBoxStack.getTag().contains("active")) {
            LunchBoxContainer container =  LunchBoxContainer.get(lunchBoxStack);
            String activeStack = lunchBoxStack.getTag().getString("active");
            for (int i = 0 ; i < container.getContainerSize() ; i++ )
            {
                if (container.getItem(i).getDescriptionId().equals(activeStack))
                    return i;
            }
        }
        return null;
    }
    @CheckForNull
    public ItemStack getItemStack(ItemStack lunchBoxStack, int slot) {
        if (lunchBoxStack.getItem() instanceof LunchBoxItem) {
            LunchBoxContainer container =  LunchBoxContainer.get(lunchBoxStack);
            return container.getItem(slot);
        }
        return null;
    }
    @CheckForNull
    public ItemStack getActiveFoodItemStack(ItemStack lunchBoxStack) {
        Integer slot = getActiveFoodItemSlot(lunchBoxStack);
        if (slot!=null)
            return getItemStack(lunchBoxStack,slot);
        return null;
    }

    @Override
    public void initializeClient(Consumer<IItemRenderProperties> consumer) {
         consumer.accept(new IItemRenderProperties() {
                            @Override
                            public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
                                return new LunchBoxItemRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
                            }
                        }
        );
    }


}
