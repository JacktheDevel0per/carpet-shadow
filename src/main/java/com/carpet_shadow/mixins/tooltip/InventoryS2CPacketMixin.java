package com.carpet_shadow.mixins.tooltip;

import com.carpet_shadow.CarpetShadow;
import com.carpet_shadow.CarpetShadowSettings;
import com.carpet_shadow.interfaces.ShadowItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(InventoryS2CPacket.class)
public abstract class InventoryS2CPacketMixin {

    private static final String DISPLAY_KEY = "display";
    private static final String LORE_KEY = "Lore";
    @Redirect(method = "<init>(ILnet/minecraft/util/collection/DefaultedList;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;copy()Lnet/minecraft/item/ItemStack;"))
    public ItemStack copy_redirect(ItemStack instance) {




        ItemStack stackCopy = CarpetShadowSettings.shadowItemTooltip ? ShadowItem.copy_redirect(instance) : instance.copy();

        //Fix Buggy Client Stuff - wish it was by world...
        if(FabricLoader.getInstance().getEnvironmentType().equals(EnvType.CLIENT)) return stackCopy;




        NbtCompound ret = stackCopy.getTag();
        NbtCompound display = new NbtCompound();
        if (CarpetShadowSettings.shadowItemTooltip && ((ShadowItem) (Object) instance).getShadowId() != null) {
            LiteralText text = new LiteralText("shadow_id: ");
            text.append(new LiteralText(((ShadowItem) (Object) instance).getShadowId()).formatted(Formatting.GOLD, Formatting.BOLD));
            text.formatted(Formatting.ITALIC);
            NbtList list = new NbtList();
            if (ret == null) {
                ret = new NbtCompound();
            } else if (ret.contains(DISPLAY_KEY)) {
                display = ret.getCompound(DISPLAY_KEY);
                if (display.contains(LORE_KEY)) {
                    list = ret.getList(LORE_KEY, 8);
                }
            }
            list.add(NbtString.of(Text.Serializer.toJson(text)));
            display.put(LORE_KEY, list);
            ret.put(DISPLAY_KEY, display);
            ret.putString(ShadowItem.SHADOW_KEY,((ShadowItem) (Object) instance).getShadowId());


            stackCopy.setTag(ret);
        }


        return stackCopy;
    }

}
