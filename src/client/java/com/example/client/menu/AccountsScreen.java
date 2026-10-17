package com.example.client.menu;

import com.example.client.mixin.MinecraftMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

public class AccountsScreen extends Screen {
    private final Screen parent;
    private EditBox nameField;
    private String statusMessage = "Введите ник и нажмите 'Сменить'";
    private int statusColor = 0xAAAAAA;

    public AccountsScreen(Screen parent) {
        super(Component.literal("Accounts"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.nameField = new EditBox(this.font, this.width / 2 - 100, this.height / 2 - 20, 200, 20, Component.literal("Nickname"));
        this.nameField.setValue(this.minecraft.getUser().getName());
        this.addRenderableWidget(this.nameField);

        this.addRenderableWidget(Button.builder(Component.literal("Сменить"), (btn) -> {
            changeNickname(this.nameField.getValue().trim());
        }).bounds(this.width / 2 - 100, this.height / 2 + 10, 200, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("Назад"), (btn) -> {
            this.minecraft.setScreen(this.parent);
        }).bounds(this.width / 2 - 100, this.height / 2 + 40, 200, 20).build());
    }

    private void changeNickname(String newName) {
        if (newName.isEmpty() || newName.length() > 16) {
            statusMessage = "Ник должен быть от 1 до 16 символов!";
            statusColor = 0xFF5555;
            return;
        }

        try {
            Minecraft mc = Minecraft.getInstance();
            UUID newUuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + newName).getBytes(StandardCharsets.UTF_8));
            
            // Создаем нового юзера
            User newUser = new User(newName, newUuid, "0", Optional.empty(), Optional.empty());
            
            // Подменяем юзера через наш Mixin
            ((MinecraftMixin) mc).setUser(newUser);

            statusMessage = "УСПЕХ! Ник изменен на " + newName;
            statusColor = 0x55FF55; // Зеленый

        } catch (Throwable e) {
            String errorName = e.getClass().getSimpleName();
            statusMessage = "ОШИБКА: " + errorName;
            statusColor = 0xFF5555;
            e.printStackTrace();
        }
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
        this.renderBackground(g, mouseX, mouseY, delta);
        g.drawCenteredString(this.font, "Смена оффлайн-аккаунта", this.width / 2, this.height / 2 - 50, 0xFFFFFF);
        g.drawCenteredString(this.font, statusMessage, this.width / 2, this.height / 2 + 70, statusColor);
        super.render(g, mouseX, mouseY, delta);
    }
}
