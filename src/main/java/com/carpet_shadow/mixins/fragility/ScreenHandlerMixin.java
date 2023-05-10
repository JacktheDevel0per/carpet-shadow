package com.carpet_shadow.mixins.fragility;

import com.carpet_shadow.CarpetShadowSettings;
import com.carpet_shadow.Globals;
import com.carpet_shadow.interfaces.ShadowItem;
import com.carpet_shadow.interfaces.ShifingItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ScreenHandler.class)
public abstract class ScreenHandlerMixin {

    @Shadow
    public static boolean canInsertItemIntoSlot(@Nullable Slot slot, ItemStack stack, boolean allowOverflow) {
        return false;
    }


    @Redirect(method = "method_30010", slice = @Slice(
            from = @At(value = "INVOKE", target = "Lnet/minecraft/screen/slot/Slot;canTakeItems(Lnet/minecraft/entity/player/PlayerEntity;)Z", ordinal = 1)),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;setCursorStack(Lnet/minecraft/item/ItemStack;)V", ordinal = 1)
    )
    public void remove_shadow_stack(PlayerInventory instance, ItemStack stack) {
        String shadowId1 = ((ShadowItem) (Object) instance.getCursorStack()).getShadowId();
        String shadowId2 = ((ShadowItem) (Object) stack).getShadowId();
        if (CarpetShadowSettings.shadowItemInventoryFragilityFix && shadowId1 != null && shadowId1.equals(shadowId2)) {
            instance.setCursorStack(ItemStack.EMPTY);
        } else {
            instance.setCursorStack(stack);
        }
    }

    @Redirect(method = "method_30010", slice = @Slice(
            from = @At(value = "INVOKE", target = "Lnet/minecraft/screen/slot/Slot;canTakeItems(Lnet/minecraft/entity/player/PlayerEntity;)Z")
    ),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/ScreenHandler;transferSlot(Lnet/minecraft/entity/player/PlayerEntity;I)Lnet/minecraft/item/ItemStack;"))
    public ItemStack fix_shift(ScreenHandler instance, PlayerEntity player, int index) {
        if (CarpetShadowSettings.shadowItemInventoryFragilityFix) {
            Slot og = instance.slots.get(index);
            ItemStack og_item = og.getStack();
            if (((ShadowItem) (Object) og_item).getShadowId() != null) {
                ItemStack mirror = og_item.copy();
                ((ShadowItem) (Object) mirror).setShadowId(((ShadowItem) (Object) og_item).getShadowId());
                og.setStack(mirror);
                ((ShifingItem)(Object)mirror).setShiftMoving(true);
                ItemStack ret = instance.transferSlot(player, index);
                ((ShifingItem)(Object)mirror).setShiftMoving(false);
                if (ret == ItemStack.EMPTY) {
                    og_item = Globals.getByIdOrAdd(((ShadowItem) (Object) og_item).getShadowId(), og_item);
                    og.setStack(og_item);
                    og_item.setCount(mirror.getCount());
                }
                return ret;
            }
        }
        return instance.transferSlot(player, index);
    }

    @Redirect(method = "insertItem", slice = @Slice(
            from = @At(value = "INVOKE", target = "Lnet/minecraft/screen/slot/Slot;getStack()Lnet/minecraft/item/ItemStack;", ordinal = 1)
    ),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;split(I)Lnet/minecraft/item/ItemStack;", ordinal = 1))
    public ItemStack fix_shift(ItemStack instance, int amount) {
        if (CarpetShadowSettings.shadowItemInventoryFragilityFix && ((ShadowItem) (Object) instance).getShadowId() != null) {
            String shadow_id = ((ShadowItem) (Object) instance).getShadowId();
            ItemStack og_item = Globals.getByIdOrNull(shadow_id);
            if (og_item != null) {
                og_item.setCount(instance.getCount());
                instance.setCount(0);
                return og_item;
            }
        }
        return instance.split(amount);
    }

    @Redirect(method = "insertItem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z",ordinal = 0))
    public boolean fix_shift2(ItemStack instance) {
        if (CarpetShadowSettings.shadowItemInventoryFragilityFix && ((ShifingItem) (Object) instance).isShiftMoving()) {
            return true;
        }
        return instance.isEmpty();
    }

    @Redirect(method = "method_30010",
            slice = @Slice(
                    from = @At(value = "INVOKE",target = "Ljava/util/List;get(I)Ljava/lang/Object;"),
                    to = @At(value = "INVOKE",target = "Ljava/util/Set;add(Ljava/lang/Object;)Z")
            ),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/ScreenHandler;canInsertItemIntoSlot(Lnet/minecraft/screen/slot/Slot;Lnet/minecraft/item/ItemStack;Z)Z"))
    public boolean fixQuickCraft(Slot slot, ItemStack stack, boolean allowOverflow) {
        if (CarpetShadowSettings.shadowItemInventoryFragilityFix) {
            ItemStack slotStack = slot.getStack();
            ItemStack ref1 = Globals.getByIdOrNull(((ShadowItem) (Object) slotStack).getShadowId());
            ItemStack ref2 = Globals.getByIdOrNull(((ShadowItem) (Object) stack).getShadowId());
            if(slotStack == ref1 || stack == ref2)
                return false;
        }
        return canInsertItemIntoSlot(slot, stack, allowOverflow);
    }






    /**
    * BROKEN
     * Stack place back (click an empty slot)
     * @reason fix fragility issues having to do with Minecraft creating new ItemStacks (copies) when transferring items even if it is grabbing a full stack.
     */



    @Inject(method = "method_30010",at=@At(value = "INVOKE",target = "Lnet/minecraft/item/ItemStack;decrement(I)V"),locals = LocalCapture.CAPTURE_FAILEXCEPTION,cancellable = true)
    private void stopreInit(int i, int j, SlotActionType slotActionType, PlayerEntity playerEntity, CallbackInfoReturnable<ItemStack> cir, ItemStack itemStack, PlayerInventory playerInventory, Slot slot3, ItemStack itemStack3, ItemStack itemStack2, int o) {


        if (!CarpetShadowSettings.shadowItemInventoryFragilityFix) return;



        if (itemStack2.getCount() == o) {
            itemStack3 = itemStack2;

            itemStack2 = ItemStack.EMPTY;
            cir.cancel();

            slot3.markDirty();
        }

    }


/**
     *
     * Stack Pickup
     * @reason fix fragility issues having to do with Minecraft creating new ItemStacks (copies) when transferring items even if it is grabbing a full stack.
     */

    @Redirect(method = "method_30010",at=@At(value = "INVOKE",target = "Lnet/minecraft/screen/slot/Slot;takeStack(I)Lnet/minecraft/item/ItemStack;"))
    public ItemStack takeNoCopy(Slot instance, int amount) {
        if (!CarpetShadowSettings.shadowItemInventoryFragilityFix) return instance.takeStack(amount);

        ItemStack currentStack = instance.getStack();


        if (amount == currentStack.getCount()) {
            instance.setStack(ItemStack.EMPTY);
            return currentStack;
        }


        return instance.takeStack(amount);
    }



}
