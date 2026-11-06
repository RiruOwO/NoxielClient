package com.noxiel.noxiel.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class ClientConfig {
    // ==========================================
    // 基本設定データ (初期値はすべてfalse)
    // ==========================================
    public static boolean isFpsEnabled = false;
    public static boolean isCpsEnabled = false;
    public static boolean isPingEnabled = false;
    public static boolean isKeystrokesEnabled = false;
    public static boolean isToggleSprintEnabled = false;
    public static boolean isToggleSneakEnabled = false;

    // HUDの座標
    public static int fpsX = 10, fpsY = 10;
    public static int cpsX = 10, cpsY = 30;
    public static int pingX = 10, pingY = 50;
    public static int keysX = 10, keysY = 80;

    // HUDの詳細設定
    public static float fpsScale = 1.0f;
    public static boolean fpsShowBackground = false;
    public static int fpsColor = 0xFFF5C2E7;

    public static float cpsScale = 1.0f;
    public static boolean cpsShowBackground = false;
    public static int cpsColor = 0xFFF5C2E7;

    public static float pingScale = 1.0f;
    public static float keysScale = 1.0f;

    // Toggle内部状態
    public static boolean sprintToggled = false;
    public static boolean sneakToggled = false;

    // プロファイル管理
    public static String currentProfile = "Default";
    public static List<String> profiles = new ArrayList<>();

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_DIR = new File(FabricLoader.getInstance().getConfigDir().toFile(), "Noxiel");

    private static File getCurrentProfileFile() {
        return new File(CONFIG_DIR, currentProfile + ".json");
    }

    public static void init() {
        if (!CONFIG_DIR.exists()) CONFIG_DIR.mkdirs();
        File[] files = CONFIG_DIR.listFiles((dir, name) -> name.endsWith(".json"));
        profiles.clear();
        if (files != null) {
            for (File f : files) profiles.add(f.getName().replace(".json", ""));
        }
        if (profiles.isEmpty()) {
            profiles.add("Default");
            currentProfile = "Default";
            save();
        } else {
            currentProfile = profiles.get(0);
            load();
        }
    }

    public static void load() {
        File file = getCurrentProfileFile();
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                ProfileData data = GSON.fromJson(reader, ProfileData.class);
                if (data != null) {
                    isFpsEnabled = data.isFpsEnabled;
                    isCpsEnabled = data.isCpsEnabled;
                    isPingEnabled = data.isPingEnabled;
                    isKeystrokesEnabled = data.isKeystrokesEnabled;
                    isToggleSprintEnabled = data.isToggleSprintEnabled;
                    isToggleSneakEnabled = data.isToggleSneakEnabled;

                    fpsX = data.fpsX; fpsY = data.fpsY;
                    cpsX = data.cpsX; cpsY = data.cpsY;
                    pingX = data.pingX; pingY = data.pingY;
                    keysX = data.keysX; keysY = data.keysY;

                    fpsScale = data.fpsScale;
                    fpsShowBackground = data.fpsShowBackground;
                    fpsColor = data.fpsColor;
                    cpsScale = data.cpsScale;
                    cpsShowBackground = data.cpsShowBackground;
                    cpsColor = data.cpsColor;

                    pingScale = data.pingScale;
                    keysScale = data.keysScale;

                }
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    public static void save() {
        try {
            if (!CONFIG_DIR.exists()) CONFIG_DIR.mkdirs();
            ProfileData data = new ProfileData();
            data.isFpsEnabled = isFpsEnabled;
            data.isCpsEnabled = isCpsEnabled;
            data.isPingEnabled = isPingEnabled;
            data.isKeystrokesEnabled = isKeystrokesEnabled;
            data.isToggleSprintEnabled = isToggleSprintEnabled;
            data.isToggleSneakEnabled = isToggleSneakEnabled;

            data.fpsX = fpsX; data.fpsY = fpsY;
            data.cpsX = cpsX; data.cpsY = cpsY;
            data.pingX = pingX; data.pingY = pingY;
            data.keysX = keysX; data.keysY = keysY;

            data.fpsScale = fpsScale;
            data.fpsShowBackground = fpsShowBackground;
            data.fpsColor = fpsColor;
            data.cpsScale = cpsScale;
            data.cpsShowBackground = cpsShowBackground;
            data.cpsColor = cpsColor;

            data.pingScale = pingScale;
            data.keysScale = keysScale;

            try (FileWriter writer = new FileWriter(getCurrentProfileFile())) {
                GSON.toJson(data, writer);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static void createProfile(String name) {
        if (name == null || name.trim().isEmpty() || profiles.contains(name)) return;
        profiles.add(name);
        currentProfile = name;
        save();
    }

    public static void deleteProfile(String name) {
        if (name.equals("Default") || !profiles.contains(name)) return;
        new File(CONFIG_DIR, name + ".json").delete();
        profiles.remove(name);
        if (currentProfile.equals(name)) {
            currentProfile = profiles.get(0);
            load();
        }
    }

    public static void switchProfile(String name) {
        if (!profiles.contains(name)) return;
        currentProfile = name;
        load();
    }

    private static class ProfileData {
        boolean isFpsEnabled, isCpsEnabled, isPingEnabled, isKeystrokesEnabled, isToggleSprintEnabled, isToggleSneakEnabled;
        int fpsX, fpsY, cpsX, cpsY, pingX, pingY, keysX, keysY;
        float fpsScale, cpsScale, pingScale, keysScale;
        boolean fpsShowBackground, cpsShowBackground;
        int fpsColor, cpsColor;
        // ★追加
        String currentSkinModel;
    }
}