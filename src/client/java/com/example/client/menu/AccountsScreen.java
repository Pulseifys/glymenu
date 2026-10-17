package com.example.client.menu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.lang.reflect.Field;
import java.util.UUID;

public class AccountsScreen extends Screen {

    private final Screen parent;
    private EditBox nameBox;
    private String status = "";

    public AccountsScreen(Screen parent) {
        super(Component.literal("Аккаунты"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        int y = this.height / 2 - 40;
        this.nameBox = new EditBox(this.font, cx - 100, y, 200, 20, Component.literal("Ник"));
        this.nameBox.setValue(this.minecraft.getUser().getName());
        this.nameBox.setMaxLength(16);
        addRenderableWidget(this.nameBox);

        // simple clickable "buttons" handled manually
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
        g.fill(0, 0, this.width, this.height, 0xFF0A0A10);

        String nick = this.minecraft.getUser().getName();
        g.drawCenteredString(this.font, "Текущий ник: " + nick, this.width / 2, this.height / 2 - 70, 0xFFFFFFFF);
        g.drawCenteredString(this.font, "Введи новый ник и нажми «Сменить ник»", this.width / 2, this.height / 2 - 56, 0xFFA678FF);

        super.render(g, mouseX, mouseY, delta);

        drawBtn(g, mouseX, mouseY, this.width / 2 - 100, this.height / 2 - 10, 200, 20, "Сменить ник");
        drawBtn(g, mouseX, mouseY, this.width / 2 - 100, this.height / 2 + 16, 200, 20, "Назад");

        if (!status.isEmpty()) {
            g.drawCenteredString(this.font, status, this.width / 2, this.height / 2 + 48, 0xFF7CFC00);
        }
    }

    private void drawBtn(GuiGraphics g, int mouseX, int mouseY, int x, int y, int w, int h, String text) {
        boolean hovered = mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;
        int fill = hovered ? 0xC0803FD4 : 0x996A36B0;
        CustomMainMenuScreenAccess.rounded(g, x, y, w, h, 6, fill);
        g.drawCenteredString(this.font, text, x + w / 2, y + (h - 8) / 2, 0xFFFFFFFF);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int by1 = this.height / 2 - 10;
            int by2 = this.height / 2 + 16;
            int bx = this.width / 2 - 100;
            if (mouseX >= bx && mouseX <= bx + 200 && mouseY >= by1 && mouseY <= by1 + 20) {
                changeNick();
                return true;
            }
            if (mouseX >= bx && mouseX <= bx + 200 && mouseY >= by2 && mouseY <= by2 + 20) {
                this.minecraft.setScreen(this.parent);
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void changeNick() {
        String newName = this.nameBox.getValue().trim();
        if (newName.isEmpty()) {
            status = "Введи ник!";
            return;
        }
        try {
            User user = this.minecraft.getUser();
            Field nameField = User.class.getDeclaredField("name");
            nameField.setAccessible(true);
            nameField.set(user, newName);
            UUID offlineUuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + newName).getBytes(java.nio.charset.StandardCharsets.UTF_8));
            Field uuidField = User.class.getDeclaredField("uuid");
            uuidField.setAccessible(true);
            uuidField.set(user, offlineUuid);
            status = "Ник изменён на " + newName;
        } catch (Throwable t) {
            status = "Ошибка: " + t.getMessage();
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
