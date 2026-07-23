package com.plank.meds_and_herbs.item;

import com.plank.meds_and_herbs.init.DataComponents;
import com.plank.meds_and_herbs.init.Tags;
import com.plank.meds_and_herbs.data.MedkitContents;
import net.minecraft.ChatFormatting;
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
import net.minecraft.world.item.component.BundleContents;
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

    // ==================== 右键使用（潜行→丢弃所有，否则无动作） ====================
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

        // 非潜行：无动作
        return InteractionResultHolder.sidedSuccess(medkit, level.isClientSide());
    }

    // ==================== 丢弃所有物品 ====================
    private void dropAllItems(Player player, ItemStack medkit) {
        MedkitContents contents = getContents(medkit);
        if (contents.isEmpty()) return;

        // 复制物品列表，清空医疗包后再抛出
        List<ItemStack> items = new ArrayList<>();
        contents.itemsCopy().forEach(items::add);
        setContents(medkit, MedkitContents.EMPTY);

        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                player.drop(stack, false, false);
            }
        }

        player.playSound(SoundEvents.BUNDLE_DROP_CONTENTS, 1.0F, 1.0F);
    }

    // ==================== 组件操作 ====================
    private MedkitContents getContents(ItemStack stack) {
        return stack.getOrDefault(DataComponents.MEDKIT_CONTENTS, MedkitContents.EMPTY);
    }

    private void setContents(ItemStack stack, MedkitContents contents) {
        stack.set(DataComponents.MEDKIT_CONTENTS, contents);
    }

    // ==================== 交互：右键点击其他槽位（鼠标上有医疗包时） ====================
    @Override
    public boolean overrideStackedOnOther(ItemStack medkit, @Nonnull Slot slot, @Nonnull ClickAction action, @Nonnull Player player) {
        if (medkit.getCount() != 1) return false;
        if (action != ClickAction.SECONDARY) return false;

        ItemStack stackInSlot = slot.getItem();
        if (stackInSlot.isEmpty()) {
            // 从医疗包中取出一个物品到鼠标
            return tryExtractOne(medkit, slot, player);
        } else {
            // 尝试将槽位中的物品放入医疗包
            return tryInsertOne(medkit, stackInSlot, player);
        }
    }

    // ==================== 交互：右键点击医疗包槽位（鼠标上有物品时） ====================
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

    // ==================== 核心操作 ====================

    // 从医疗包中取出一个物品，放入鼠标（原槽位为空）
    private boolean tryExtractOne(ItemStack medkit, Slot slot, Player player) {
        MedkitContents contents = getContents(medkit);
        if (contents.isEmpty()) return false;

        MedkitContents.Mutable mutable = new MedkitContents.Mutable(contents);
        ItemStack extracted = mutable.removeOne();
        if (extracted.isEmpty()) return false;

        // 尝试放入槽位（如果放不下，则剩余部分放回）
        ItemStack leftover = slot.safeInsert(extracted);
        if (!leftover.isEmpty()) {
            mutable.tryInsert(leftover);
        }
        setContents(medkit, mutable.toImmutable());
        playRemoveOneSound(player);
        return true;
    }

    // 从医疗包中取出一个物品，放入指定槽位（用于 overrideOtherStackedOnMe）
    private boolean tryExtractOneToSlot(ItemStack medkit, Player player, SlotAccess access) {
        MedkitContents contents = getContents(medkit);
        if (contents.isEmpty()) return false;

        MedkitContents.Mutable mutable = new MedkitContents.Mutable(contents);
        ItemStack extracted = mutable.removeOne();
        if (extracted.isEmpty()) return false;

        access.set(extracted);
        setContents(medkit, mutable.toImmutable());
        playRemoveOneSound(player);
        return true;
    }

    // 尝试将一个物品放入医疗包
    private boolean tryInsertOne(ItemStack medkit, ItemStack toInsert, Player player) {
        // 检查物品是否允许放入医疗包（通过标签）
        if (!toInsert.is(Tags.Items.MEDKIT_ITEMS)) return false;

        MedkitContents contents = getContents(medkit);
        if (contents.totalCount() >= MAX_ITEMS) return false;

        MedkitContents.Mutable mutable = new MedkitContents.Mutable(contents);
        if (!mutable.tryInsert(toInsert)) return false;

        setContents(medkit, mutable.toImmutable());
        playInsertSound(player);
        return true;
    }

    // ==================== 工具提示 ====================
    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nonnull TooltipContext context, List<Component> tooltip, @Nonnull TooltipFlag flag) {
        MedkitContents contents = getContents(stack);
        int filled = contents.totalCount();
        tooltip.add(Component.translatable("item.minecraft.bundle.fullness", filled, MAX_ITEMS)
                .withStyle(ChatFormatting.GRAY));
    }

    // ==================== 悬浮显示物品列表 ====================
    @Override
    @Nonnull
    public Optional<TooltipComponent> getTooltipImage(@Nonnull ItemStack stack) {
        if (stack.has(net.minecraft.core.component.DataComponents.HIDE_TOOLTIP)) {
            return Optional.empty();
        }
        List<ItemStack> items = new ArrayList<>();
        getContents(stack).itemsCopy().forEach(items::add);
        return Optional.of(new BundleTooltip(new BundleContents(items)));
    }

    // ==================== 耐久条（显示填充度） ====================
    @Override
    public boolean isBarVisible(@Nonnull ItemStack stack) {
        return !getContents(stack).isEmpty();
    }

    @Override
    public int getBarWidth(@Nonnull ItemStack stack) {
        int filled = getContents(stack).totalCount();
        return Math.round(13.0f * filled / MAX_ITEMS);
    }

    @Override
    public int getBarColor(@Nonnull ItemStack stack) {
        return 0x4C6A9B; // 蓝色
    }

    // ==================== 音效 ====================
    private void playRemoveOneSound(Player player) {
        player.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F,
                0.8F + player.level().getRandom().nextFloat() * 0.4F);
    }

    private void playInsertSound(Player player) {
        player.playSound(SoundEvents.BUNDLE_INSERT, 0.8F,
                0.8F + player.level().getRandom().nextFloat() * 0.4F);
    }
}