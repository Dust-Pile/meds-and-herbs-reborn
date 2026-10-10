package com.dusty_dusty.meds_and_herbs.item;

import com.dusty_dusty.meds_and_herbs.init.MHTags;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.BundleTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Medkit extends BundleItem {

    private static final int MAX_ITEMS = 16;

    public Medkit() {
        super(new Properties().stacksTo(1));
    }

    @Override
    @Nonnull
    public InteractionResultHolder<ItemStack> use(@Nonnull Level level, @Nonnull Player player, @Nonnull InteractionHand hand) {
        ItemStack medkit = player.getItemInHand(hand);
        if (medkit.getCount() != 1) {
            return InteractionResultHolder.pass(medkit);
        }

        if (player.isShiftKeyDown()) {
            if (!level.isClientSide) {
                dropAllItems(player, medkit);
            }
            return InteractionResultHolder.sidedSuccess(medkit, level.isClientSide());
        }

        return InteractionResultHolder.sidedSuccess(medkit, level.isClientSide());
    }

    private void dropAllItems(Player player, ItemStack medkit) {
        List<ItemStack> contents = getContents(medkit);
        if (contents.isEmpty()) return;

        setContents(medkit, List.of());

        for (ItemStack stack : contents) {
            if (!stack.isEmpty()) {
                player.drop(stack, false, false);
            }
        }

        player.playSound(SoundEvents.BUNDLE_DROP_CONTENTS, 1.0F, 1.0F);
    }

    private List<ItemStack> getContents(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains("MedkitContents", Tag.TAG_LIST)) {
            return new ArrayList<>();
        }
        ListTag list = tag.getList("MedkitContents", Tag.TAG_COMPOUND);
        List<ItemStack> out = new ArrayList<>(list.size());
        for (int i = 0; i < list.size(); i++) {
            ItemStack s = ItemStack.of(list.getCompound(i));
            if (!s.isEmpty()) out.add(s);
        }
        return out;
    }

    private void setContents(ItemStack stack, List<ItemStack> contents) {
        ListTag list = new ListTag();
        for (ItemStack s : contents) {
            if (!s.isEmpty()) list.add(s.save(new CompoundTag()));
        }
        stack.getOrCreateTag().put("MedkitContents", list);
    }

    private static int totalCount(List<ItemStack> contents) {
        int total = 0;
        for (ItemStack s : contents) total += s.getCount();
        return total;
    }

    private static ItemStack removeOneFromContents(List<ItemStack> contents) {
        for (int i = contents.size() - 1; i >= 0; i--) {
            ItemStack s = contents.get(i);
            if (s.isEmpty()) continue;
            ItemStack removed = s.copyWithCount(1);
            s.shrink(1);
            if (s.isEmpty()) contents.remove(i);
            return removed;
        }
        return ItemStack.EMPTY;
    }

    private static boolean tryInsertIntoContents(List<ItemStack> contents, ItemStack toInsert, int maxItems) {
        if (totalCount(contents) >= maxItems) return false;
        if (toInsert.isEmpty()) return false;

        for (ItemStack existing : contents) {
            if (ItemStack.isSameItemSameTags(existing, toInsert)
                    && existing.getCount() < existing.getMaxStackSize()) {
                existing.grow(1);
                toInsert.shrink(1);
                return true;
            }
        }

        contents.add(toInsert.copyWithCount(1));
        toInsert.shrink(1);
        return true;
    }


    @Override
    public boolean overrideStackedOnOther(ItemStack medkit, @Nonnull Slot slot, @Nonnull ClickAction action, @Nonnull Player player) {
        if (medkit.getCount() != 1) return false;
        if (action != ClickAction.SECONDARY) return false;

        ItemStack stackInSlot = slot.getItem();
        if (stackInSlot.isEmpty()) {
            return tryExtractOne(medkit, slot, player);
        } else {
            return tryInsertOne(medkit, stackInSlot, player);
        }
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack medkit, @Nonnull ItemStack other, @Nonnull Slot slot, @Nonnull ClickAction action, @Nonnull Player player, @Nonnull SlotAccess access) {
        if (medkit.getCount() != 1) return false;
        if (action != ClickAction.SECONDARY) return false;

        if (other.isEmpty()) {
            // 从医疗包中取出一个物品到目标槽位
            return tryExtractOneToSlot(medkit, player, access);
        } else {
            // 尝试将 other 放入医疗包
            return tryInsertOne(medkit, other, player);
        }
    }

    private boolean tryExtractOne(ItemStack medkit, Slot slot, Player player) {
        List<ItemStack> contents = getContents(medkit);
        if (contents.isEmpty()) return false;

        ItemStack extracted = removeOneFromContents(contents);
        if (extracted.isEmpty()) return false;

        ItemStack leftover = slot.safeInsert(extracted);
        if (!leftover.isEmpty()) {
            tryInsertIntoContents(contents, leftover, 16);
        }

        setContents(medkit, contents);
        playRemoveOneSound(player);
        return true;
    }

    private boolean tryExtractOneToSlot(ItemStack medkit, Player player, SlotAccess access) {
        List<ItemStack> contents = getContents(medkit);
        if (contents.isEmpty()) return false;

        ItemStack extracted = removeOneFromContents(contents);
        if (extracted.isEmpty()) return false;

        access.set(extracted);
        setContents(medkit, contents);
        playRemoveOneSound(player);
        return true;
    }

    private boolean tryInsertOne(ItemStack medkit, ItemStack toInsert, Player player) {
        if (!toInsert.is(MHTags.Items.MEDKIT_ITEMS)) return false;

        List<ItemStack> contents = getContents(medkit);
        if (totalCount(contents) >= MAX_ITEMS) return false;

        if (!tryInsertIntoContents(contents, toInsert, MAX_ITEMS)) return false;

        setContents(medkit, contents);
        playInsertSound(player);
        return true;
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nonnull Level level, List<Component> tooltip, @Nonnull TooltipFlag flag) {
        List<ItemStack> contents = getContents(stack);
        int filled = totalCount(contents);
        tooltip.add(Component.translatable("item.minecraft.bundle.fullness", filled, MAX_ITEMS)
                .withStyle(ChatFormatting.GRAY));
    }

    @Override
    @Nonnull
    public Optional<TooltipComponent> getTooltipImage(@Nonnull ItemStack stack) {
        var tag = stack.getTag();
        if (tag != null && (tag.getInt("HideFlags") & 32) != 0) {
            return Optional.empty();
        }
        NonNullList<ItemStack> items = NonNullList.create();
        for (ItemStack s : getContents(stack)) {
            if (!s.isEmpty()) items.add(s);
        }
        return Optional.of(new BundleTooltip(items, totalCount(items)));
    }

    @Override
    public boolean isBarVisible(@Nonnull ItemStack stack) {
        return !getContents(stack).isEmpty();
    }

    @Override
    public int getBarWidth(@Nonnull ItemStack stack) {
        int filled = totalCount(getContents(stack));
        return Math.round(13.0f * filled / MAX_ITEMS);
    }

    @Override
    public int getBarColor(@Nonnull ItemStack stack) {
        return 0x4C6A9B;
    }

    private void playRemoveOneSound(Player player) {
        player.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F,
                0.8F + player.level().getRandom().nextFloat() * 0.4F);
    }

    private void playInsertSound(Player player) {
        player.playSound(SoundEvents.BUNDLE_INSERT, 0.8F,
                0.8F + player.level().getRandom().nextFloat() * 0.4F);
    }
}