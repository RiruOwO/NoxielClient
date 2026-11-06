package com.noxiel.noxiel.client;

import com.noxiel.noxiel.client.gui.ModMenuScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class NoxielClient implements ClientModInitializer {

    private static KeyBinding openMenuKey;
    private static long lastAttackTime = 0;

    @Override
    public void onInitializeClient() {
        ClientConfig.init();
        // 1. 起動時に設定を読み込む
        ClientConfig.load();

        // 2. ゲーム終了時に設定を保存する
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
            ClientConfig.save();
        });

        // HUD描画の登録
        HudRenderer.register();

        // キーバインドの登録（右Shift）
        openMenuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.noxiel.open_menu",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "category.noxiel"
        ));

        // 右Shiftでメニューを開く処理
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenuKey.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new ModMenuScreen());
                }
            }

            // CPSカウント用のクリック検知
            if (client.options.attackKey.isPressed()) {
                if (System.currentTimeMillis() - lastAttackTime > 50) {
                    HudRenderer.onClick();
                    lastAttackTime = System.currentTimeMillis();
                }
            }
        });
    }
}