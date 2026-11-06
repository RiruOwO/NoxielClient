package com.noxiel.noxiel.client.gui;

import com.noxiel.noxiel.client.ClientConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class ModMenuScreen extends Screen {
    private int currentTab = 0;
    private int panelWidth = 450;
    private int panelHeight = 300;
    private int panelX, panelY;
    private TextFieldWidget searchField;

    public ModMenuScreen() {
        super(Text.literal("Noxiel Client"));
    }

    @Override
    protected void init() {
        super.init();
        this.panelX = (this.width - this.panelWidth) / 2;
        this.panelY = (this.height - this.panelHeight) / 2;
        this.searchField = new TextFieldWidget(this.textRenderer, panelX + panelWidth - 130, panelY + 25, 110, 16, Text.literal("Search"));
        this.searchField.setMaxLength(32);
        this.searchField.setPlaceholder(Text.literal("Search..."));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);

        // パネル背景と枠線
        context.fill(panelX + 5, panelY + 5, panelX + panelWidth + 5, panelY + panelHeight + 5, 0x80000000);
        context.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, 0xFF1E121E);
        context.drawBorder(panelX, panelY, panelWidth, panelHeight, 0xFFFF1493);

        // タブの描画
        drawTab(context, "MODS", panelX + 10, panelY - 25, 80, 25, 0, mouseX, mouseY);
        drawTab(context, "SETTINGS", panelX + 95, panelY - 25, 100, 25, 1, mouseX, mouseY);

        // MODSタブの中身
        if (currentTab == 0) {
            String searchText = this.searchField != null ? this.searchField.getText().toLowerCase().trim() : "";

            int col1 = panelX + 15;
            int col2 = panelX + 155;
            int col3 = panelX + 295;
            int row1 = panelY + 50;
            int row2 = panelY + 160;

            if (searchText.isEmpty() || "fps".contains(searchText)) drawCard(context, "FPS", col1, row1, 120, 90, ClientConfig.isFpsEnabled);
            if (searchText.isEmpty() || "cps".contains(searchText)) drawCard(context, "CPS", col2, row1, 120, 90, ClientConfig.isCpsEnabled);
            if (searchText.isEmpty() || "ping".contains(searchText)) drawCard(context, "PING", col3, row1, 120, 90, ClientConfig.isPingEnabled);
            if (searchText.isEmpty() || "keys".contains(searchText) || "keystrokes".contains(searchText)) drawCard(context, "KEYS", col1, row2, 120, 90, ClientConfig.isKeystrokesEnabled);

            if (searchText.isEmpty() || "sprint".contains(searchText) || "sneak".contains(searchText)) {
                boolean anyEnabled = ClientConfig.isToggleSprintEnabled || ClientConfig.isToggleSneakEnabled;
                drawCard(context, "Sprint/Sneak", col2, row2, 120, 90, anyEnabled);
            }
        }

        // タイトルと右上ボタン
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, panelY + 10, 0xFFF5C2E7);
        drawTab(context, "HUD", panelX + panelWidth - 100, panelY + 5, 40, 20, -1, mouseX, mouseY);
        drawTab(context, "CONFIG", panelX + panelWidth - 55, panelY + 5, 45, 20, -1, mouseX, mouseY);

        if (this.searchField != null) {
            this.searchField.render(context, mouseX, mouseY, delta);
        }
    }

    private void drawTab(DrawContext context, String text, int x, int y, int width, int height, int tabIndex, int mouseX, int mouseY) {
        boolean isSelected = (currentTab == tabIndex && tabIndex != -1);
        boolean isHovered = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
        int bgColor = isSelected ? 0xFF3A1F3A : (isHovered ? 0xFF2B1B2B : 0xFF1E121E);
        int textColor = isSelected ? 0xFFFF1493 : 0xFFAAAAAA;
        context.fill(x, y, x + width, y + height, bgColor);
        if (isSelected) context.fill(x, y + height - 2, x + width, y + height, 0xFFFF1493);
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
        String statusText = isEnabled ? "ENABLED" : "DISABLED";
        context.fill(x + 5, statusY, x + width - 5, statusY + 15, statusColor);
        int statusTextX = x + (width - this.textRenderer.getWidth(statusText)) / 2;
        context.drawTextWithShadow(this.textRenderer, statusText, statusTextX, statusY + 4, 0xFFFFFF);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.searchField != null && this.searchField.mouseClicked(mouseX, mouseY, button)) {
            this.setFocused(this.searchField);
            return true;
        }
        if (this.searchField != null) this.setFocused(null);

        if (isMouseOverTab(mouseX, mouseY, panelX + 10, panelY - 25, 80, 25)) { currentTab = 0; return true; }
        if (isMouseOverTab(mouseX, mouseY, panelX + 95, panelY - 25, 100, 25)) { currentTab = 1; return true; }
        if (isMouseOverTab(mouseX, mouseY, panelX + 200, panelY - 25, 80, 25)) { currentTab = 2; return true; } // ★追加

        if (isMouseOverTab(mouseX, mouseY, panelX + panelWidth - 100, panelY + 5, 40, 20)) { if (this.client != null) this.client.setScreen(new HudEditorScreen()); return true; }
        if (isMouseOverTab(mouseX, mouseY, panelX + panelWidth - 55, panelY + 5, 45, 20)) { if (this.client != null) this.client.setScreen(new ConfigScreen()); return true; }

        if (currentTab == 0) {
            String searchText = this.searchField != null ? this.searchField.getText().toLowerCase().trim() : "";
            int col1 = panelX + 15;
            int col2 = panelX + 155;
            int col3 = panelX + 295;
            int row1 = panelY + 50;
            int row2 = panelY + 160;

            if (searchText.isEmpty() || "fps".contains(searchText)) {
                if (isMouseOverTab(mouseX, mouseY, col1, row1, 120, 90)) {
                    if (mouseY >= row1 + 70) { ClientConfig.isFpsEnabled = !ClientConfig.isFpsEnabled; ClientConfig.save(); }
                    else if (this.client != null) this.client.setScreen(new HudSettingsScreen("FPS"));
                    return true;
                }
            }
            if (searchText.isEmpty() || "cps".contains(searchText)) {
                if (isMouseOverTab(mouseX, mouseY, col2, row1, 120, 90)) {
                    if (mouseY >= row1 + 70) { ClientConfig.isCpsEnabled = !ClientConfig.isCpsEnabled; ClientConfig.save(); }
                    else if (this.client != null) this.client.setScreen(new HudSettingsScreen("CPS"));
                    return true;
                }
            }
            if (searchText.isEmpty() || "ping".contains(searchText)) {
                if (isMouseOverTab(mouseX, mouseY, col3, row1, 120, 90)) {
                    if (mouseY >= row1 + 70) { ClientConfig.isPingEnabled = !ClientConfig.isPingEnabled; ClientConfig.save(); }
                    else if (this.client != null) this.client.setScreen(new HudSettingsScreen("PING"));
                    return true;
                }
            }
            if (searchText.isEmpty() || "keys".contains(searchText) || "keystrokes".contains(searchText)) {
                if (isMouseOverTab(mouseX, mouseY, col1, row2, 120, 90)) {
                    if (mouseY >= row2 + 70) { ClientConfig.isKeystrokesEnabled = !ClientConfig.isKeystrokesEnabled; ClientConfig.save(); }
                    else if (this.client != null) this.client.setScreen(new HudSettingsScreen("KEYS"));
                    return true;
                }
            }
            if (searchText.isEmpty() || "sprint".contains(searchText) || "sneak".contains(searchText)) {
                if (isMouseOverTab(mouseX, mouseY, col2, row2, 120, 90)) {
                    if (mouseY >= row2 + 70) {
                        boolean anyEnabled = ClientConfig.isToggleSprintEnabled || ClientConfig.isToggleSneakEnabled;
                        ClientConfig.isToggleSprintEnabled = !anyEnabled;
                        ClientConfig.isToggleSneakEnabled = false;
                        ClientConfig.save();
                    } else {
                        if (this.client != null) this.client.setScreen(new HudSettingsScreen("MOVE"));
                    }
                    return true;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (this.searchField != null && this.searchField.isFocused()) return this.searchField.charTyped(chr, modifiers);
        return super.charTyped(chr, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.searchField != null && this.searchField.isFocused()) {
            if (keyCode == 256) { this.setFocused(null); return true; }
            return this.searchField.keyPressed(keyCode, scanCode, modifiers);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private boolean isMouseOverTab(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}