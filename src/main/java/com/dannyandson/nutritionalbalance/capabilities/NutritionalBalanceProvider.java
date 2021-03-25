package com.dannyandson.nutritionalbalance.capabilities;

import com.dannyandson.nutritionalbalance.api.INutritionalBalancePlayer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NutritionalBalanceProvider implements ICapabilitySerializable<CompoundNBT> {
    private final DefaultNutritionalBalancePlayer nutritionalbalancePlayer = new DefaultNutritionalBalancePlayer();
    private final LazyOptional<INutritionalBalancePlayer> nutritionalbalancePlayerLazyOptional = LazyOptional.of(() -> nutritionalbalancePlayer);

    @Override
    public CompoundNBT serializeNBT() {
        if (CapabilityNutritionalBalancePlayer.HEALTHY_DIET_PLAYER_CAPABILITY == null) {
            return new CompoundNBT();
        } else {
            return (CompoundNBT) CapabilityNutritionalBalancePlayer.HEALTHY_DIET_PLAYER_CAPABILITY.writeNBT(nutritionalbalancePlayer, null);
        }
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        if (CapabilityNutritionalBalancePlayer.HEALTHY_DIET_PLAYER_CAPABILITY != null) {
            CapabilityNutritionalBalancePlayer.HEALTHY_DIET_PLAYER_CAPABILITY.readNBT(nutritionalbalancePlayer, null, nbt);
        }
    }

    /**
     * Retrieves the Optional handler for the capability requested on the specific side.
     * The return value <strong>CAN</strong> be the same for multiple faces.
     * Modders are encouraged to cache this value, using the listener capabilities of the Optional to
     * be notified if the requested capability get lost.
     *
     * @param cap
     * @param side
     * @return The requested an optional holding the requested capability.
     */
    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap== CapabilityNutritionalBalancePlayer.HEALTHY_DIET_PLAYER_CAPABILITY)
            return nutritionalbalancePlayerLazyOptional.cast();
        else
            return LazyOptional.empty();
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
        return getCapability(cap, null);
    }

    public void invalidate() {
        this.nutritionalbalancePlayerLazyOptional.invalidate();
    }

}