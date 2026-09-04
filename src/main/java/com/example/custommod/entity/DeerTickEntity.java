Java
package com.example.custommod.entity;

import com.example.custommod.CustomMod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class DeerTickEntity extends HostileEntity {
    public DeerTickEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 2;
    }

    public static DefaultAttributeContainer.Builder createTickAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 4.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.22D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 1.0D)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 12.0D);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new PounceAtTargetGoal(this, 0.25F));
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.15D, false));
        this.goalSelector.add(3, new WanderAroundFarGoal(this, 0.7D));
        this.goalSelector.add(4, new LookAroundGoal(this));

        this.targetSelector.add(1, new RevengeGoal(this));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public EntityGroup getGroup() {
        return EntityGroup.ARTHROPOD;
    }

    @Override
    public boolean tryAttack(Entity target) {
        boolean attacked = super.tryAttack(target);
        if (attacked && target instanceof LivingEntity living) {
            if (this.random.nextFloat() < 0.75F) {
                living.addStatusEffect(new StatusEffectInstance(CustomMod.LYME_DISEASE, 12000, 0), this);
            }
            this.playSound(SoundEvents.ENTITY_SPIDER_STEP, 0.6F, 2.0F);
        }
        return attacked;
    }

    @Override
    protected void dropEquipment(DamageSource source, int lootingMultiplier, boolean allowDrops) {
        super.dropEquipment(source, lootingMultiplier, allowDrops);
        this.dropItem(CustomMod.BUGS_ITEM);
    }
}
src/main/java/com/example/custommod/client/model/DeerTickModel.java

Java
package com.example.custommod.client.model;

import com.example.custommod.entity.DeerTickEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

public class DeerTickModel extends EntityModel<DeerTickEntity> {
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart legs;

    public DeerTickModel(ModelPart root) {
        this.body = root.getChild("body");
        this.head = root.getChild("head");
        this.legs = root.getChild("legs");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();

        root.addChild("body", ModelPartBuilder.create()
                        .uv(0, 0).cuboid(-2.0F, -1.0F, -2.0F, 4.0F, 2.0F, 5.0F),
                ModelTransform.pivot(0.0F, 23.0F, 0.0F));

        root.addChild("head", ModelPartBuilder.create()
                        .uv(0, 7).cuboid(-1.0F, -0.5F, -3.5F, 2.0F, 1.0F, 2.0F),
                ModelTransform.pivot(0.0F, 23.0F, 0.0F));

        root.addChild("legs", ModelPartBuilder.create()
                        .uv(0, 10).cuboid(-3.5F, 0.0F, -2.0F, 7.0F, 1.0F, 4.0F),
                ModelTransform.pivot(0.0F, 23.0F, 0.0F));

        return TexturedModelData.of(modelData, 16, 16);
    }

    @Override
    public void setAngles(DeerTickEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.head.yaw = headYaw * 0.017453292F;
        this.legs.yaw = MathHelper.sin(animationProgress * 0.8F) * 0.2F;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        body.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        head.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        legs.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }
}