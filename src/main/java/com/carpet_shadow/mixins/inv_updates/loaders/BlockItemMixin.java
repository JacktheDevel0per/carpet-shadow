package com.carpet_shadow.mixins.inv_updates.loaders;


import com.carpet_shadow.interfaces.InventoryItem;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin {

    @Redirect(method = "writeTagToBlockEntity",at=@At(value = "INVOKE",target = "Lnet/minecraft/block/entity/BlockEntity;fromTag(Lnet/minecraft/block/BlockState;Lnet/minecraft/nbt/NbtCompound;)V"))
    private static void interceptBlockEntityLoad(BlockEntity instance, BlockState state, NbtCompound tag){
        InventoryItem.fromTag(instance, state, tag);
    }


}
