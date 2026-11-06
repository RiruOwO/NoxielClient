package com.noxiel.noxiel.client.gui;

import com.noxiel.noxiel.client.ClientConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class HudSettingsScreen extends Screen {
    private final String moduleName;
    private int panelWidth = 400;
    private int panelHeight = 250;
    private int panelX, panelY;

    public HudSettingsScreen(String moduleName) {
        super(Text.literal(moduleName + " Settings"));
        this.moduleName = moduleName;
    }

    @Override
    protected void init() {
        this.panelX = (this.width - this.panelWidth) / 2;
        this.panelY = (this.height - this.panelHeight) / 2;
        if (this.client != null) this.client.mouse.unlockCursor();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, 0xFF1E121E);
        context.drawBorder(panelX, panelY, panelWidth, panelHeight, 0xFFFF1493);
        context.drawTextWithShadow(this.textRenderer, moduleName + " Settings", panelX + 20, panelY + 15, 0xFFF5C2E7);

        if (moduleName.equals("FPS") || moduleName.equals("CPS")) {
            // FPS/CPSの設定
            context.drawTextWithShadow(this.textRenderer, "Scale: " + String.format("%.1f", getScale()), panelX + 20, panelY + 60, 0xFFFFFF);
            drawButton(context, "-", panelX + 150, panelY + 55, 20, 20, mouseX, mouseY);
            drawButton(context, "+", panelX + 175, panelY + 55, 20, 20, mouseX, mouseY);

            context.drawTextWithShadow(this.textRenderer, "Show Background", panelX + 20, panelY + 110, 0xFFFFFF);
            drawToggle(context, getShowBackground(), panelX + 150, panelY + 105, mouseX, mouseY);

        } else if (moduleName.equals("MOVE")) {
            // Sprint / Sneak の設定
            context.drawTextWithShadow(this.textRenderer, "Toggle Sprint", panelX + 20, panelY + 60, 0xFFFFFF);
            drawToggle(context, ClientConfig.isToggleSprintEnabled, panelX + 150, panelY + 55, mouseX, mouseY);

            context.drawTextWithShadow(this.textRenderer, "Toggle Sneak", panelX + 20, panelY + 110, 0xFFFFFF);
            drawToggle(context, ClientConfig.isToggleSneakEnabled, panelX + 150, panelY + 105, mouseX, mouseY);

        } else {
            // PING / KEYS (設定項目なし)
            context.drawCenteredTextWithShadow(this.textRenderer, "No settings available for " + moduleName, this.width / 2, panelY + 100, 0xAAAAAA);
        }

        context.drawCenteredTextWithShadow(this.textRenderer, "Press ESC to return", this.width / 2, panelY + panelHeight - 20, 0xAAAAAA);
    }


    private void drawButton(DrawContext context, String text, int x, int y, int w, int h, int mouseX, int mouseY) {
        boolean hovered = mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;
        context.fill(x, y, x + w, y + h, hovered ? 0xFF5A3F5A : 0xFF2B1B2B);
        context.drawBorder(x, y, w, h, 0xFF5A3F5A);
        context.drawCenteredTextWithShadow(this.textRenderer, text, x + w / 2, y + (h - 8) / 2, 0xFFFFFF);
    }

    private void drawToggle(DrawContext context, boolean isOn, int x, int y, int mouseX, int mouseY) {
        boolean hovered = mouseX >= x && mouseX <= x + 40 && mouseY >= y && mouseY <= y + 15;
        context.fill(x, y, x + 40, y + 15, isOn ? 0xFF00AA00 : 0xFFAA0000);
        if (hovered) context.drawBorder(x, y, 40, 15, 0xFFFFFFFF);
        context.drawCenteredTextWithShadow(this.textRenderer, isOn ? "ON" : "OFF", x + 20, y + 4, 0xFFFFFF);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (moduleName.equals("FPS") || moduleName.equals("CPS")) {
            if (mouseX >= panelX + 150 && mouseX <= panelX + 170 && mouseY >= panelY + 55 && mouseY <= panelY + 75) {
                setScale(Math.max(0.5f, getScale() - 0.1f)); ClientConfig.save(); return true;
            }
            if (mouseX >= panelX + 175 && mouseX <= panelX + 195 && mouseY >= panelY + 55 && mouseY <= panelY + 75) {
                setScale(Math.min(2.0f, getScale() + 0.1f)); ClientConfig.save(); return true;
            }
            if (mouseX >= panelX + 150 && mouseX <= panelX + 190 && mouseY >= panelY + 105 && mouseY <= panelY + 120) {
                setShowBackground(!getShowBackground()); ClientConfig.save(); return true;
            }
        } else if (moduleName.equals("MOVE")) {
            // Sprint Toggle
            if (mouseX >= panelX + 150 && mouseX <= panelX + 190 && mouseY >= panelY + 55 && mouseY <= panelY + 70) {
                ClientConfig.isToggleSprintEnabled = !ClientConfig.isToggleSprintEnabled; ClientConfig.save(); return true;
            }
            // Sneak Toggle
            if (mouseX >= panelX + 150 && mouseX <= panelX + 190 && mouseY >= panelY + 105 && mouseY <= panelY + 120) {
                ClientConfig.isToggleSneakEnabled = !ClientConfig.isToggleSneakEnabled; ClientConfig.save(); return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private float getScale() { return moduleName.equals("FPS") ? ClientConfig.fpsScale : ClientConfig.cpsScale; }
    private void setScale(float v) { if (moduleName.equals("FPS")) ClientConfig.fpsScale = v; else ClientConfig.cpsScale = v; }
    private boolean getShowBackground() { return moduleName.equals("FPS") ? ClientConfig.fpsShowBackground : ClientConfig.cpsShowBackground; }
    private void setShowBackground(boolean v) { if (moduleName.equals("FPS")) ClientConfig.fpsShowBackground = v; else ClientConfig.cpsShowBackground = v; }

    @Override
    public boolean shouldPause() { return false; }
}