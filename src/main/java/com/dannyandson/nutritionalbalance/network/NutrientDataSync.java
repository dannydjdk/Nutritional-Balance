package com.dannyandson.nutritionalbalance.network;

import com.dannyandson.nutritionalbalance.nutrients.Nutrient;
import com.dannyandson.nutritionalbalance.nutrients.WorldNutrients;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.network.CustomPayloadEvent;

import java.util.ArrayList;
import java.util.List;

public class NutrientDataSync {

    private Item item;
    private List<Nutrient> nutrients;

    public NutrientDataSync(Item item, List<Nutrient>nutrients) {
        this.item = item;
        this.nutrients = nutrients;
    }

    public NutrientDataSync(FriendlyByteBuf buffer)
    {
        this.item=buffer.readItem().getItem();
        this.nutrients=new ArrayList<Nutrient>();
        for (String nutrientString : buffer.readUtf().split(",")){
            nutrients.add(WorldNutrients.getByName(nutrientString));
        }

    }

    public void toBytes(FriendlyByteBuf buf)
    {
        buf.writeItem(item.getDefaultInstance());
        String nutrientString = "";
        for (Nutrient nutrient : nutrients){
            if (!nutrientString.equals("")) nutrientString = nutrientString.concat(",");
            nutrientString = nutrientString.concat(nutrient.name);
        }
        buf.writeUtf(nutrientString);
    }

    public boolean handle(CustomPayloadEvent.Context ctx) {

        ctx.enqueueWork(() -> {
            WorldNutrients.setItemNutrients(item,nutrients);
            ctx.setPacketHandled(true);
        });
        return true;
    }
}
