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

            if (ClientConfig.isPingEnabled) {
                // シングルプレイ等ではPingが取得できないことがあるため0にする
                int ping = 0;
                if (client.getNetworkHandler() != null && client.player != null) {
                    var entry = client.getNetworkHandler().getPlayerListEntry(client.player.getUuid());
                    if (entry != null) {
                        ping = entry.getLatency();
                    }
                }
                String text = "Ping: " + ping + "ms";

                context.getMatrices().push();
                context.getMatrices().translate(ClientConfig.pingX, ClientConfig.pingY, 0);
                context.getMatrices().scale(ClientConfig.pingScale, ClientConfig.pingScale, 1.0f);
                context.drawTextWithShadow(client.textRenderer, text, 0, 0, 0xFFF5C2E7);
                context.getMatrices().pop();
            }

            // ==========================================
            // Keystrokesの描画 (簡易版)
            // ==========================================
            if (ClientConfig.isKeystrokesEnabled) {
                int x = ClientConfig.keysX;
                int y = ClientConfig.keysY;
                int size = 15; // キーのサイズ
                int gap = 2;   // キーの間隔

                context.getMatrices().push();
                context.getMatrices().translate(x, y, 0);
                context.getMatrices().scale(ClientConfig.keysScale, ClientConfig.keysScale, 1.0f);

                // Wキー
                drawKey(context, client, "W", 0, 0, size, gap, client.options.forwardKey.isPressed());
                // Aキー
                drawKey(context, client, "A", -(size + gap), size + gap, size, gap, client.options.leftKey.isPressed());
                // Sキー
                drawKey(context, client, "S", 0, size + gap, size, gap, client.options.backKey.isPressed());
                // Dキー
                drawKey(context, client, "D", size + gap, size + gap, size, gap, client.options.rightKey.isPressed());

                context.getMatrices().pop();
            }
        });
    }

    private static void drawKey(DrawContext context, MinecraftClient client, String key, int x, int y, int size, int gap, boolean isPressed) {
        // 押されている時はピンク、押されていない時は半透明の黒
        int bgColor = isPressed ? 0xFFFF1493 : 0x80000000;
        int textColor = isPressed ? 0xFFFFFF : 0xFFAAAAAA;

        context.fill(x, y, x + size, y + size, bgColor);
        context.drawBorder(x, y, size, size, 0xFF555555);

        int textWidth = client.textRenderer.getWidth(key);
        context.drawTextWithShadow(client.textRenderer, key, x + (size - textWidth) / 2, y + (size - 8) / 2, textColor);
    }
}