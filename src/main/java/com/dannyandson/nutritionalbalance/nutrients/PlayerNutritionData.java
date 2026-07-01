package com.dannyandson.nutritionalbalance.nutrients;

import com.dannyandson.nutritionalbalance.NutritionalBalance;
import com.dannyandson.nutritionalbalance.api.INutritionalBalancePlayer;
import com.dannyandson.nutritionalbalance.api.IPlayerNutrient;
import com.dannyandson.nutritionalbalance.capabilities.DefaultNutritionalBalancePlayer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.HashMap;
import java.util.Map;

public class PlayerNutritionData extends SavedData {

    private static PlayerNutritionData worldNutritionData;

    public static final Codec<PlayerNutritionData> CODEC = RecordCodecBuilder.<PlayerNutritionData>mapCodec(instance ->
            instance.group(
                    CompoundTag.CODEC.optionalFieldOf("nutrition_data", new CompoundTag())
                            .forGetter(PlayerNutritionData::saveToTag)
            ).apply(instance, PlayerNutritionData::loadFromTag)
    ).codec();

    public static final SavedDataType<PlayerNutritionData> TYPE = new SavedDataType<>(
            Identifier.fromNamespaceAndPath(NutritionalBalance.MODID, "nutrition_data"),
            PlayerNutritionData::new,
            CODEC,
            null
    );

    public static void init(ServerLevel serverLevel) {
        worldNutritionData = serverLevel.getDataStorage().computeIfAbsent(TYPE);
    }

    public static synchronized PlayerNutritionData getWorldNutritionData() {
        if (worldNutritionData == null)
            worldNutritionData = new PlayerNutritionData();
        return worldNutritionData;
    }

    Map<String, INutritionalBalancePlayer> playerUUIDDataMap = new HashMap<>();

    public PlayerNutritionData() {}

    /**
     * Serialize all player nutrition data into a CompoundTag.
     */
    public CompoundTag saveToTag() {
        CompoundTag nbt = new CompoundTag();
        for (Map.Entry<String, INutritionalBalancePlayer> entry : playerUUIDDataMap.entrySet()) {
            CompoundTag playerNBT = new CompoundTag();
            for (IPlayerNutrient playerNutrient : entry.getValue().getPlayerNutrients()) {
                playerNBT.putFloat(playerNutrient.getNutrient().name, playerNutrient.getValue());
            }
            nbt.put(entry.getKey(), playerNBT);
        }
        return nbt;
    }

    /**
     * Deserialize player nutrition data from a CompoundTag.
     */
    public static PlayerNutritionData loadFromTag(CompoundTag nbt) {
        PlayerNutritionData data = new PlayerNutritionData();
        for (String uuid : nbt.keySet()) {
            DefaultNutritionalBalancePlayer nutritionalBalancePlayer = new DefaultNutritionalBalancePlayer();
            CompoundTag playerNBT = nbt.getCompound(uuid).orElseGet(CompoundTag::new);
            for (Nutrient nutrient : WorldNutrients.get()) {
                IPlayerNutrient playerNutrient = nutritionalBalancePlayer.getPlayerNutrientByName(nutrient.name);
                if (playerNutrient != null) {
                    playerNutrient.setValue(playerNBT.getFloatOr(nutrient.name, 0.0f));
                }
            }
            data.playerUUIDDataMap.put(uuid, nutritionalBalancePlayer);
        }
        return data;
    }

    public INutritionalBalancePlayer getNutritionalBalancePlayer(Player player) {
        String uuid = player.getStringUUID();
        if (!playerUUIDDataMap.containsKey(uuid))
            playerUUIDDataMap.put(uuid, new DefaultNutritionalBalancePlayer());
        return playerUUIDDataMap.get(uuid);
    }
}