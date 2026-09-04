Java
package com.example.custommod.entity;

import com.example.custommod.CustomMod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EnderClownEntity extends HostileEntity {
    public EnderClownEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 12;
    }

    public static DefaultAttributeContainer.Builder createEnderClownAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 40.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.34D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 7.0D)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0D);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.2D, false));
        this.goalSelector.add(2, new WanderAroundFarGoal(this, 1.0D));
        this.goalSelector.add(3, new LookAroundGoal(this));
        this.targetSelector.add(1, new RevengeGoal(this));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public boolean tryAttack(Entity target) {
        boolean attacked = super.tryAttack(target);
        if (attacked && target instanceof PlayerEntity player) {
            Vec3d vel = player.getVelocity();
            player.setVelocity(-vel.x * 2.0D, 0.4D, -vel.z * 2.0D);
            player.velocityModified = true;
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 120, 0), this);
            teleportRandomly();
        }
        return attacked;
    }

    private void teleportRandomly() {
        for (int i = 0; i < 16; ++i) {
            double tx = this.getX() + (this.random.nextDouble() - 0.5D) * 24.0D;
            double ty = MathHelper.clamp(this.getY() + (this.random.nextInt(16) - 8), this.getWorld().getBottomY(), this.getWorld().getTopY());
            double tz = this.getZ() + (this.random.nextDouble() - 0.5D) * 24.0D;
            if (this.teleport(tx, ty, tz, true)) {
                this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                        SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.HOSTILE, 1.0F, 1.4F);
                break;
            }
        }
    }

    @Override
    protected void dropEquipment(DamageSource source, int lootingMultiplier, boolean allowDrops) {
        super.dropEquipment(source, lootingMultiplier, allowDrops);
        this.dropItem(Items.ENDER_PEARL, this.random.nextInt(2 + lootingMultiplier));
        this.dropItem(CustomMod.RUBBER, this.random.nextInt(3 + lootingMultiplier) + 1);
        if (this.random.nextFloat() < 0.15F) this.dropItem(CustomMod.PARTY_HAT);
    }
}