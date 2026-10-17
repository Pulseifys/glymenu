package com.example.client.menu;

import net.minecraft.client.gui.GuiGraphics;

public final class CustomMainMenuScreenAccess {
    private CustomMainMenuScreenAccess() {}

    public static void rounded(GuiGraphics g, int x, int y, int w, int h, int r, int color) {
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
}
