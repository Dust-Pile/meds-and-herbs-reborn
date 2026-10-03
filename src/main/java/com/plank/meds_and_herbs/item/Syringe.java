package com.plank.meds_and_herbs.item;

import com.plank.meds_and_herbs.data.MedicineDefinition;
import com.plank.meds_and_herbs.data.MedicineTypeLoader;
import com.plank.meds_and_herbs.data.MedsType;
import com.plank.meds_and_herbs.data.UseMedicine;
import com.plank.meds_and_herbs.init.MHDamageTypes;
import com.plank.meds_and_herbs.init.MHEffects;
import com.plank.meds_and_herbs.init.MHItems;
import com.plank.meds_and_herbs.util.MHUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Syringe extends Item {
    private static final double REACH_DISTANCE = 5.0;
    private static final int COOLDOWN_TICKS = 20;

    public Syringe() {
        super(new Properties().stacksTo(1).durability(16));
    }

    @Override
    public @Nonnull InteractionResult interactLivingEntity(@Nonnull ItemStack stack, @Nonnull Player player,
                                                           @Nonnull LivingEntity target, @Nonnull InteractionHand hand) {
        if (player.level().isClientSide) return InteractionResult.SUCCESS;
        if (player.getCooldowns().isOnCooldown(this)) return InteractionResult.FAIL;
        performInjection(player, target, hand);
        player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
        return InteractionResult.SUCCESS;
    }

    @Override
    public @Nonnull InteractionResultHolder<ItemStack> use(@Nonnull Level level, @Nonnull Player player, @Nonnull InteractionHand hand) {
        if (level.isClientSide) return InteractionResultHolder.consume(player.getItemInHand(hand));
        if (player.getCooldowns().isOnCooldown(this)) return InteractionResultHolder.fail(player.getItemInHand(hand));

        LivingEntity target;
        if (player.isShiftKeyDown()) {
            target = player;
        } else {
            target = getTargetEntity(player);
            if (target == null) {
                player.displayClientMessage(Component.translatable("message.meds_and_herbs.no_target"), true);
                player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
                return InteractionResultHolder.fail(player.getItemInHand(hand));
            }
        }

        performInjection(player, target, hand);
        player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

    private void performInjection(Player player, LivingEntity target, InteractionHand hand) {
        if (player.level().isClientSide) return;

        ItemStack syringe = player.getItemInHand(hand);
        ItemStack otherHand = hand == InteractionHand.MAIN_HAND ? player.getOffhandItem() : player.getMainHandItem();

        if (otherHand.getItem() instanceof Medkit) {
            if (!tryAutoTreatFromMedkit(player, target, syringe, otherHand)) {
                player.displayClientMessage(Component.translatable("message.meds_and_herbs.no_medicine_in_kit"), true);
            }
            return;
        }

        if (otherHand.getItem() == MHItems.MEDICINE_BOTTLE.get()) {
            List<ResourceLocation> availableBloodTypes = new ArrayList<>();
            if (target.hasEffect(MHEffects.ADRENALINE.get())) {
                availableBloodTypes.add(MedsType.ADRENALINE_BLOOD);
            }
            if (target.hasEffect(MobEffects.POISON)) {
                availableBloodTypes.add(MedsType.POISON_BLOOD);
            }
            if (target.hasEffect(MHEffects.BELLADONNA_BERRY.get())) {
                availableBloodTypes.add(MedsType.BELLADONNA_POISON_BLOOD);
            }
            if (target.hasEffect(MHEffects.HIGH_POTENCY_POISON.get())) {
                availableBloodTypes.add(MedsType.HIGH_POTENCY_POISON_BLOOD);
            }

            ResourceLocation bloodType = availableBloodTypes.isEmpty()
                    ? MedsType.BLOOD
                    : availableBloodTypes.get(player.getRandom().nextInt(availableBloodTypes.size()));

            ItemStack bloodBottle = Medicine.create(bloodType, 3);
            player.setItemInHand(hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND, bloodBottle);
            player.displayClientMessage(Component.translatable("message.meds_and_herbs.converted_to_blood"), true);

            MHUtils.hurtWithCustomType(target, MHDamageTypes.BLEEDING, 6.0f);
            syringe.hurtAndBreak(1, player, item -> {});
            return;
        }

        if (Medicine.isMedicineBottle(otherHand)) {
            var uses = Medicine.getUses(otherHand);
            if (uses <= 0) {
                player.displayClientMessage(Component.translatable("message.meds_and_herbs.bottle_empty"), true);
                return;
            }

            ResourceLocation typeId = Medicine.getType(otherHand);
            var def = MedicineTypeLoader.get(typeId);
            if (def != null && !def.isInternal()) {
                player.displayClientMessage(Component.translatable("message.meds_and_herbs.external_meds"), true);
                return;
            }

            UseMedicine.use(target, typeId);
            player.setItemInHand(hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND,
                    Medicine.consume(otherHand, player));
            syringe.hurtAndBreak(1, player, item -> {});

            var medicineName = Medicine.getTypeName(otherHand);
            player.displayClientMessage(Component.translatable("message.meds_and_herbs.injected",
                    target.getDisplayName(), medicineName), true);
            return;
        }

        player.displayClientMessage(Component.translatable("message.meds_and_herbs.need_bottle_or_medkit"), true);
    }

    private boolean tryAutoTreatFromMedkit(Player player, LivingEntity target, ItemStack syringe, ItemStack medkitStack) {
        CompoundTag medkitTag = medkitStack.getTag();
        if (medkitTag == null || !medkitTag.contains("MedkitContents", Tag.TAG_LIST)) return false;

        ListTag contentList = medkitTag.getList("MedkitContents", Tag.TAG_COMPOUND);
        List<ItemStack> items = new ArrayList<>(contentList.size());
        for (int i = 0; i < contentList.size(); i++) {
            ItemStack s = ItemStack.of(contentList.getCompound(i));
            if (!s.isEmpty()) items.add(s);
        }
        if (items.isEmpty()) return false;

        for (int i = 0; i < items.size(); i++) {
            ItemStack stack = items.get(i);
            if (!Medicine.isMedicineBottle(stack)) continue;

            int uses = Medicine.getUses(stack);
            if (uses <= 0) continue;

            ResourceLocation typeId = Medicine.getType(stack);
            MedicineDefinition def = MedicineTypeLoader.get(typeId);
            if (def == null || !def.isInternal()) continue;

            boolean canCure = def.cures().stream()
                    .map(BuiltInRegistries.MOB_EFFECT::get)
                    .filter(Objects::nonNull)
                    .anyMatch(target::hasEffect);
            if (!canCure) continue;

            Component medicineName = Medicine.getTypeName(stack);
            UseMedicine.use(target, typeId);
            items.set(i, Medicine.consume(stack, player));

            ListTag newContents = new ListTag();
            for (ItemStack s : items) {
                if (!s.isEmpty()) newContents.add(s.save(new CompoundTag()));
            }
            medkitStack.getOrCreateTag().put("MedkitContents", newContents);

            syringe.hurtAndBreak(1, player, item -> {});

            player.displayClientMessage(Component.translatable("message.meds_and_herbs.auto_treat",
                    target.getDisplayName(), medicineName), true);
            return true;
        }
        return false;
    }

    @Nullable
    private LivingEntity getTargetEntity(Player player) {
        double reach = REACH_DISTANCE;
        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getViewVector(1.0F);
        Vec3 endPos = eyePos.add(lookVec.scale(reach));
        AABB searchBox = player.getBoundingBox().expandTowards(lookVec.scale(reach)).inflate(1.0);

        LivingEntity result = null;
        double closestDist = reach + 1.0;

        for (net.minecraft.world.entity.Entity entity : player.level().getEntities(player, searchBox,
                e -> e instanceof LivingEntity && e != player && e.isAlive())) {
            AABB entityBox = entity.getBoundingBox().inflate(0.3);
            var hit = entityBox.clip(eyePos, endPos);
            if (hit.isPresent()) {
                double dist = eyePos.distanceTo(hit.get());
                if (dist < closestDist) {
                    closestDist = dist;
                    result = (LivingEntity) entity;
                }
            }
        }
        return result;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltip, isAdvanced);

        tooltip.add(Component.translatable("tooltip.meds_and_herbs.use").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.meds_and_herbs.sneak").withStyle(ChatFormatting.GRAY));
    }
}