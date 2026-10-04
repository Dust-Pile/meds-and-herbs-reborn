package com.plank.meds_and_herbs.jei;

import com.plank.meds_and_herbs.init.MHBlocks;
import com.plank.meds_and_herbs.init.MHItems;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class DistillingRecipeCategory implements IRecipeCategory<DistillingRecipe> {

    private final IDrawable icon;
    private final IDrawable slotBackground;
    private final IDrawableAnimated arrow;

    public DistillingRecipeCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(MHBlocks.DISTILLERY_APPARATUS.get()));
        this.slotBackground = guiHelper.getSlotDrawable();
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
        builder.setShapeless();

        List<IRecipeSlotBuilder> linkedSlots = new ArrayList<>();

        IRecipeSlotBuilder inputASlot = builder.addSlot(RecipeIngredientRole.INPUT, 10, 25)
                .setBackground(slotBackground, -1, -1);
        recipe.getInputA().ifPresent(ingredient -> {
            inputASlot.addIngredients(ingredient);
            linkedSlots.add(inputASlot);
        });

        IRecipeSlotBuilder inputBSlot = builder.addSlot(RecipeIngredientRole.INPUT, 28, 25)
                .setBackground(slotBackground, -1, -1);
        recipe.getInputB().ifPresent(ingredient -> {
            inputBSlot.addIngredients(ingredient);
            linkedSlots.add(inputBSlot);
        });

        IRecipeSlotBuilder bottleSlot = builder.addSlot(RecipeIngredientRole.INPUT, 10, 6)
                .setBackground(slotBackground, -1, -1)
                .addIngredients(Ingredient.of(MHItems.MEDICINE_BOTTLE.get()));
        linkedSlots.add(bottleSlot);

        IRecipeSlotBuilder outputSlot = builder.addSlot(RecipeIngredientRole.OUTPUT, 96, 25)
                .setBackground(slotBackground, -1, -1)
                .addItemStack(recipe.getOutput());
        linkedSlots.add(outputSlot);

        IRecipeSlotBuilder spillageSlot = builder.addSlot(RecipeIngredientRole.OUTPUT, 114, 25)
                .setBackground(slotBackground, -1, -1);
        if (!recipe.getSpillage().isEmpty()) {
            spillageSlot.addIngredients(Ingredient.of(recipe.getSpillage()));
            linkedSlots.add(spillageSlot);
        }

        builder.createFocusLink(linkedSlots.toArray(new IRecipeSlotBuilder[0]));
    }

    @Override
    public void draw(DistillingRecipe recipe, @Nonnull IRecipeSlotsView recipeSlotsView, @Nonnull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        Minecraft minecraft = Minecraft.getInstance();

        arrow.draw(guiGraphics, 60, 25);

        int seconds = DistillingRecipe.MAX_PROGRESS / 20;
        Component timeText = Component.translatable("gui.jei.category.smelting.time.seconds", seconds);
        guiGraphics.drawString(minecraft.font, timeText, 60, 40, 0xFF808080, false);
    }

    @Override
    public void createRecipeExtras(@Nonnull IRecipeExtrasBuilder builder, @Nonnull DistillingRecipe recipe, @Nonnull IFocusGroup focuses) {
    }

    @Override
    public void getTooltip(@Nonnull ITooltipBuilder tooltip, @Nonnull DistillingRecipe recipe, @Nonnull IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
    }
}