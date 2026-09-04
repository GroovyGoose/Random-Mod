Java
package com.example.custommod.entity;

import com.example.custommod.CustomMod;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class OverworldClownEntity extends HostileEntity {
    private int honkCooldown = 0;

    public OverworldClownEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 5;
    }

    public static DefaultAttributeContainer.Builder createClownAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.28D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 3.0D)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 20.0D);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.15D, false));
        this.goalSelector.add(2, new WanderAroundFarGoal(this, 0.8D));
        this.goalSelector.add(3, new LookAroundGoal(this));
        this.targetSelector.add(1, new RevengeGoal(this));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        if (!this.getWorld().isClient && this.getTarget() != null && --this.honkCooldown <= 0) {
            if (this.squaredDistanceTo(this.getTarget()) < 25.0D) {
                this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                        SoundEvents.ENTITY_PIG_HURT, SoundCategory.HOSTILE, 1.4F, 2.0F);
                for (PlayerEntity player : this.getWorld().getEntitiesByClass(PlayerEntity.class, this.getBoundingBox().expand(5.0D), p -> true)) {
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 120, 0));
                }
                this.honkCooldown = 160;
            }
        }
    }

    @Override
    protected void dropEquipment(DamageSource source, int lootingMultiplier, boolean allowDrops) {
        super.dropEquipment(source, lootingMultiplier, allowDrops);
        this.dropItem(CustomMod.RUBBER, this.random.nextInt(2 + lootingMultiplier) + 1);
        if (this.random.nextFloat() < 0.20F) this.dropItem(CustomMod.RUBBER_NOSE);
        if (this.random.nextFloat() < 0.35F) this.dropItem(CustomMod.BALLOON);
    }
}