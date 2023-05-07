package com.carpet_shadow.mixins.tooltip;


import com.carpet_shadow.interfaces.ShadowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ScreenHandler.class)
public abstract class ScreenHandlerMixin {


    @Redirect(method = "sendContentUpdates", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;copy()Lnet/minecraft/item/ItemStack;", ordinal = 0))
    public ItemStack copy_redirect(ItemStack instance) {
        return ShadowItem.copy_redirect(instance);
    }

}
