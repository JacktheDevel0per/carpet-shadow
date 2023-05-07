package com.carpet_shadow.mixins.inv_updates.loaders;


import com.carpet_shadow.interfaces.InventoryItem;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.Structure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Structure.class)
public abstract class StructureMixin {

    @Redirect(method = "place(Lnet/minecraft/world/ServerWorldAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/structure/StructurePlacementData;Ljava/util/Random;I)Z",at=@At(value = "INVOKE",target = "Lnet/minecraft/block/entity/BlockEntity;fromTag(Lnet/minecraft/block/BlockState;Lnet/minecraft/nbt/NbtCompound;)V"))
    public void interceptBlockEntityLoad(BlockEntity instance, BlockState state, NbtCompound tag){
        InventoryItem.fromTag(instance, state, tag);
    }


}
