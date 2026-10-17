package com.example.client.menu;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class CustomMainMenuScreen extends Screen {

    private static final int BTN_W = 220;
    private static final int BTN_H = 24;
    private static final int GAP = 8;

    private record MenuButton(String text, int x, int y, Runnable action) {
        boolean contains(double mx, double my) {
            return mx >= x && mx <= x + BTN_W && my >= y && my <= y + BTN_H;
        }
    }

    private final List<MenuButton> buttons = new ArrayList<>();

    public CustomMainMenuScreen() {
        super(Component.literal("Menu"));
    }

    @Override
    protected void init() {
        buttons.clear();
        int totalH = buttonsCount() * BTN_H + (buttonsCount() - 1) * GAP;
        int startY = this.height / 2 - totalH / 2 + 20;
        int x = this.width / 2 - BTN_W / 2;
        int y = startY;

        addButton(x, y, "Одиночная игра", () -> this.minecraft.setScreen(new SelectWorldScreen(this)));
        y += BTN_H + GAP;
        addButton(x, y, "Мультиплеер", () -> this.minecraft.setScreen(new JoinMultiplayerScreen(this)));
        y += BTN_H + GAP;
        addButton(x, y, "Аккаунты", () -> this.minecraft.setScreen(new AccountsScreen(this)));
        y += BTN_H + GAP;
        addButton(x, y, "Настройки", () -> this.minecraft.setScreen(new OptionsScreen(this, this.minecraft.options, null)));
        y += BTN_H + GAP;
        addButton(x, y, "Выход", () -> this.minecraft.stop());
    }

    private int buttonsCount() {
        return 5;
    }

    private void addButton(int x, int y, String text, Runnable action) {
        buttons.add(new MenuButton(text, x, y, action));
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
        // dark background
        g.fill(0, 0, this.width, this.height, 0xFF0A0A10);
        // subtle purple vignette at top
        g.fillGradient(0, 0, this.width, this.height / 2, 0x33FFFFFF, 0x00000000);

        String nick = this.minecraft.getUser().getName();
        g.drawCenteredString(this.font, "Good to see you, " + nick, this.width / 2, 36, 0xFFFFFFFF);
        g.drawCenteredString(this.font, "Hello Player! Good Game", this.width / 2, 52, 0xFFA678FF);

        for (MenuButton b : buttons) {
            boolean hovered = b.contains(mouseX, mouseY);
            int fill = hovered ? 0xC0803FD4 : 0x996A36B0;
            rounded(g, b.x(), b.y(), BTN_W, BTN_H, 8, fill);
            // outline
            roundedOutline(g, b.x(), b.y(), BTN_W, BTN_H, 8, hovered ? 0xFFBE93FF : 0x55A678FF);
            int textY = b.y() + (BTN_H - 8) / 2;
            g.drawCenteredString(this.font, b.text(), this.width / 2, textY, 0xFFFFFFFF);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            for (MenuButton b : buttons) {
                if (b.contains(mouseX, mouseY)) {
                    b.action().run();
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private static void rounded(GuiGraphics g, int x, int y, int w, int h, int r, int color) {
        g.fill(x + r, y, x + w - r, y + h, color);
        g.fill(x, y + r, x + r, y + h - r, color);
        g.fill(x + w - r, y + r, x + w, y + h - r, color);
        for (int dy = 0; dy < r; dy++) {
            int dx = (int) Math.round(r - Math.sqrt(r * r - (r - dy) * (r - dy)));
            g.fill(x + dx, y + dy, x + r, y + dy + 1, color);
            g.fill(x + w - r, y + dy, x + w - dx, y + dy + 1, color);
            g.fill(x + dx, y + h - 1 - dy, x + r, y + h - dy, color);
            g.fill(x + w - r, y + h - 1 - dy, x + w - dx, y + h - dy, color);
        }
    }

    private static void roundedOutline(GuiGraphics g, int x, int y, int w, int h, int r, int color) {
        // top and bottom lines
        g.fill(x + r, y, x + w - r, y + 1, color);
        g.fill(x + r, y + h - 1, x + w - r, y + h, color);
        // left and right lines
        g.fill(x, y + r, x + 1, y + h - r, color);
        g.fill(x + w - 1, y + r, x + w, y + h - r, color);
        // corner pixels
        for (int dy = 1; dy < r; dy++) {
            int dx = (int) Math.round(r - Math.sqrt(r * r - (r - dy) * (r - dy)));
            g.fill(x + dx, y + dy, x + dx + 1, y + dy + 1, color);
            g.fill(x + w - dx - 1, y + dy, x + w - dx, y + dy + 1, color);
            g.fill(x + dx, y + h - dy - 1, x + dx + 1, y + h - dy, color);
            g.fill(x + w - dx - 1, y + h - dy - 1, x + w - dx, y + h - dy, color);
        }
    }
}
