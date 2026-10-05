package com.juiceybeans.meds_and_herbs.client.gui.screen;

import com.juiceybeans.meds_and_herbs.MedsAndHerbs;
import com.juiceybeans.meds_and_herbs.client.gui.menu.DistilleryApparatusGUIMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

import javax.annotation.Nonnull;

public class DistilleryApparatusGUIScreen extends AbstractContainerScreen<DistilleryApparatusGUIMenu> {
    private static final ResourceLocation TEXTURE = MedsAndHerbs.id("textures/screens/distillery_apparatus_gui.png");
    private static final ResourceLocation[] PROGRESS_TEXTURES = new ResourceLocation[8];

    static {
        for (int i = 0; i < 8; i++) {
            PROGRESS_TEXTURES[i] = MedsAndHerbs.id("textures/screens/distillery_progress_" + i + ".png");
        }
    }

    public DistilleryApparatusGUIScreen(DistilleryApparatusGUIMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void render(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);

        if (!menu.isRunning()) {
            return;
        }

        int progress = menu.getProgress();
        int maxProgress = menu.getMaxProgress();
        if (maxProgress <= 0) return;

        int index = (int) ((float) progress / maxProgress * 7);
        index = Mth.clamp(index, 0, 7);

        ResourceLocation progressTexture = PROGRESS_TEXTURES[index];
        guiGraphics.blit(progressTexture, this.leftPos + 44, this.topPos + 17, 0, 0, 88, 16, 88, 16);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
    }
}