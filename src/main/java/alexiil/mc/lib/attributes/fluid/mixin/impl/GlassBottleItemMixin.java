/*
 * Copyright (c) 2019 AlexIIL
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package alexiil.mc.lib.attributes.fluid.mixin.impl;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.item.GlassBottleItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;

import alexiil.mc.lib.attributes.fluid.FluidProviderItem;
import alexiil.mc.lib.attributes.fluid.FluidVolumeUtil;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.mixin.api.IBucketItem;
import alexiil.mc.lib.attributes.fluid.volume.FluidKey;
import alexiil.mc.lib.attributes.fluid.volume.FluidKeys;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import alexiil.mc.lib.attributes.fluid.volume.PotionFluidKey;
import alexiil.mc.lib.attributes.fluid.volume.PotionFluidVolume;
import alexiil.mc.lib.attributes.misc.Ref;

@Mixin(GlassBottleItem.class)
public class GlassBottleItemMixin extends Item implements FluidProviderItem, IBucketItem {

    public GlassBottleItemMixin(Item.Settings settings) {
        super(settings);
    }

    @Override
    public FluidVolume drain(Ref<ItemStack> stack) {
        return FluidVolumeUtil.EMPTY;
    }

    @Override
    public boolean fill(Ref<ItemStack> stack, Ref<FluidVolume> with) {
        if (stack.obj.getCount() != 1) {
            return false;
        }
        if (with.obj.getAmount_F().isLessThan(FluidAmount.BOTTLE)) {
            return false;
        }
        final Potion potion;
        if (with.obj instanceof PotionFluidVolume) {
            potion = ((PotionFluidVolume) with.obj).getPotion();
        } else if (with.obj.fluidKey == FluidKeys.WATER) {
            potion = Potions.WATER;
        } else {
            return false;
        }
        with.obj = with.obj.copy();
        FluidVolume split = with.obj.split(FluidAmount.BOTTLE);
        if (!split.isEmpty()) {
            ItemStack potionStack = new ItemStack(Items.POTION);
            PotionUtil.setPotion(potionStack, potion);
            stack.obj = potionStack;
            return true;
        }
        return false;
    }

    @Override
    public boolean libblockattributes__shouldExposeFluid() {
        return true;
    }

    @Override
    public FluidKey libblockattributes__getFluid(ItemStack stack) {
        return FluidKeys.EMPTY;
    }

    @Override
    public ItemStack libblockattributes__withFluid(FluidKey fluid) {
        Potion potion;
        if (fluid instanceof PotionFluidKey) {
            potion = ((PotionFluidKey) fluid).potion;
        } else if (fluid == FluidKeys.WATER) {
            potion = Potions.WATER;
        } else if (fluid == FluidKeys.EMPTY) {
            return new ItemStack(Items.GLASS_BOTTLE);
        } else {
            return ItemStack.EMPTY;
        }
        ItemStack potionStack = new ItemStack(Items.POTION);
        PotionUtil.setPotion(potionStack, potion);
        return potionStack;
    }

    @Override
    public FluidAmount libblockattributes__getFluidVolumeAmount() {
        return FluidAmount.BOTTLE;
    }
}
