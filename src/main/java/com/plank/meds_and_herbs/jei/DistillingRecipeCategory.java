package com.plank.meds_and_herbs.jei;

import com.plank.meds_and_herbs.MedsAndHerbs;
import com.plank.meds_and_herbs.init.MHBlocks;
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
import java.util.ArrayList;
import java.util.List;

public class DistillingRecipeCategory implements IRecipeCategory<DistillingRecipe> {

    private final IDrawable icon;
    private final IDrawable slotBackground;
    private final IDrawable arrowBackground;
    private final IDrawableAnimated arrow;

    public DistillingRecipeCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(MHBlocks.DISTILLERY_APPARATUS.get()));
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
        builder.setShapeless();

        List<IRecipeSlotBuilder> linkedSlots = new ArrayList<>();

        recipe.getInputA().ifPresent(ingredient -> {
            IRecipeSlotBuilder slot = builder.addSlot(RecipeIngredientRole.INPUT, 10, 25)
                    .setBackground(slotBackground, -1, -1)
                    .addIngredients(ingredient);
            linkedSlots.add(slot);
        });

        recipe.getInputB().ifPresent(ingredient -> {
            IRecipeSlotBuilder slot = builder.addSlot(RecipeIngredientRole.INPUT, 28, 25)
                    .setBackground(slotBackground, -1, -1)
                    .addIngredients(ingredient);
            linkedSlots.add(slot);
        });

        recipe.getEmptyBottle().ifPresent(ingredient -> {
            IRecipeSlotBuilder slot = builder.addSlot(RecipeIngredientRole.INPUT, 69, 6)
                    .setBackground(slotBackground, -1, -1)
                    .addIngredients(ingredient);
            linkedSlots.add(slot);
        });

        IRecipeSlotBuilder outputSlot = builder.addSlot(RecipeIngredientRole.OUTPUT, 96, 25)
                .setBackground(slotBackground, -1, -1)
                .addItemStack(recipe.getOutput());
        linkedSlots.add(outputSlot);

        if (!recipe.getSpillage().isEmpty()) {
            IRecipeSlotBuilder spillageSlot = builder.addSlot(RecipeIngredientRole.OUTPUT, 114, 25)
                    .setBackground(slotBackground, -1, -1)
                    .addItemStack(recipe.getSpillage());
            linkedSlots.add(spillageSlot);
        }

        builder.createFocusLink(linkedSlots.toArray(new IRecipeSlotBuilder[0]));
    }

    @Override
    public void draw(DistillingRecipe recipe, @Nonnull IRecipeSlotsView recipeSlotsView, @Nonnull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        Minecraft minecraft = Minecraft.getInstance();

        arrowBackground.draw(guiGraphics, 48, 25);
        arrow.draw(guiGraphics, 48, 25);

        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(MedsAndHerbs.MODID, "textures/jei/empty_bottle_slot.png");
        guiGraphics.blit(texture, 95, 5, 0, 0, 18, 18, 18, 18);

        if (recipe.getEmptyBottle().isPresent()) {
            Component requireText = Component.translatable("jei.recipe.requires");
            guiGraphics.drawString(minecraft.font, requireText, 40, 9, 0xFFFFFFFF, false);
            guiGraphics.drawString(minecraft.font, "→", 87, 12, 0xFFFFFFFF, false);
        }

        int seconds = DistillingRecipe.MAX_PROGRESS / 20;
        Component timeText = Component.translatable("gui.jei.category.smelting.time.seconds", seconds);
        guiGraphics.drawString(minecraft.font, timeText, 10, 50, 0xFF808080, false);
    }

    @Override
    public void createRecipeExtras(@Nonnull IRecipeExtrasBuilder builder, @Nonnull DistillingRecipe recipe, @Nonnull IFocusGroup focuses) {
    }

    @Override
    public void getTooltip(@Nonnull ITooltipBuilder tooltip, @Nonnull DistillingRecipe recipe, @Nonnull IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
    }
}