package com.plank.meds_and_herbs.client.gui.screen;

import com.plank.meds_and_herbs.client.gui.menu.ExtractApparatusGUIMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import javax.annotation.Nonnull;

public class ExtractApparatusGUIScreen extends AbstractContainerScreen<ExtractApparatusGUIMenu> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("meds_and_herbs", "textures/screens/extraction_apparatus_gui.png");
    private static final ResourceLocation[] PROGRESS_TEXTURES = new ResourceLocation[8];
    private static final ResourceLocation[] CANDLE_FLAME = new ResourceLocation[2];

    static {
        for (int i = 0; i < 8; i++) {
            PROGRESS_TEXTURES[i] = ResourceLocation.fromNamespaceAndPath("meds_and_herbs",
                    "textures/screens/extraction_progress_" + i + ".png");
        }
        CANDLE_FLAME[0] = ResourceLocation.fromNamespaceAndPath("meds_and_herbs", "textures/screens/candle_flame_0.png");
        CANDLE_FLAME[1] = ResourceLocation.fromNamespaceAndPath("meds_and_herbs", "textures/screens/candle_flame_1.png");
    }

    public ExtractApparatusGUIScreen(ExtractApparatusGUIMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);

        // ===== 只有正在烹饪时才绘制进度条和火焰 =====
        if (menu.isCooking()) {
            int progress = menu.getProgress();
            int maxProgress = menu.getMaxProgress();
            if (maxProgress > 0) {
                int stage = (int) ((float) progress / maxProgress * 7);
                stage = Math.clamp(stage, 0, 7);
                guiGraphics.blit(PROGRESS_TEXTURES[stage], leftPos + 48, topPos + 14, 0, 0, 84, 46, 84, 46);
            }

            // 火焰动画（进度未完成时闪烁）
            if (progress >= 0 && progress < maxProgress) {
                int stage = (int) ((float) progress / maxProgress * 7 % 2);
                guiGraphics.blit(CANDLE_FLAME[stage], leftPos + 120, topPos + 61, 0, 0, 8, 8, 8, 8);
            }
        }
    }

    @Override
    public void render(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
    }
}