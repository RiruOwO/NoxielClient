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
    // 現在の設定データ（これまでの変数たち）
    // ==========================================
    public static boolean isFpsEnabled = true;
    public static boolean isCpsEnabled = false;
    public static int fpsX = 10;
    public static int fpsY = 10;
    public static int cpsX = 10;
    public static int cpsY = 30;

    // ==========================================
    // プロファイル管理用のデータ
    // ==========================================
    // 現在選択されているプロファイル名
    public static String currentProfile = "Default";

    // 存在するプロファイルのリスト
    public static List<String> profiles = new ArrayList<>();

    // ==========================================
    // 保存・読み込み処理
    // ==========================================
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // 保存先のベースフォルダ (config/Noxiel)
    private static final File CONFIG_DIR = new File(FabricLoader.getInstance().getConfigDir().toFile(), "Noxiel");

    // 現在のプロファイルのファイルパス
    private static File getCurrentProfileFile() {
        return new File(CONFIG_DIR, currentProfile + ".json");
    }

    // 初期化（起動時に呼ぶ）
    public static void init() {
        if (!CONFIG_DIR.exists()) {
            CONFIG_DIR.mkdirs();
        }

        // 既存のプロファイルファイルをリストアップ
        File[] files = CONFIG_DIR.listFiles((dir, name) -> name.endsWith(".json"));
        profiles.clear();
        if (files != null) {
            for (File f : files) {
                profiles.add(f.getName().replace(".json", ""));
            }
        }

        // プロファイルが何もなければDefaultを作成
        if (profiles.isEmpty()) {
            profiles.add("Default");
            currentProfile = "Default";
            save();
        } else {
            // リストの最初のプロファイルを読み込む（あるいは前回のプロファイルを記録する仕組みを作ってもよい）
            currentProfile = profiles.get(0);
            load();
        }
    }

    // 現在のプロファイルを読み込む
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
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 現在のプロファイルを保存する
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

    // 新しいプロファイルを作成する
    public static void createProfile(String name) {
        if (name == null || name.trim().isEmpty() || profiles.contains(name)) return;

        // 現在の設定を新規プロファイルとして保存
        profiles.add(name);
        currentProfile = name;
        save();
    }

    // プロファイルを削除する
    public static void deleteProfile(String name) {
        if (name.equals("Default") || !profiles.contains(name)) return; // Defaultは消せない

        File file = new File(CONFIG_DIR, name + ".json");
        if (file.exists()) file.delete();

        profiles.remove(name);

        // 消したのが現在のプロファイルなら、Defaultに切り替える
        if (currentProfile.equals(name)) {
            currentProfile = profiles.get(0);
            load();
        }
    }

    // プロファイルを切り替える
    public static void switchProfile(String name) {
        if (!profiles.contains(name)) return;
        currentProfile = name;
        load();
    }

    // JSONの形を定義する内部クラス
    private static class ProfileData {
        boolean isFpsEnabled;
        boolean isCpsEnabled;
        int fpsX;
        int fpsY;
        int cpsX;
        int cpsY;
    }
}