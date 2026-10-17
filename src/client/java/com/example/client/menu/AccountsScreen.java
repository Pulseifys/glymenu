package com.example.client.menu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
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
        // Текстовое поле для ввода ника
        this.nameField = new EditBox(this.font, this.width / 2 - 100, this.height / 2 - 20, 200, 20, Component.literal("Nickname"));
        this.nameField.setValue(this.minecraft.getUser().getName());
        this.addRenderableWidget(this.nameField);

        // Кнопка смены ника
        this.addRenderableWidget(Button.builder(Component.literal("Сменить"), (btn) -> {
            changeNickname(this.nameField.getValue().trim());
        }).bounds(this.width / 2 - 100, this.height / 2 + 10, 200, 20).build());

        // Кнопка назад
        this.addRenderableWidget(Button.builder(Component.literal("Назад"), (btn) -> {
            this.minecraft.setScreen(this.parent);
        }).bounds(this.width / 2 - 100, this.height / 2 + 40, 200, 20).build());
    }

    private void changeNickname(String newName) {
        if (newName.isEmpty() || newName.length() > 16) {
            statusMessage = "Ник должен быть от 1 до 16 символов!";
            statusColor = 0xFF5555; // Красный
            return;
        }

        try {
            Minecraft mc = Minecraft.getInstance();
            // Генерируем оффлайн UUID на основе нового ника
            UUID uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + newName).getBytes(StandardCharsets.UTF_8));

            // Пытаемся создать нового пользователя через рефлексию (нужно для 1.21.4)
            Class<?> userClass = Class.forName("net.minecraft.client.User");
            Constructor<?> constructor = null;
            
            // Ищем подходящий конструктор (String, UUID, String, Optional, Optional)
            for (Constructor<?> c : userClass.getConstructors()) {
                if (c.getParameterCount() == 5) {
                    constructor = c;
                    break;
                }
            }

            Object newUser = null;
            if (constructor != null) {
                // Создаем нового пользователя с "нулевым" токеном (подходит для оффлайна)
                newUser = constructor.newInstance(newName, uuid, "0", Optional.empty(), Optional.empty());
            }

            // Подменяем пользователя в главном классе Minecraft
            Field userField = Minecraft.class.getDeclaredField("user");
            userField.setAccessible(true);

            if (newUser != null) {
                userField.set(mc, newUser);
                statusMessage = "Ник изменен на " + newName + "! Перезапусти игру.";
                statusColor = 0x55FF55; // Зеленый
            } else {
                statusMessage = "Ошибка: конструктор не найден (нужен другой метод).";
                statusColor = 0xFF5555;
            }

        } catch (Exception e) {
            statusMessage = "Ошибка: " + e.getClass().getSimpleName();
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
