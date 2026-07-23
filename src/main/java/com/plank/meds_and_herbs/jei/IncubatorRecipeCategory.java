package com.plank.meds_and_herbs.jei;

import com.plank.meds_and_herbs.init.Blocks;
import com.plank.meds_and_herbs.recipe.IncubatorRecipe;
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

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.List;

public class IncubatorRecipeCategory implements IRecipeCategory<IncubatorRecipe> {

    private final IDrawable icon;
    private final IDrawable slotBackground;
    private final IDrawable arrowBackground;
    private final IDrawableAnimated arrow;
    private static final DecimalFormat PERCENT_FORMAT = new DecimalFormat("0.#");

    public IncubatorRecipeCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(Blocks.INCUBATOR.get()));
        this.slotBackground = guiHelper.getSlotDrawable();
        this.arrowBackground = guiHelper.getRecipeArrow();
        this.arrow = guiHelper.createAnimatedRecipeArrow(200); // 10秒
    }

    @Override
    @Nonnull
    public RecipeType<IncubatorRecipe> getRecipeType() {
        return MedsAndHerbsJEIPlugin.INCUBATOR_TYPE;
    }

    @Override
    @Nonnull
    public Component getTitle() {
        return Component.translatable("container.meds_and_herbs.incubator");
    }

    @Override
    public int getWidth() {
        return 200;
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
    public void setRecipe(IRecipeLayoutBuilder builder, IncubatorRecipe recipe, @Nonnull IFocusGroup focuses) {
        // ---- 输入槽 ----
        IRecipeSlotBuilder inputSlot = builder.addSlot(RecipeIngredientRole.INPUT, 10, 25)
                .setBackground(slotBackground, -1, -1)
                .addItemStack(recipe.input());

        // ---- 输出槽（只添加前三个，用于焦点链接） ----
        List<IncubatorRecipe.WeightedOutput> sortedOutputs = recipe.outputs().stream()
                .sorted(Comparator.comparingInt(IncubatorRecipe.WeightedOutput::weight).reversed())
                .toList();

        int maxDisplay = Math.min(3, sortedOutputs.size());

        IRecipeSlotBuilder[] outputSlots = new IRecipeSlotBuilder[maxDisplay];
        int startX = 90;
        int y = 25;
        for (int i = 0; i < maxDisplay; i++) {
            IncubatorRecipe.WeightedOutput wo = sortedOutputs.get(i);
            int x = startX + i * 20;
            outputSlots[i] = builder.addSlot(RecipeIngredientRole.OUTPUT, x, y)
                    .setBackground(slotBackground, -1, -1)
                    .addItemStack(wo.itemStack());
        }

        // ---- 关联焦点 ----
        if (maxDisplay > 0) {
            IRecipeSlotBuilder[] allSlots = new IRecipeSlotBuilder[maxDisplay + 1];
            allSlots[0] = inputSlot;
            System.arraycopy(outputSlots, 0, allSlots, 1, maxDisplay);
            builder.createFocusLink(allSlots);
        } else {
            builder.createFocusLink(inputSlot);
        }
    }

    @Override
    public void draw(IncubatorRecipe recipe, @Nonnull IRecipeSlotsView recipeSlotsView, @Nonnull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        Minecraft minecraft = Minecraft.getInstance();

        // ---- 箭头 ----
        int arrowX = 48, arrowY = 25;
        arrowBackground.draw(guiGraphics, arrowX, arrowY);
        arrow.draw(guiGraphics, arrowX, arrowY);

        // ---- 输出物品概率和“其他”文本 ----
        List<IncubatorRecipe.WeightedOutput> sortedOutputs = recipe.outputs().stream()
                .sorted(Comparator.comparingInt(IncubatorRecipe.WeightedOutput::weight).reversed())
                .toList();

        int totalWeight = sortedOutputs.stream().mapToInt(IncubatorRecipe.WeightedOutput::weight).sum();
        int maxDisplay = Math.min(3, sortedOutputs.size());

        int startX = 90;
        int y = 25;
        // 绘制前三个的概率
        for (int i = 0; i < maxDisplay; i++) {
            IncubatorRecipe.WeightedOutput wo = sortedOutputs.get(i);
            double percent = (double) wo.weight() / totalWeight * 100;
            String percentStr = PERCENT_FORMAT.format(percent) + "%";
            int x = startX + i * 20;
            // 绘制在物品下方（y+18）
            guiGraphics.drawString(minecraft.font, percentStr, x + 1, y + 18, 0xFFFFFFFF, false);
        }

        // ---- 如果输出数量 > 3，绘制“……其他 XX%” ----
        if (sortedOutputs.size() > 3) {
            int remainingWeight = sortedOutputs.stream().skip(3).mapToInt(IncubatorRecipe.WeightedOutput::weight).sum();
            double remainingPercent = (double) remainingWeight / totalWeight * 100;

            Component otherText = Component.translatable("jei.recipe.other", PERCENT_FORMAT.format(remainingPercent));
            int x = startX + 3 * 20 + 2; // 第四个槽位位置
            // 绘制在输出槽的水平线上，位置在物品下方（与概率对齐）
            // 但这里没有槽位，我们需要一个合适的位置，可能稍微靠上
            guiGraphics.drawString(minecraft.font, otherText, x, y + 4, 0xFFFFFFFF, false);
        }

        // ---- 左下角显示加工时间（秒） ----
        int seconds = recipe.processingTime() / 20;
        Component timeText = Component.translatable("gui.jei.category.smelting.time.seconds", seconds);
        guiGraphics.drawString(minecraft.font, timeText, 10, getHeight() - 15, 0xFF808080, false);
    }

    @Override
    public void createRecipeExtras(@Nonnull IRecipeExtrasBuilder builder, @Nonnull IncubatorRecipe recipe, @Nonnull IFocusGroup focuses) {
        // 无需额外控件
    }

    @Override
    public void getTooltip(@Nonnull ITooltipBuilder tooltip, @Nonnull IncubatorRecipe recipe, @Nonnull IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        // 无额外提示
    }
}