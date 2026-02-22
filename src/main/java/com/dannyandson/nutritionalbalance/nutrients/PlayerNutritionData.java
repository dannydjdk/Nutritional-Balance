package com.dannyandson.nutritionalbalance.nutrients;

import com.dannyandson.nutritionalbalance.NutritionalBalance;
import com.dannyandson.nutritionalbalance.api.INutritionalBalancePlayer;
import com.dannyandson.nutritionalbalance.api.IPlayerNutrient;
import com.dannyandson.nutritionalbalance.capabilities.DefaultNutritionalBalancePlayer;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;

public class PlayerNutritionData extends SavedData {

    private static PlayerNutritionData worldNutritionData;

    public static void init(ServerLevel serverLevel){
        worldNutritionData = serverLevel.getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(PlayerNutritionData::new, PlayerNutritionData::load),
                NutritionalBalance.MODID
        );
    }

    public static PlayerNutritionData getWorldNutritionData() {
        if (worldNutritionData==null)
            worldNutritionData=new PlayerNutritionData();
        return worldNutritionData;
    }

    Map<String,INutritionalBalancePlayer> playerUUIDDataMap = new HashMap<>();

    PlayerNutritionData(){}

    public static PlayerNutritionData load(CompoundTag nbt, HolderLookup.Provider registries) {
        PlayerNutritionData data = new PlayerNutritionData();
        for(String uuid : nbt.getAllKeys()){
            DefaultNutritionalBalancePlayer nutritionalBalancePlayer = new DefaultNutritionalBalancePlayer();
            CompoundTag playerNBT = (CompoundTag) nbt.get(uuid);
            for (Nutrient nutrient: WorldNutrients.get())
            {
                nutritionalBalancePlayer.getPlayerNutrientByName(nutrient.name).setValue(playerNBT.getFloat(nutrient.name));
            }
            data.playerUUIDDataMap.put(uuid,nutritionalBalancePlayer);
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag nbt, HolderLookup.Provider registries) {
        for(Map.Entry<String,INutritionalBalancePlayer> entry: playerUUIDDataMap.entrySet()){
            CompoundTag playerNBT = new CompoundTag();
            for (IPlayerNutrient playerNutrient : entry.getValue().getPlayerNutrients()){
                playerNBT.putFloat(playerNutrient.getNutrient().name, playerNutrient.getValue());
            }
            nbt.put(entry.getKey(), playerNBT);
        }
        return nbt;
    }

    public INutritionalBalancePlayer getNutritionalBalancePlayer(Player player){
        String uuid = player.getStringUUID();
        if (!playerUUIDDataMap.containsKey(uuid))
            playerUUIDDataMap.put(uuid,new DefaultNutritionalBalancePlayer());
        return playerUUIDDataMap.get(uuid);
    }
}
