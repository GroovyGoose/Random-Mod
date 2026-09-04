Java
package com.example.custommod.item;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import net.minecraft.client.item.TooltipContext;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LifeDiaperItem extends ArmorItem {
    public LifeDiaperItem(Settings settings) {
        super(ArmorMaterials.LEATHER, Type.LEGGINGS, settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("§ePops to prevent lethal blows and grant emergency absorption."));
    }
}