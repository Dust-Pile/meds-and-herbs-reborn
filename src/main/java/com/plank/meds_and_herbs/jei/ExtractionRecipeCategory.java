package com.plank.meds_and_herbs.jei;

import com.plank.meds_and_herbs.MedsAndHerbs;
import com.plank.meds_and_herbs.init.Blocks;
import com.plank.meds_and_herbs.init.Items;
import com.plank.meds_and_herbs.init.Tags;
import com.plank.meds_and_herbs.recipe.ExtractionRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class ExtractionRecipeCategory implements IRecipeCategory<ExtractionRecipe> {

    private final IDrawable icon;
    private final IDrawable slotBackground;
    private final IDrawable arrowBackground;
    private final IDrawableAnimated arrow;

    public ExtractionRecipeCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(Blocks.EXTRACTION_APPARATUS.get()));
        this.slotBackground = guiHelper.getSlotDrawable();
        this.arrowBackground = guiHelper.getRecipeArrow();
        this.arrow = guiHelper.createAnimatedRecipeArrow(200);
    }

    @Override
    @Nonnull
    public RecipeType<ExtractionRecipe> getRecipeType() {
        return MedsAndHerbsJEIPlugin.EXTRACTION_TYPE;
    }

    @Override
    @Nonnull
    public Component getTitle() {
        return Component.translatable("container.meds_and_herbs.extraction_apparatus");
    }

    @Override
    public int getWidth() {
        return 140;
    }

    @Override
    public int getHeight() {
        return 90;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ExtractionRecipe recipe, @Nonnull IFocusGroup focuses) {
        // ---- 粉末（带组件） ----
        ItemStack powderStack = JeiHelper.createItemStackWithComponents(recipe.powder().ingredient(), recipe.powder().count());
        IRecipeSlotBuilder powderSlot = builder.addSlot(RecipeIngredientRole.INPUT, 10, 30)
                .setBackground(slotBackground, -1, -1)
                .addItemStack(powderStack);

        // ---- 溶剂（带组件） ----
        ItemStack solventStack = JeiHelper.createItemStackWithComponents(recipe.solvent().ingredient(), recipe.solvent().count());
        IRecipeSlotBuilder solventSlot = builder.addSlot(RecipeIngredientRole.INPUT, 10, 52)
                .setBackground(slotBackground, -1, -1)
                .addItemStack(solventStack);

        // ---- 空瓶 ----
        IRecipeSlotBuilder emptyBottleSlot = null;
        if (recipe.emptyBottle().isPresent()) {
            ItemStack bottleStack = JeiHelper.createItemStackWithComponents(recipe.emptyBottle().get().ingredient(), recipe.emptyBottle().get().count());
            emptyBottleSlot = builder.addSlot(RecipeIngredientRole.INPUT, 69, 10)
                    .setBackground(slotBackground, -1, -1)
                    .addItemStack(bottleStack);
        }

        // ---- 主输出 ----
        IRecipeSlotBuilder outputSlot = builder.addSlot(RecipeIngredientRole.OUTPUT, 96, 30)
                .setBackground(slotBackground, -1, -1)
                .addItemStack(recipe.output());

        // ---- 副产物 ----
        IRecipeSlotBuilder stillageSlot = null;
        if (!recipe.stillage().isEmpty()) {
            stillageSlot = builder.addSlot(RecipeIngredientRole.OUTPUT, 96, 52)
                    .setBackground(slotBackground, -1, -1)
                    .addItemStack(recipe.stillage());
        }
        // ---- 棉过滤器（从标签获取所有匹配物品） ----
        var filterItems = net.minecraft.core.registries.BuiltInRegistries.ITEM
                .getTag(Tags.Items.FILTER)
                .map(holders -> holders.stream()
                        .map(holder -> {
                            ItemStack stack = new ItemStack(holder.value(), 1);
                            if (stack.isDamageableItem()) {
                                stack.set(DataComponents.DAMAGE, 1);
                            }
                            return stack;
                        })
                        .toArray(ItemStack[]::new))
                .orElseGet(() -> new ItemStack[]{ new ItemStack(Items.COTTON_FILTER.get()) });

        builder.addSlot(RecipeIngredientRole.CATALYST, 48, 20)
                .setBackground(slotBackground, -1, -1)
                .addItemStacks(java.util.Arrays.asList(filterItems));

        // ---- 关联焦点 ----
        if (stillageSlot != null) {
            if (emptyBottleSlot != null) {
                builder.createFocusLink(emptyBottleSlot, powderSlot, solventSlot, outputSlot, stillageSlot);
            } else {
                builder.createFocusLink(powderSlot, solventSlot, outputSlot, stillageSlot);
            }
        } else {
            if (emptyBottleSlot != null) {
                builder.createFocusLink(emptyBottleSlot, powderSlot, solventSlot, outputSlot);
            } else {
                builder.createFocusLink(powderSlot, solventSlot, outputSlot);
            }
        }
    }

    @Override
    public void draw(ExtractionRecipe recipe, @Nonnull IRecipeSlotsView recipeSlotsView, @Nonnull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        Minecraft minecraft = Minecraft.getInstance();

        // ---- 主加工箭头 ----
        arrowBackground.draw(guiGraphics, 48, 41);
        arrow.draw(guiGraphics, 48, 41);

        // ---- 棉过滤器耐久标注 ----
        guiGraphics.pose().pushPose();
        float scale = 0.8f;
        guiGraphics.pose().scale(scale, scale, 1.0f);
        int x = (int) (48 / scale) + 16;
        int y = (int) (20 / scale) + 16;
        guiGraphics.drawString(minecraft.font, Component.literal("-1"), x, y, 0xFFFF0000, true);
        guiGraphics.pose().popPose();

        // ---- “需要：”文字 + 小箭头 ----
        if (recipe.emptyBottle().isPresent()) {
            Component requireText = Component.translatable("jei.recipe.requires");
            guiGraphics.drawString(minecraft.font, requireText, 69, 0, 0xFFFFFFFF, false);
            guiGraphics.drawString(minecraft.font, "→", 87, 15, 0xFFFFFFFF, false);
            ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(MedsAndHerbs.MODID, "textures/jei/empty_bottle_slot.png");
            guiGraphics.blit(texture, 95, 9, 0, 0, 18, 18, 18, 18);
        }

        // ---- 时间文字 ----
        int seconds = ExtractionRecipe.MAX_PROGRESS / 20;
        Component timeText = Component.translatable("gui.jei.category.smelting.time.seconds", seconds);
        guiGraphics.drawString(minecraft.font, timeText, 10, 72, 0xFF808080, false);
    }

    @Override
    public void createRecipeExtras(@Nonnull IRecipeExtrasBuilder builder, @Nonnull ExtractionRecipe recipe, @Nonnull IFocusGroup focuses) {
        // 无需额外控件
    }

    @Override
    public void getTooltip(@Nonnull ITooltipBuilder tooltip, @Nonnull ExtractionRecipe recipe, @Nonnull IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        // 无额外提示
    }
}