package com.plank.meds_and_herbs.jei;

import com.plank.meds_and_herbs.init.Blocks;
import com.plank.meds_and_herbs.recipe.FermentationRecipe;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class FermentationRecipeCategory implements IRecipeCategory<FermentationRecipe> {

    private final IDrawable icon;
    private final IDrawable slotBackground;
    private final IDrawable arrowBackground;
    private final IDrawableAnimated arrow;

    public FermentationRecipeCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(Blocks.FERMENTATION_BARREL.get()));
        this.slotBackground = guiHelper.getSlotDrawable();
        this.arrowBackground = guiHelper.getRecipeArrow();
        this.arrow = guiHelper.createAnimatedRecipeArrow(200);
    }

    @Override
    @Nonnull
    public RecipeType<FermentationRecipe> getRecipeType() {
        return MedsAndHerbsJEIPlugin.FERMENTATION_TYPE;
    }

    @Override
    @Nonnull
    public Component getTitle() {
        return Component.translatable("block.meds_and_herbs.fermentation_barrel");
    }

    @Override
    public int getWidth() {
        return 160;
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
    public void setRecipe(IRecipeLayoutBuilder builder, FermentationRecipe recipe, @Nonnull IFocusGroup focuses) {
        // 标记为无序配方（自动在右上角绘制弯曲箭头图标）
        builder.setShapeless();

        // ---- 3x3 输入网格 ----
        int startX = 10, startY = 10;
        int slotSize = 18;
        List<Ingredient> ingredients = recipe.getIngredients();
        List<IRecipeSlotBuilder> inputSlots = new ArrayList<>();

        for (int i = 0; i < 9; i++) {
            int row = i / 3;
            int col = i % 3;
            int x = startX + col * slotSize;
            int y = startY + row * slotSize;

            if (i < ingredients.size()) {
                Ingredient ing = ingredients.get(i);
                if (!ing.isEmpty()) {
                    ItemStack[] stacks = ing.getItems();
                    if (stacks.length > 0) {
                        IRecipeSlotBuilder slot = builder.addSlot(RecipeIngredientRole.INPUT, x, y)
                                .setBackground(slotBackground, -1, -1)
                                .addItemStack(stacks[0]);
                        inputSlots.add(slot);
                    }
                }
            }
            // 剩余空槽位不添加，保持背景透明（或可添加空槽背景，但不加更干净）
        }

        // ---- 输出（右侧） ----
        if (Minecraft.getInstance().level != null) {
            IRecipeSlotBuilder outputSlot = builder.addSlot(RecipeIngredientRole.OUTPUT, 120, 33)
                    .setBackground(slotBackground, -1, -1)
                    .addItemStack(recipe.getResultItem(Minecraft.getInstance().level.registryAccess()));
            if (!inputSlots.isEmpty()) {
                List<IRecipeSlotBuilder> allSlots = new ArrayList<>(inputSlots);
                allSlots.add(outputSlot);
                builder.createFocusLink(allSlots.toArray(new IRecipeSlotBuilder[0]));
            }
        }
    }

    @Override
    public void draw(@Nonnull FermentationRecipe recipe, @Nonnull IRecipeSlotsView recipeSlotsView, @Nonnull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        Minecraft minecraft = Minecraft.getInstance();

        // ---- 动态箭头（位于输入网格右侧） ----
        int arrowX = 80, arrowY = 33;
        arrowBackground.draw(guiGraphics, arrowX, arrowY);
        arrow.draw(guiGraphics, arrowX, arrowY);

        // ---- 左下角时间 ----
        int seconds = FermentationRecipe.DEFAULT_COOKING_TIME;
        Component timeText = Component.translatable("gui.jei.category.smelting.time.seconds", seconds);
        guiGraphics.drawString(minecraft.font, timeText, 10, getHeight() - 15, 0xFF808080, false);
    }

    @Override
    public void createRecipeExtras(@Nonnull IRecipeExtrasBuilder builder, @Nonnull FermentationRecipe recipe, @Nonnull IFocusGroup focuses) {
        // 无需额外控件
    }

    @Override
    public void getTooltip(@Nonnull ITooltipBuilder tooltip, @Nonnull FermentationRecipe recipe, @Nonnull IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        // 无额外提示
    }
}