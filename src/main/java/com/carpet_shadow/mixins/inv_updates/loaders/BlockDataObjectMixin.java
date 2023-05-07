package com.carpet_shadow.mixins.inv_updates.loaders;


import com.carpet_shadow.interfaces.InventoryItem;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.command.BlockDataObject;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockDataObject.class)
public abstract class BlockDataObjectMixin {

    @Redirect(method = "setNbt",at=@At(value = "INVOKE",target = "Lnet/minecraft/block/entity/BlockEntity;fromTag(Lnet/minecraft/block/BlockState;Lnet/minecraft/nbt/NbtCompound;)V"))
    private void interceptBlockEntityLoad(BlockEntity instance, BlockState state, NbtCompound tag){
        InventoryItem.fromTag(instance, state, tag);
    }


}
