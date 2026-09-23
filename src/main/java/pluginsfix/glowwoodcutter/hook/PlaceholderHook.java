package pluginsfix.glowwoodcutter.hook;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class PlaceholderHook {
    private static Boolean available = null;

    private PlaceholderHook() {
    }

    public static boolean isAvailable() {
        if (available == null) {
            available = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
        }
        return available;
    }

    public static String setPlaceholders(Player player, String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        if (player != null && isAvailable()) {
            try {
                return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, text);
            } catch (Throwable ignored) {
                return text;
            }
        }
        return text;
    }
}
