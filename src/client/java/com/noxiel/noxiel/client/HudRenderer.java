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
            if (client.currentScreen != null && !(client.currentScreen instanceof HudEditorScreen)) {
                return;
            }

            // FPSの描画（FPS専用の座標）
            if (ClientConfig.isFpsEnabled) {
                int fps = client.getCurrentFps();
                context.drawTextWithShadow(client.textRenderer, "FPS: " + fps, ClientConfig.fpsX, ClientConfig.fpsY, 0xFFF5C2E7);
            }

            // CPSの描画（CPS専用の座標）
            if (ClientConfig.isCpsEnabled) {
                context.drawTextWithShadow(client.textRenderer, "CPS: " + currentCps, ClientConfig.cpsX, ClientConfig.cpsY, 0xFFF5C2E7);
            }
        });
    }
}