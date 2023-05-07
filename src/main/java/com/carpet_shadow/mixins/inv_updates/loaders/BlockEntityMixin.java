package com.carpet_shadow.mixins.inv_updates.loaders;


import com.carpet_shadow.interfaces.InventoryItem;
import com.carpet_shadow.interfaces.ShadowItem;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.command.BlockDataObject;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockEntity.class)
public abstract class BlockEntityMixin {

    @Inject(method = "createFromTag",at=@At("RETURN"))
    private static void interceptBlockEntityLoad(BlockState state, NbtCompound tag, CallbackInfoReturnable<BlockEntity> cir){
        if(cir.getReturnValue() instanceof Inventory inv){
            try {
                for (int index = 0; index < inv.size(); index++) {
                    ItemStack stack = inv.getStack(index);
                    if (((ShadowItem) (Object) stack).getShadowId() != null) {
                        ((InventoryItem) (Object) stack).addSlot(inv, index);
                    }
                }
            }catch(Exception ignored){}
        }
    }

}
