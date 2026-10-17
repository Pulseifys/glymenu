package com.example.client.mixin;

import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.network.SocialInteractionsManager;
import net.minecraft.client.util.ProfileKeys;
import net.minecraft.client.util.Session;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Minecraft.class)
public interface MinecraftMixin {

    @Mutable
    @Accessor("user")
    void setUser(User user);

    @Mutable
    @Accessor("session")
    void setSession(Session session);

    @Mutable
    @Accessor("userApiService")
    void setUserApiService(UserApiService userApiService);

    @Mutable
    @Accessor("socialInteractionsManager")
    void setSocialInteractionsManager(SocialInteractionsManager manager);

    @Mutable
    @Accessor("profileKeys")
    void setProfileKeys(ProfileKeys keys);

    @Accessor("authenticationService")
    YggdrasilAuthenticationService getAuthenticationService();
}
