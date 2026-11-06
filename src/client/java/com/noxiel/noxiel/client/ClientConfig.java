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
    // 基本設定データ
    // ==========================================
    public static boolean isFpsEnabled = false;
    public static boolean isCpsEnabled = false;

    public static int fpsX = 10;
    public static int fpsY = 10;
    public static int cpsX = 10;
    public static int cpsY = 30;

    // ==========================================
    // HUDの詳細設定（サイズ・背景・色）
    // ==========================================
    // FPSの設定
    public static float fpsScale = 1.0f;
    public static boolean fpsShowBackground = false;
    public static int fpsColor = 0xFFF5C2E7;

    // CPSの設定
    public static float cpsScale = 1.0f;
    public static boolean cpsShowBackground = false;
    public static int cpsColor = 0xFFF5C2E7;

    // ==========================================
    // プロファイル管理用のデータ
    // ==========================================
    public static String currentProfile = "Default";
    public static List<String> profiles = new ArrayList<>();

    // ==========================================
    // 保存・読み込み処理
    // ==========================================
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_DIR = new File(FabricLoader.getInstance().getConfigDir().toFile(), "Noxiel");

    private static File getCurrentProfileFile() {
        return new File(CONFIG_DIR, currentProfile + ".json");
    }

    public static void init() {
        if (!CONFIG_DIR.exists()) {
            CONFIG_DIR.mkdirs();
        }

        File[] files = CONFIG_DIR.listFiles((dir, name) -> name.endsWith(".json"));
        profiles.clear();
        if (files != null) {
            for (File f : files) {
                profiles.add(f.getName().replace(".json", ""));
            }
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
                    fpsX = data.fpsX;
                    fpsY = data.fpsY;
                    cpsX = data.cpsX;
                    cpsY = data.cpsY;

                    // 詳細設定の読み込み
                    fpsScale = data.fpsScale;
                    fpsShowBackground = data.fpsShowBackground;
                    fpsColor = data.fpsColor;
                    cpsScale = data.cpsScale;
                    cpsShowBackground = data.cpsShowBackground;
                    cpsColor = data.cpsColor;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void save() {
        try {
            if (!CONFIG_DIR.exists()) {
                CONFIG_DIR.mkdirs();
            }

            ProfileData data = new ProfileData();
            data.isFpsEnabled = isFpsEnabled;
            data.isCpsEnabled = isCpsEnabled;
            data.fpsX = fpsX;
            data.fpsY = fpsY;
            data.cpsX = cpsX;
            data.cpsY = cpsY;

            // 詳細設定の保存
            data.fpsScale = fpsScale;
            data.fpsShowBackground = fpsShowBackground;
            data.fpsColor = fpsColor;
            data.cpsScale = cpsScale;
            data.cpsShowBackground = cpsShowBackground;
            data.cpsColor = cpsColor;

            try (FileWriter writer = new FileWriter(getCurrentProfileFile())) {
                GSON.toJson(data, writer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ==========================================
    // プロファイル操作メソッド
    // ==========================================
    public static void createProfile(String name) {
        if (name == null || name.trim().isEmpty() || profiles.contains(name)) return;
        profiles.add(name);
        currentProfile = name;
        save();
    }

    public static void deleteProfile(String name) {
        if (name.equals("Default") || !profiles.contains(name)) return;
        File file = new File(CONFIG_DIR, name + ".json");
        if (file.exists()) file.delete();
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

    // JSONの形を定義する内部クラス（ここにも追加した変数を忘れずに書く）
    private static class ProfileData {
        boolean isFpsEnabled;
        boolean isCpsEnabled;
        int fpsX;
        int fpsY;
        int cpsX;
        int cpsY;

        float fpsScale;
        boolean fpsShowBackground;
        int fpsColor;

        float cpsScale;
        boolean cpsShowBackground;
        int cpsColor;
    }
}