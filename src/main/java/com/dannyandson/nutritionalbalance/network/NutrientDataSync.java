package com.dannyandson.nutritionalbalance.network;

import com.dannyandson.nutritionalbalance.nutrients.Nutrient;
import com.dannyandson.nutritionalbalance.nutrients.WorldNutrients;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Item;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

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
        String[] nutrientStrings = buffer.readUtf().split(",");
        for (String nutrientString : nutrientStrings){
            Nutrient nutrient = WorldNutrients.getByName(nutrientString);
            if (nutrient!=null)
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

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {

        ctx.get().enqueueWork(() -> {
            WorldNutrients.setItemNutrients(item,nutrients);
            ctx.get().setPacketHandled(true);
        });
        return true;
    }
}
