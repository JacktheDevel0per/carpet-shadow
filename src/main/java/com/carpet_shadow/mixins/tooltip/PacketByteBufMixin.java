package com.carpet_shadow.mixins.tooltip;

import com.carpet_shadow.CarpetShadowSettings;
import com.carpet_shadow.interfaces.ShadowItem;
import com.google.gson.JsonParseException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(PacketByteBuf.class)
public abstract class PacketByteBufMixin {

    private static final String DISPLAY_KEY = "display";
    private static final String LORE_KEY = "Lore";


    @Redirect(method = "writeItemStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getTag()Lnet/minecraft/nbt/NbtCompound;"))
    @Environment(EnvType.SERVER)
    public NbtCompound add_shadow_lore(ItemStack instance) {
        NbtCompound ret = instance.getTag();
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
        }
        return ret;
    }

    @Inject(method = "readItemStack", at = @At(value = "RETURN"))
    public void filter_shadow_lore(CallbackInfoReturnable<ItemStack> cir) {
        ItemStack stack = cir.getReturnValue();
        String string;
        MutableText mutableText2;
        NbtCompound nbt = stack.getTag();
        if (nbt != null && nbt.contains(DISPLAY_KEY)) {
            String shadow_id = nbt.getString(ShadowItem.SHADOW_KEY);
            if (!shadow_id.equals("")){
                ((ShadowItem) (Object) stack).setShadowId(shadow_id);
            }
            NbtCompound display = nbt.getCompound(DISPLAY_KEY);
            if (display.contains(LORE_KEY)) {
                NbtList lore = display.getList(LORE_KEY, 8);
                for (int i = 0; i < lore.size(); ++i) {
                    string = lore.getString(i);
                    try {
                        mutableText2 = Text.Serializer.fromJson(string);
                        if (mutableText2 != null && mutableText2.asString().startsWith("shadow_id: ")) {
                            lore.remove(i);
                            if(((ShadowItem) (Object) stack).getShadowId() == null)
                                ((ShadowItem) (Object) stack).setShadowId(mutableText2.getSiblings().get(0).asString());
                            break;
                        }
                    } catch (JsonParseException ignored) {
                    }
                }
                if (lore.isEmpty())
                    display.remove(LORE_KEY);
                if (display.isEmpty())
                    nbt.remove(DISPLAY_KEY);
                if (nbt.isEmpty())
                    stack.setTag(null);
            }
        }

    }
}
