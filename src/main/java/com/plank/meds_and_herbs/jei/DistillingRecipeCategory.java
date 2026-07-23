package com.plank.meds_and_herbs.jei;

import com.plank.meds_and_herbs.MedsAndHerbs;
import com.plank.meds_and_herbs.init.Blocks;
import com.plank.meds_and_herbs.recipe.DistillingRecipe;
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
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class DistillingRecipeCategory implements IRecipeCategory<DistillingRecipe> {

    private final IDrawable icon;
    private final IDrawable slotBackground;
    private final IDrawable arrowBackground;
    private final IDrawableAnimated arrow;

    public DistillingRecipeCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(Blocks.DISTILLERY_APPARATUS.get()));
        this.slotBackground = guiHelper.getSlotDrawable();
        this.arrowBackground = guiHelper.getRecipeArrow();
        this.arrow = guiHelper.createAnimatedRecipeArrow(200);
    }

    @Override
    @Nonnull
    public RecipeType<DistillingRecipe> getRecipeType() {
        return MedsAndHerbsJEIPlugin.DISTILLING_TYPE;
    }

    @Override
    @Nonnull
    public Component getTitle() {
        return Component.translatable("container.meds_and_herbs.distillery_apparatus");
    }

    @Override
    public int getWidth() {
        return 160;
    }

    @Override
    public int getHeight() {
        return 70;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, DistillingRecipe recipe, @Nonnull IFocusGroup focuses) {
        // ✅ 标记为无序配方（JEI 自动在右上角显示弯曲箭头图标）
        builder.setShapeless();

        // ---- 输入（水平排列） ----
        IRecipeSlotBuilder inputASlot = null;
        IRecipeSlotBuilder inputBSlot = null;

        if (recipe.inputA().isPresent()) {
            ItemStack stackA = recipe.inputA().get().ingredient().getItems()[0].copy();
            stackA.setCount(recipe.inputA().get().count());
            inputASlot = builder.addSlot(RecipeIngredientRole.INPUT, 10, 25)
                    .setBackground(slotBackground, -1, -1)
                    .addItemStack(stackA);
        }
        if (recipe.inputB().isPresent()) {
            ItemStack stackB = recipe.inputB().get().ingredient().getItems()[0].copy();
            stackB.setCount(recipe.inputB().get().count());
            inputBSlot = builder.addSlot(RecipeIngredientRole.INPUT, 28, 25)
                    .setBackground(slotBackground, -1, -1)
                    .addItemStack(stackB);
        }

        // ---- 空瓶输入（右上） ----
        IRecipeSlotBuilder emptyBottleSlot = null;
        if (recipe.emptyBottle().isPresent()) {
            ItemStack bottleStack = recipe.emptyBottle().get().ingredient().getItems()[0].copy();
            bottleStack.setCount(recipe.emptyBottle().get().count());
            emptyBottleSlot = builder.addSlot(RecipeIngredientRole.INPUT, 69, 6)
                    .setBackground(slotBackground, -1, -1)
                    .addItemStack(bottleStack);
        }

        // ---- 输出（水平排列） ----
        IRecipeSlotBuilder outputSlot = builder.addSlot(RecipeIngredientRole.OUTPUT, 96, 25)
                .setBackground(slotBackground, -1, -1)
                .addItemStack(recipe.output());

        IRecipeSlotBuilder stillageSlot = null;
        if (!recipe.stillage().isEmpty()) {
            stillageSlot = builder.addSlot(RecipeIngredientRole.OUTPUT, 114, 25)
                    .setBackground(slotBackground, -1, -1)
                    .addItemStack(recipe.stillage());
        }

        // ---- 关联焦点 ----
        java.util.ArrayList<IRecipeSlotBuilder> slots = new java.util.ArrayList<>();
        if (inputASlot != null) slots.add(inputASlot);
        if (inputBSlot != null) slots.add(inputBSlot);
        if (emptyBottleSlot != null) slots.add(emptyBottleSlot);
        slots.add(outputSlot);
        if (stillageSlot != null) slots.add(stillageSlot);
        builder.createFocusLink(slots.toArray(new IRecipeSlotBuilder[0]));
    }

    @Override
    public void draw(DistillingRecipe recipe, @Nonnull IRecipeSlotsView recipeSlotsView, @Nonnull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        Minecraft minecraft = Minecraft.getInstance();

        // ---- 主加工箭头 ----
        arrowBackground.draw(guiGraphics, 48, 25);
        arrow.draw(guiGraphics, 48, 25);

        // ---- 特殊槽位背景图标 ----
        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(MedsAndHerbs.MODID, "textures/jei/empty_bottle_slot.png");
        guiGraphics.blit(texture, 95, 5, 0, 0, 18, 18, 18, 18);

        // ---- “需要：”文字 + 小箭头 ----
        if (recipe.emptyBottle().isPresent()) {
            Component requireText = Component.translatable("jei.recipe.requires");
            guiGraphics.drawString(minecraft.font, requireText, 40, 9, 0xFFFFFFFF, false);
            guiGraphics.drawString(minecraft.font, "→", 87, 12, 0xFFFFFFFF, false);
        }

        // ---- 时间文字 ----
        int seconds = DistillingRecipe.MAX_PROGRESS / 20;
        Component timeText = Component.translatable("gui.jei.category.smelting.time.seconds", seconds);
        guiGraphics.drawString(minecraft.font, timeText, 10, 50, 0xFF808080, false);
    }

    @Override
    public void createRecipeExtras(@Nonnull IRecipeExtrasBuilder builder, @Nonnull DistillingRecipe recipe, @Nonnull IFocusGroup focuses) {
        // 无需额外控件
    }

    @Override
    public void getTooltip(@Nonnull ITooltipBuilder tooltip, @Nonnull DistillingRecipe recipe, @Nonnull IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        // 无额外提示
    }
}