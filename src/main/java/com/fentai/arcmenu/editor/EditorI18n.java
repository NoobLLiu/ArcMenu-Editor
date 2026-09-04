package com.fentai.arcmenu.editor;

import net.minecraft.network.chat.Component;

/** Uses Minecraft's active client language and standard resource-pack fallback. */
public final class EditorI18n {
    private EditorI18n() {}

    public static String text(String key, Object... arguments) {
        return Component.translatable(key, arguments).getString();
    }

    public static Component component(String key, Object... arguments) {
        return Component.translatable(key, arguments);
    }
}
