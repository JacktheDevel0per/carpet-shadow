package com.carpet_shadow.mixins.hand_update_fix;

import com.carpet_shadow.CarpetShadowServerSettings;
import com.carpet_shadow.interfaces.ShadowItem;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ServerPlayerInteractionManager.class)
public abstract class ServerPlayerInteractionManagerMixin {

    @Shadow public abstract boolean isCreative();

    //@Inject(method = "interactBlock", at=@At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/item/ItemStack;useOnBlock(Lnet/minecraft/item/ItemUsageContext;)Lnet/minecraft/util/ActionResult;"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    @SuppressWarnings("InvalidInjectorMethodSignature")
    @Inject(method = "interactBlock", at=@At(value = "INVOKE_ASSIGN",target = "Lnet/minecraft/block/BlockState;onUse(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/util/hit/BlockHitResult;)Lnet/minecraft/util/ActionResult;"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void inject_on_block_use(ServerPlayerEntity player, World world, ItemStack stack, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir, BlockPos blockPos, BlockState blockState, boolean bl, boolean bl2, ItemStack itemStack, ActionResult actionResult/*, BlockState state, World world2, PlayerEntity player2, Hand hand2, BlockHitResult hitResult2*/){
        if(CarpetShadowServerSettings.shadowItemUseFix && ((ShadowItem)(Object)stack).getShadowId() != null && !isCreative()) {
            switch (actionResult) {
                case SUCCESS, CONSUME -> {
                    int index = (hand == Hand.OFF_HAND) ? 40 : player.inventory.selectedSlot;
                    player.currentScreenHandler.getSlot(index);
                    //player.currentScreenHandler.setPreviousTrackedSlot(i, new ItemStack(Blocks.AIR));
                }
            }
        }
    }

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @Inject(method = "interactItem", at=@At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/item/ItemStack;use(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/TypedActionResult;"),locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void inject_on_item_use2(ServerPlayerEntity player, World world, ItemStack stack, Hand hand, CallbackInfoReturnable<ActionResult> cir, int i, int j, TypedActionResult<ItemStack> typedActionResult){
        if(CarpetShadowServerSettings.shadowItemUseFix && ((ShadowItem)(Object)stack).getShadowId() != null && !isCreative()) {
            switch (typedActionResult.getResult()) {
                case SUCCESS, CONSUME -> {
                    int index = (hand == Hand.OFF_HAND) ? 40 : player.inventory.selectedSlot;
                    player.currentScreenHandler.getSlot(index);
                    //player.currentScreenHandler.setPreviousTrackedSlot(index, new ItemStack(Blocks.AIR));
                }
            }
        }
    }
}
