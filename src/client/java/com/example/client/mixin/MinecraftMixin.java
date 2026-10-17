package com.example.client.mixin;

import com.example.client.menu.CustomMainMenuScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User; // Добавили импорт User
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable; // Добавили импорт Mutable
import org.spongepowered.asm.mixin.gen.Accessor; // Добавили импорт Accessor
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Класс стал abstract, чтобы Mixin смог добавить метод изменения юзера
@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

    // ТВОЙ СТАРЫЙ КОД (замена меню)
    @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
    private void custommenu$replaceTitleScreen(Screen screen, CallbackInfo ci) {
        if (screen instanceof TitleScreen && !(screen instanceof CustomMainMenuScreen)) {
            ci.cancel();
            Minecraft.getInstance().setScreen(new CustomMainMenuScreen());
        }
    }

    // НОВЫЙ КОД (доступ к смене ника)
    @Mutable
    @Accessor("user")
    public abstract void setUser(User user);
}
