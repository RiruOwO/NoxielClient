package com.noxiel.noxiel.client.gui;

import com.noxiel.noxiel.client.ClientConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class HudEditorScreen extends Screen {

    private int draggingTarget = 0; // 0=なし, 1=FPS, 2=CPS
    private double dragOffsetX = 0;
    private double dragOffsetY = 0;

    public HudEditorScreen() {
        super(Text.literal("HUD Editor"));
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, this.width, this.height, 0xA0000000);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);

        // ドラッグ用のテキスト描画
        if (ClientConfig.isFpsEnabled) {
            context.drawTextWithShadow(this.textRenderer, "FPS: 999", ClientConfig.fpsX, ClientConfig.fpsY, 0xFFF5C2E7);
        }
        if (ClientConfig.isCpsEnabled) {
            context.drawTextWithShadow(this.textRenderer, "CPS: 9", ClientConfig.cpsX, ClientConfig.cpsY, 0xFFF5C2E7);
        }

        context.drawCenteredTextWithShadow(this.textRenderer, "HUD Editor - Drag text to move", this.width / 2, this.height - 40, 0xFFFFFF);
        context.drawCenteredTextWithShadow(this.textRenderer, "Press ESC to save and return", this.width / 2, this.height - 25, 0xAAAAAA);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // FPSの当たり判定
        if (ClientConfig.isFpsEnabled &&
                mouseX >= ClientConfig.fpsX && mouseX <= ClientConfig.fpsX + 60 &&
                mouseY >= ClientConfig.fpsY && mouseY <= ClientConfig.fpsY + 12) {

            draggingTarget = 1;
            dragOffsetX = mouseX - ClientConfig.fpsX;
            dragOffsetY = mouseY - ClientConfig.fpsY;
            return true;
        }

        // CPSの当たり判定
        if (ClientConfig.isCpsEnabled &&
                mouseX >= ClientConfig.cpsX && mouseX <= ClientConfig.cpsX + 60 &&
                mouseY >= ClientConfig.cpsY && mouseY <= ClientConfig.cpsY + 12) {

            draggingTarget = 2;
            dragOffsetX = mouseX - ClientConfig.cpsX;
            dragOffsetY = mouseY - ClientConfig.cpsY;
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        draggingTarget = 0;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (draggingTarget == 1) {
            ClientConfig.fpsX = (int) (mouseX - dragOffsetX);
            ClientConfig.fpsY = (int) (mouseY - dragOffsetY);
            return true;
        } else if (draggingTarget == 2) {
            ClientConfig.cpsX = (int) (mouseX - dragOffsetX);
            ClientConfig.cpsY = (int) (mouseY - dragOffsetY);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public void close() {
        ClientConfig.save(); // 画面を閉じる時に設定を保存
        super.close();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}