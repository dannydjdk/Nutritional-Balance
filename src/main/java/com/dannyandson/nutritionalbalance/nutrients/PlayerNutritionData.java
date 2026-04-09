package com.dannyandson.nutritionalbalance.nutrients;

import com.dannyandson.nutritionalbalance.NutritionalBalance;
import com.dannyandson.nutritionalbalance.api.INutritionalBalancePlayer;
import com.dannyandson.nutritionalbalance.api.IPlayerNutrient;
import com.dannyandson.nutritionalbalance.capabilities.DefaultNutritionalBalancePlayer;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import com.mojang.serialization.MapCodec;

import java.util.HashMap;
import java.util.Map;

public class PlayerNutritionData extends SavedData {

    private static PlayerNutritionData worldNutritionData;

    public static final SavedDataType<PlayerNutritionData> TYPE = new SavedDataType<>(
            Identifier.fromNamespaceAndPath(NutritionalBalance.MODID, "nutrition_data"),
            PlayerNutritionData::new,
            MapCodec.unitCodec(PlayerNutritionData::new),
            null
    );

    public static void init(ServerLevel serverLevel){
        worldNutritionData = serverLevel.getDataStorage().computeIfAbsent(TYPE);
    }

    public static PlayerNutritionData getWorldNutritionData() {
        if (worldNutritionData==null)
            worldNutritionData=new PlayerNutritionData();
        return worldNutritionData;
    }

    Map<String,INutritionalBalancePlayer> playerUUIDDataMap = new HashMap<>();

    PlayerNutritionData(){}

    // TODO: SavedData serialization in 26.1 uses codec-based approach via SavedDataType.
    // The old CompoundTag save/load pattern may need to be adapted to the codec system.
    // For now, the codec is a unit codec (no-op) and actual data persistence needs
    // to be reimplemented using the new ValueInput/ValueOutput or codec approach.

    public INutritionalBalancePlayer getNutritionalBalancePlayer(Player player){
        String uuid = player.getStringUUID();
        if (!playerUUIDDataMap.containsKey(uuid))
            playerUUIDDataMap.put(uuid,new DefaultNutritionalBalancePlayer());
        return playerUUIDDataMap.get(uuid);
    }
}
