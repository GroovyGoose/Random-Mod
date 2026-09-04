Java
package com.example.custommod.mixin;

import com.example.custommod.CustomMod;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityDamageMixin {
    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void popLifeDiaper(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity instanceof PlayerEntity player) {
            ItemStack legs = player.getEquippedStack(EquipmentSlot.LEGS);
            if (legs.isOf(CustomMod.LIFE_DIAPER) && (player.getHealth() - amount <= 0.0F)) {
                cir.setReturnValue(false);
                legs.decrement(1);
                player.setHealth(4.0F);
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 300, 1));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 200, 1));
                player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.ITEM_TOTEM_USE, SoundCategory.PLAYERS, 1.0F, 1.5F);
            }
        }
    }
}