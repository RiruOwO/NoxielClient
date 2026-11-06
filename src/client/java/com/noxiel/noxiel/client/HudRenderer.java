package com.noxiel.noxiel.client;

import com.noxiel.noxiel.client.gui.HudEditorScreen;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class HudRenderer {

    private static int leftClicks = 0;
    private static long lastClickTime = 0;
    private static int currentCps = 0;

    public static void onClick() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime > 1000) {
            leftClicks = 0;
            lastClickTime = currentTime;
        }
        leftClicks++;
        currentCps = leftClicks;
    }

    public static void register() {
        HudRenderCallback.EVENT.register((context, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();

            if (client.player == null) return;
            // 設定画面やメニュー画面を開いている時はHUDを描画しない（HUD Editorを除く）
            if (client.currentScreen != null && !(client.currentScreen instanceof HudEditorScreen)) {
                return;
            }

            // ==========================================
            // FPSの描画
            // ==========================================
            if (ClientConfig.isFpsEnabled) {
                String text = "FPS: " + client.getCurrentFps();

                // 行列スタックを保存し、指定座標へ移動
                context.getMatrices().push();
                context.getMatrices().translate(ClientConfig.fpsX, ClientConfig.fpsY, 0);
                // スケール（サイズ）を適用
                context.getMatrices().scale(ClientConfig.fpsScale, ClientConfig.fpsScale, 1.0f);

                // 背景の描画
                if (ClientConfig.fpsShowBackground) {
                    int width = client.textRenderer.getWidth(text);
                    context.fill(-2, -2, width + 2, 10, 0x80000000);
                }

                // 文字の描画（色も適用）
                context.drawTextWithShadow(client.textRenderer, text, 0, 0, ClientConfig.fpsColor);

                // 行列スタックを元に戻す
                context.getMatrices().pop();
            }

            // ==========================================
            // CPSの描画
            // ==========================================
            if (ClientConfig.isCpsEnabled) {
                String text = "CPS: " + currentCps;

                context.getMatrices().push();
                context.getMatrices().translate(ClientConfig.cpsX, ClientConfig.cpsY, 0);
                context.getMatrices().scale(ClientConfig.cpsScale, ClientConfig.cpsScale, 1.0f);

                if (ClientConfig.cpsShowBackground) {
                    int width = client.textRenderer.getWidth(text);
                    context.fill(-2, -2, width + 2, 10, 0x80000000);
                }

                context.drawTextWithShadow(client.textRenderer, text, 0, 0, ClientConfig.cpsColor);

                context.getMatrices().pop();
            }
        });
    }
}