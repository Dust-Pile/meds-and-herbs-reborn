package com.dusty_dusty.meds_and_herbs.jei;

import com.dusty_dusty.meds_and_herbs.MedsAndHerbs;
import com.dusty_dusty.meds_and_herbs.init.MHItems;
import com.dusty_dusty.meds_and_herbs.item.Bouquet;
import com.dusty_dusty.meds_and_herbs.recipe.GrinderRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.Nonnull;
import java.util.List;

public class GrinderRecipeCategory implements IRecipeCategory<GrinderRecipe> {

    private final IDrawable icon;
    private final IDrawable slotBackground;
    private final IDrawable arrowBackground;
    private final IDrawableAnimated arrow;

    public GrinderRecipeCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(MHItems.GRINDER.get()));
        this.slotBackground = guiHelper.getSlotDrawable();
        this.arrowBackground = guiHelper.getRecipeArrow();
        this.arrow = guiHelper.createAnimatedRecipeArrow(200);
    }

    @Override
    @Nonnull
    public RecipeType<GrinderRecipe> getRecipeType() {
        return MedsAndHerbsJEIPlugin.GRINDER_TYPE;
    }

    @Override
    @Nonnull
    public Component getTitle() {
        return Component.translatable("item.meds_and_herbs.grinder");
    }

    @Override
    public int getWidth() {
        return 140;
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
    public void setRecipe(@Nonnull IRecipeLayoutBuilder builder, GrinderRecipe recipe, @Nonnull IFocusGroup focuses) {
        var ingredients = recipe.getIngredients();

        if (recipe.getId().equals(MedsAndHerbs.id("bouquet_grinding"))) {
            ItemStack bouquet = new ItemStack(MHItems.POWDER_HERBAL.get());
            List<ResourceLocation> flowers = Bouquet.createFlowersList(Items.ORANGE_TULIP, Items.DANDELION, Items.CORNFLOWER, Items.LILY_OF_THE_VALLEY);
            Bouquet.setFlowerData(bouquet, flowers);
        }


        if (!ingredients.isEmpty()) {
            var inputIng = ingredients.get(0);

            if (recipe.getId().equals(MedsAndHerbs.id("bouquet_grinding"))) {
                ItemStack bouquet = new ItemStack(MHItems.POWDER_HERBAL.get());
                List<ResourceLocation> flowers = Bouquet.createFlowersList(Items.ORANGE_TULIP, Items.DANDELION, Items.CORNFLOWER, Items.LILY_OF_THE_VALLEY);
                Bouquet.setFlowerData(bouquet, flowers);
                inputIng = Ingredient.of(bouquet);
            }

            if (!inputIng.isEmpty()) {
                builder.addSlot(RecipeIngredientRole.INPUT, 10, 25)
                        .setBackground(slotBackground, -1, -1)
                        .addItemStacks(List.of(inputIng.getItems()));
            }
        }

        ItemStack output;
        if (Minecraft.getInstance().level != null) {
            output = recipe.getResultItem(Minecraft.getInstance().level.registryAccess());
            if (!output.isEmpty()) {
                builder.addSlot(RecipeIngredientRole.OUTPUT, 96, 25)
                        .setBackground(slotBackground, -1, -1)
                        .addItemStack(output);
            }
        }
    }

    @Override
    public void draw(@Nonnull GrinderRecipe recipe, @Nonnull IRecipeSlotsView recipeSlotsView, @Nonnull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        arrowBackground.draw(guiGraphics, 48, 25);
        arrow.draw(guiGraphics, 48, 25);

    }

    @Override
    public void createRecipeExtras(@Nonnull IRecipeExtrasBuilder builder, @Nonnull GrinderRecipe recipe, @Nonnull IFocusGroup focuses) {
    }

    @Override
    public void getTooltip(@Nonnull ITooltipBuilder tooltip, @Nonnull GrinderRecipe recipe, @Nonnull IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
    }
}