Java
package com.example.custommod.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LymeDiseaseStatusEffect extends StatusEffect {
    private static final UUID SLOWNESS_ID = UUID.fromString("7107DE5E-7CE8-4030-940E-514C1F160890");
    private final Map<UUID, Double> exertionTracker = new HashMap<>();

    public LymeDiseaseStatusEffect() {
        super(StatusEffectCategory.HARMFUL, 0x556B2F);
        this.addAttributeModifier(
                EntityAttributes.GENERIC_MOVEMENT_SPEED,
                SLOWNESS_ID.toString(),
                -0.20D,
                EntityAttributeModifier.Operation.MULTIPLY_TOTAL
        );
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (!entity.getWorld().isClient && entity instanceof PlayerEntity player) {
            if (!player.isCreative() && !player.isSpectator()) {
                double speed = player.getVelocity().horizontalLength();
                if (speed > 0.05D) {
                    double accumulated = exertionTracker.getOrDefault(player.getUuid(), 0.0D) + speed;
                    if (accumulated >= 25.0D) {
                        player.damage(player.getDamageSources().create(DamageTypes.GENERIC), 2.0F + amplifier);
                        accumulated = 0.0D;
                    }
                    exertionTracker.put(player.getUuid(), accumulated);
                }
            }
        }
    }
}
