package com.juiceybeans.meds_and_herbs.jei;

import com.juiceybeans.meds_and_herbs.MedsAndHerbs;
import com.juiceybeans.meds_and_herbs.init.MHBlocks;
import com.juiceybeans.meds_and_herbs.init.MHItems;
import com.juiceybeans.meds_and_herbs.recipe.ExtractionRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
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
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ExtractionRecipeCategory implements IRecipeCategory<ExtractionRecipe> {

    private final IDrawable icon;
    private final IDrawable slotBackground;
    private final ResourceLocation PROGRESS_ARROW = MedsAndHerbs.id("textures/jei/extraction_progress_arrow.png");
    private final ResourceLocation SPILLAGE_ARROW = MedsAndHerbs.id("textures/jei/extraction_spillage_arrow.png");

    public ExtractionRecipeCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(MHBlocks.EXTRACTION_APPARATUS.get()));
        this.slotBackground = guiHelper.getSlotDrawable();
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

        ItemStack powderStack = recipe.getPowder().getItems()[0].copy();
        powderStack.setCount(recipe.getPowderCount());

        IRecipeSlotBuilder powderSlot = builder.addSlot(RecipeIngredientRole.INPUT, 80, 18)
                .setBackground(slotBackground, -1, -1)
                .addIngredients(Ingredient.of(powderStack));
        linkedSlots.add(powderSlot);

        recipe.getSolvent().ifPresent(solvent -> {
            IRecipeSlotBuilder slot = builder.addSlot(RecipeIngredientRole.INPUT, 80, 36)
                    .setBackground(slotBackground, -1, -1)
                    .addIngredients(solvent);
            linkedSlots.add(slot);
        });

        IRecipeSlotBuilder filterSlot = builder.addSlot(RecipeIngredientRole.INPUT, 28, 36)
                .setBackground(slotBackground, -1, -1)
                .addIngredients(Ingredient.of(MHItems.COTTON_FILTER.get()));
        linkedSlots.add(filterSlot);

        IRecipeSlotBuilder bottleSlot = builder.addSlot(RecipeIngredientRole.INPUT, 4, 36)
                .setBackground(slotBackground, -1, -1)
                .addIngredients(recipe.getEmptyBottle());
        linkedSlots.add(bottleSlot);

        IRecipeSlotBuilder outputSlot = builder.addSlot(RecipeIngredientRole.OUTPUT, 28, 54)
                .setBackground(slotBackground, -1, -1)
                .addItemStack(recipe.getOutput());
        linkedSlots.add(outputSlot);

        if (!recipe.getSpillage().isEmpty()) {
            IRecipeSlotBuilder spillageSlot = builder.addSlot(RecipeIngredientRole.OUTPUT, 112, 54)
                    .setBackground(slotBackground, -1, -1)
                    .addItemStack(recipe.getSpillage());
            linkedSlots.add(spillageSlot);
        }

        builder.createFocusLink(linkedSlots.toArray(new IRecipeSlotBuilder[0]));
    }

    @Override
    public void draw(ExtractionRecipe recipe, @Nonnull IRecipeSlotsView recipeSlotsView, @Nonnull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        Minecraft minecraft = Minecraft.getInstance();


        guiGraphics.pose().pushPose();
        float scale = 0.8f;
        guiGraphics.pose().scale(scale, scale, 1.0f);
        int x = (int) (30 / scale) + 16;
        int y = (int) (16 / scale) + 16;
        guiGraphics.drawString(minecraft.font, Component.literal("-1"), x, y, 0xFFFF0000, true);
        guiGraphics.pose().popPose();

        guiGraphics.blit(SPILLAGE_ARROW, 100, 32, 0, 0, 27, 16, 27, 16);
        guiGraphics.blit(PROGRESS_ARROW, 30, 4, 0, 0, 64, 31, 64, 31);

        int seconds = ExtractionRecipe.MAX_PROGRESS / 20;
        Component timeText = Component.translatable("gui.jei.category.smelting.time.seconds", seconds);
        guiGraphics.drawString(minecraft.font, timeText, 55, 12, 0xFF808080, false);
    }

    @Override
    public void createRecipeExtras(@Nonnull IRecipeExtrasBuilder builder, @Nonnull ExtractionRecipe recipe, @Nonnull IFocusGroup focuses) {
    }

    @Override
    public void getTooltip(@Nonnull ITooltipBuilder tooltip, @Nonnull ExtractionRecipe recipe, @Nonnull IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
    }
}