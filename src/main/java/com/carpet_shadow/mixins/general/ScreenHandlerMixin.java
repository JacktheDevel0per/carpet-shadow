package com.carpet_shadow.mixins.general;

import com.carpet_shadow.CarpetShadow;
import com.carpet_shadow.CarpetShadowSettings;
import com.carpet_shadow.Globals;
import com.carpet_shadow.interfaces.ShadowItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.crash.CrashException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

import java.lang.ref.WeakReference;

@Mixin(ScreenHandler.class)
public abstract class ScreenHandlerMixin {

    @Shadow public abstract Slot getSlot(int index);


    @Shadow protected abstract ItemStack method_30010(int i, int j, SlotActionType slotActionType, PlayerEntity playerEntity);

    @Redirect(method = "onSlotClick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/ScreenHandler;method_30010(IILnet/minecraft/screen/slot/SlotActionType;Lnet/minecraft/entity/player/PlayerEntity;)Lnet/minecraft/item/ItemStack;"),
            require = 0)
    private ItemStack handle_shadowing(ScreenHandler instance, int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {

        try {
            return method_30010(slotIndex, button, actionType, player);
        } catch (Throwable error) {
            if(actionType!=SlotActionType.SWAP && actionType!=SlotActionType.PICKUP && actionType!=SlotActionType.QUICK_CRAFT) {
                throw error;

            }



            ItemStack stack1 = this.getSlot(slotIndex).getStack();
            ItemStack stack2 = player.inventory.getStack(button);
            ItemStack stack3 = player.inventory.getCursorStack();
            ItemStack shadow = null;
            if(stack1 == stack2 || stack1 == stack3)
                shadow = stack1;
            else if (stack2 == stack3)
                shadow = stack2;


            if(shadow != null){
                CarpetShadow.LOGGER.warn("New Shadow Item Created");
                String shadow_id = ((ShadowItem) (Object) shadow).getShadowId();
                if (shadow_id == null)
                    shadow_id = CarpetShadow.shadow_id_generator.nextString();
                Globals.getByIdOrAdd(shadow_id,shadow);
                if (CarpetShadowSettings.shadowItemMode == CarpetShadowSettings.Mode.UNLINK) {
                    throw error;
                }
            }

            return stack1;
        }
        //return ItemStack.EMPTY;
    }
}
