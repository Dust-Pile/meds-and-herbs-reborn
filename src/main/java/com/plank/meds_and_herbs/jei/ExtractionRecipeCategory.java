package com.plank.meds_and_herbs.jei;

import com.plank.meds_and_herbs.MedsAndHerbs;
import com.plank.meds_and_herbs.init.MHBlocks;
import com.plank.meds_and_herbs.init.MHItems;
import com.plank.meds_and_herbs.init.MHTags;
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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ExtractionRecipeCategory implements IRecipeCategory<ExtractionRecipe> {

    private final IDrawable icon;
    private final IDrawable slotBackground;
    private final IDrawable arrowBackground;
    private final IDrawableAnimated arrow;

    public ExtractionRecipeCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(MHBlocks.EXTRACTION_APPARATUS.get()));
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
        builder.setShapeless();

        List<IRecipeSlotBuilder> linkedSlots = new ArrayList<>();

        var powderStack = new ItemStack(recipe.getPowder().getItems()[0].getItem());
        powderStack.setCount(recipe.getPowderCount());

        IRecipeSlotBuilder powderSlot = builder.addSlot(RecipeIngredientRole.INPUT, 10, 15)
                .setBackground(slotBackground, -1, -1)
                .addIngredients(Ingredient.of(powderStack));
        linkedSlots.add(powderSlot);

        IRecipeSlotBuilder solventSlot = builder.addSlot(RecipeIngredientRole.INPUT, 10, 40)
                .setBackground(slotBackground, -1, -1)
                .addIngredients(recipe.getSolvent());
        linkedSlots.add(solventSlot);

        IRecipeSlotBuilder filterSlot = builder.addSlot(RecipeIngredientRole.INPUT, 28, 40)
                .setBackground(slotBackground, -1, -1)
                .addIngredients(Ingredient.of(MHItems.COTTON_FILTER.get()));
        linkedSlots.add(filterSlot);

        recipe.getEmptyBottle().ifPresent(ingredient -> {
            IRecipeSlotBuilder slot = builder.addSlot(RecipeIngredientRole.INPUT, 55, 15)
                    .setBackground(slotBackground, -1, -1)
                    .addIngredients(ingredient);
            linkedSlots.add(slot);
        });

        IRecipeSlotBuilder outputSlot = builder.addSlot(RecipeIngredientRole.OUTPUT, 100, 25)
                .setBackground(slotBackground, -1, -1)
                .addItemStack(recipe.getOutput());
        linkedSlots.add(outputSlot);

        if (!recipe.getSpillage().isEmpty()) {
            IRecipeSlotBuilder spillageSlot = builder.addSlot(RecipeIngredientRole.OUTPUT, 118, 25)
                    .setBackground(slotBackground, -1, -1)
                    .addItemStack(recipe.getSpillage());
            linkedSlots.add(spillageSlot);
        }

        builder.createFocusLink(linkedSlots.toArray(new IRecipeSlotBuilder[0]));
    }

    @Override
    public void draw(ExtractionRecipe recipe, @Nonnull IRecipeSlotsView recipeSlotsView, @Nonnull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        Minecraft minecraft = Minecraft.getInstance();

        arrowBackground.draw(guiGraphics, 48, 41);
        arrow.draw(guiGraphics, 48, 41);

        guiGraphics.pose().pushPose();
        float scale = 0.8f;
        guiGraphics.pose().scale(scale, scale, 1.0f);
        int x = (int) (48 / scale) + 16;
        int y = (int) (20 / scale) + 16;
        guiGraphics.drawString(minecraft.font, Component.literal("-1"), x, y, 0xFFFF0000, true);
        guiGraphics.pose().popPose();

        if (recipe.getEmptyBottle().isPresent()) {
            Component requireText = Component.translatable("jei.recipe.requires");
            guiGraphics.drawString(minecraft.font, requireText, 69, 0, 0xFFFFFFFF, false);
            guiGraphics.drawString(minecraft.font, "→", 87, 15, 0xFFFFFFFF, false);
            ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(MedsAndHerbs.MODID, "textures/jei/empty_bottle_slot.png");
            guiGraphics.blit(texture, 95, 9, 0, 0, 18, 18, 18, 18);
        }

        int seconds = ExtractionRecipe.MAX_PROGRESS / 20;
        Component timeText = Component.translatable("gui.jei.category.smelting.time.seconds", seconds);
        guiGraphics.drawString(minecraft.font, timeText, 10, 72, 0xFF808080, false);
    }

    @Override
    public void createRecipeExtras(@Nonnull IRecipeExtrasBuilder builder, @Nonnull ExtractionRecipe recipe, @Nonnull IFocusGroup focuses) {
    }

    @Override
    public void getTooltip(@Nonnull ITooltipBuilder tooltip, @Nonnull ExtractionRecipe recipe, @Nonnull IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
    }
}