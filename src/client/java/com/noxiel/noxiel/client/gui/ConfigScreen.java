package com.noxiel.noxiel.client.gui;

import com.noxiel.noxiel.client.ClientConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class ConfigScreen extends Screen {
    private int panelWidth = 400;
    private int panelHeight = 250;
    private int panelX;
    private int panelY;

    private TextFieldWidget nameField;

    public ConfigScreen() {
        super(Text.literal("Config Manager"));
    }

    @Override
    protected void init() {
        super.init();
        this.panelX = (this.width - this.panelWidth) / 2;
        this.panelY = (this.height - this.panelHeight) / 2;

        // プロファイル名入力欄の作成
        this.nameField = new TextFieldWidget(this.textRenderer, panelX + 20, panelY + panelHeight - 60, 150, 20, Text.literal("Profile Name"));
        this.nameField.setMaxLength(16);
        this.addDrawableChild(this.nameField);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // 1. 背景をぼかす
        this.renderBackground(context, mouseX, mouseY, delta);

        // 2. パネルの背景と枠線
        context.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, 0xFF1E121E);
        context.drawBorder(panelX, panelY, panelWidth, panelHeight, 0xFFFF1493);

        // 3. タイトル文字
        context.drawCenteredTextWithShadow(this.textRenderer, "Config Manager", this.width / 2, panelY + 10, 0xFFF5C2E7);

        // 4. プロファイルリストの描画
        int listY = panelY + 35;
        for (String profile : ClientConfig.profiles) {
            boolean isSelected = profile.equals(ClientConfig.currentProfile);
            int color = isSelected ? 0xFFFF1493 : 0xAAAAAA;

            context.drawTextWithShadow(this.textRenderer, (isSelected ? "▶ " : "  ") + profile, panelX + 20, listY, color);

            if (!profile.equals("Default")) {
                context.drawTextWithShadow(this.textRenderer, "[X]", panelX + 150, listY, 0xFFAA0000);
            }
            listY += 15;
        }

        // 5. 入力欄のヒントテキスト
        context.drawTextWithShadow(this.textRenderer, "Create new profile:", panelX + 20, panelY + panelHeight - 75, 0xAAAAAA);
        context.drawTextWithShadow(this.textRenderer, "[Create]", panelX + 180, panelY + panelHeight - 55, 0xFF00AA00);
        context.drawTextWithShadow(this.textRenderer, "Press ESC to return", this.width / 2, panelY + panelHeight - 20, 0xAAAAAA);

        // ★6. テキスト入力欄だけは自前で描画する
        // (super.render() を呼ぶと文字がぼやけるため、nameFieldを直接描画します)
        if (this.nameField != null) {
            this.nameField.render(context, mouseX, mouseY, delta);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // プロファイルの選択と削除
        int listY = panelY + 35;
        for (String profile : ClientConfig.profiles) {
            // プロファイル名クリック（切り替え）
            if (mouseX >= panelX + 20 && mouseX <= panelX + 140 && mouseY >= listY && mouseY <= listY + 10) {
                ClientConfig.switchProfile(profile);
                return true;
            }
            // 削除ボタンクリック
            if (!profile.equals("Default") && mouseX >= panelX + 150 && mouseX <= panelX + 170 && mouseY >= listY && mouseY <= listY + 10) {
                ClientConfig.deleteProfile(profile);
                return true;
            }
            listY += 15;
        }

        // 作成ボタンクリック
        if (mouseX >= panelX + 180 && mouseX <= panelX + 230 && mouseY >= panelY + panelHeight - 60 && mouseY <= panelY + panelHeight - 40) {
            String name = this.nameField.getText().trim();
            if (!name.isEmpty()) {
                ClientConfig.createProfile(name);
                this.nameField.setText(""); // 入力欄をクリア
            }
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void close() {
        ClientConfig.save(); // 画面を閉じる時に現在のプロファイルを保存
        super.close();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}