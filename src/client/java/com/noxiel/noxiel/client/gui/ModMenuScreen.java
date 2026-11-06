package com.noxiel.noxiel.client.gui;

import com.noxiel.noxiel.client.ClientConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ModMenuScreen extends Screen {

    private int currentTab = 0;
    private int panelWidth = 400;
    private int panelHeight = 250;
    private int panelX;
    private int panelY;

    public ModMenuScreen() {
        super(Text.literal("Noxiel Client"));
    }

    @Override
    protected void init() {
        super.init();
        this.panelX = (this.width - this.panelWidth) / 2;
        this.panelY = (this.height - this.panelHeight) / 2;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);

        context.fill(panelX + 5, panelY + 5, panelX + panelWidth + 5, panelY + panelHeight + 5, 0x80000000);
        context.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, 0xFF1E121E);
        context.drawBorder(panelX, panelY, panelWidth, panelHeight, 0xFFFF1493);

        drawTab(context, "MODS", panelX + 10, panelY - 25, 80, 25, 0, mouseX, mouseY);
        drawTab(context, "SETTINGS", panelX + 95, panelY - 25, 100, 25, 1, mouseX, mouseY);

        if (currentTab == 0) {
            drawCard(context, "FPS", panelX + 15, panelY + 35, 110, 80, ClientConfig.isFpsEnabled);
            drawCard(context, "CPS", panelX + 145, panelY + 35, 110, 80, ClientConfig.isCpsEnabled);
        }

        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, panelY + 10, 0xFFF5C2E7);

        // 右上のボタン
        drawTab(context, "HUD", panelX + panelWidth - 100, panelY + 5, 40, 20, -1, mouseX, mouseY);
        drawTab(context, "CONFIG", panelX + panelWidth - 55, panelY + 5, 45, 20, -1, mouseX, mouseY);
    }

    private void drawTab(DrawContext context, String text, int x, int y, int width, int height, int tabIndex, int mouseX, int mouseY) {
        boolean isSelected = (currentTab == tabIndex && tabIndex != -1);
        boolean isHovered = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;

        int bgColor = isSelected ? 0xFF3A1F3A : (isHovered ? 0xFF2B1B2B : 0xFF1E121E);
        int textColor = isSelected ? 0xFFFF1493 : 0xFFAAAAAA;

        context.fill(x, y, x + width, y + height, bgColor);
        if (isSelected) {
            context.fill(x, y + height - 2, x + width, y + height, 0xFFFF1493);
        }

        int textX = x + (width - this.textRenderer.getWidth(text)) / 2;
        int textY = y + (height - 8) / 2;
        context.drawTextWithShadow(this.textRenderer, text, textX, textY, textColor);
    }

    private void drawCard(DrawContext context, String title, int x, int y, int width, int height, boolean isEnabled) {
        context.fill(x, y, x + width, y + height, 0xFF2B1B2B);
        context.drawBorder(x, y, width, height, 0xFF5A3F5A);

        int titleX = x + (width - this.textRenderer.getWidth(title)) / 2;
        context.drawTextWithShadow(this.textRenderer, title, titleX, y + 10, 0xFFF5C2E7);

        int statusY = y + height - 20;
        int statusColor = isEnabled ? 0xFF00AA00 : 0xFFAA0000;
        context.fill(x + 5, statusY, x + width - 5, statusY + 15, statusColor);

        String statusText = isEnabled ? "ENABLED" : "DISABLED";
        int statusTextX = x + (width - this.textRenderer.getWidth(statusText)) / 2;
        context.drawTextWithShadow(this.textRenderer, statusText, statusTextX, statusY + 4, 0xFFFFFF);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isMouseOverTab(mouseX, mouseY, panelX + 10, panelY - 25, 80, 25)) {
            currentTab = 0;
            return true;
        }
        if (isMouseOverTab(mouseX, mouseY, panelX + 95, panelY - 25, 100, 25)) {
            currentTab = 1;
            return true;
        }
        if (isMouseOverTab(mouseX, mouseY, panelX + panelWidth - 100, panelY + 5, 40, 20)) {
            if (this.client != null) {
                this.client.setScreen(new HudEditorScreen());
            }
            return true;
        }
        if (isMouseOverTab(mouseX, mouseY, panelX + panelWidth - 55, panelY + 5, 45, 20)) {
            if (this.client != null) {
                this.client.setScreen(new ConfigScreen()); // Config画面を開く
            }
            return true;
        }

        if (currentTab == 0) {
            if (isMouseOverTab(mouseX, mouseY, panelX + 15, panelY + 35, 110, 80)) {
                ClientConfig.isFpsEnabled = !ClientConfig.isFpsEnabled;
                ClientConfig.save(); // ★切り替えた瞬間に保存
                return true;
            }
            if (isMouseOverTab(mouseX, mouseY, panelX + 145, panelY + 35, 110, 80)) {
                ClientConfig.isCpsEnabled = !ClientConfig.isCpsEnabled;
                ClientConfig.save(); // ★切り替えた瞬間に保存
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean isMouseOverTab(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}