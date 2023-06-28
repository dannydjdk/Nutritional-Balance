package com.dannyandson.nutritionalbalance.lunchbox;

import com.dannyandson.nutritionalbalance.NutritionalBalance;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class LunchBoxMenu extends AbstractContainerMenu {

    public static LunchBoxMenu createMenu(int containerId, Inventory playerInventory) {
        return createMenu(containerId, playerInventory, LunchBoxContainer.get(NutritionalBalance.LUNCHBOX_ITEM.get().getDefaultInstance()));
    }

    public static LunchBoxMenu createMenu(int containerId, Inventory playerInventory, Container inventory) {
        return new LunchBoxMenu(containerId, playerInventory, inventory);
    }

    private Container container;
    private Inventory playerInventory;
    private ItemStack lunchBox;

    protected LunchBoxMenu(int containerId, Inventory playerInventory, Container container) {
        super(NutritionalBalance.LUNCHBOX_MENU_TYPE.get(), containerId);
        checkContainerSize(container,5);

        this.container = container;
        this.playerInventory = playerInventory;
        this.lunchBox = playerInventory.getSelected();

        int leftCol = 47;
        int ySize = 252;

        container.startOpen(playerInventory.player);

        for (int lunchBoxRow = 0 ; lunchBoxRow < container.getContainerSize() ; lunchBoxRow++){
            this.addSlot(new Slot(container,lunchBoxRow, leftCol + lunchBoxRow * 18, ySize - 6 * 18 - 10){
                @Override
                public void setChanged() {
                    slotsChanged(container);
                    super.setChanged();
                }

                @Override
                public boolean mayPlace(ItemStack stack) {
                    return stack.isEdible() && !(stack.getItem() instanceof LunchBoxItem);
                }
            });
        }

        for (int playerInvRow = 0; playerInvRow < 3; playerInvRow++) {
            for (int playerInvCol = 0; playerInvCol < 9; playerInvCol++) {
                this.addSlot(new Slot(playerInventory, playerInvCol + playerInvRow * 9 + 9, leftCol + playerInvCol * 18, ySize - (4 - playerInvRow) * 18 - 10));
            }

        }

        for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++) {
            this.addSlot(new Slot(playerInventory, hotbarSlot, leftCol + hotbarSlot * 18, ySize - 24));
        }

    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            if (index < 5) {
                if (!this.moveItemStackTo(itemstack1, 5, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, 4, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    @Override
    public void slotsChanged(Container container) {
        if (this.container instanceof LunchBoxContainer lunchBoxContainer)
            lunchBoxContainer.save();
        super.slotsChanged(container);
    }

    public void setActiveSlot(int slot){
        if (this.lunchBox.getItem() instanceof LunchBoxItem lunchBoxItem){
            lunchBoxItem.setActiveFood(this.lunchBox,container.getItem(slot),this.playerInventory.player.level().isClientSide());
        }
    }


    public ItemStack getLunchBoxItemStack(){
        return this.lunchBox;
    }
}
