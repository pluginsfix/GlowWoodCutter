package pluginsfix.glowwoodcutter.action;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import pluginsfix.glowwoodcutter.config.PluginSettings;
import pluginsfix.glowwoodcutter.text.TextFormatter;

import java.util.List;

public final class ActionService {
    private final Plugin plugin;
    private volatile PluginSettings settings;

    public ActionService(Plugin plugin, PluginSettings settings) {
        this.plugin = plugin;
        this.settings = settings;
    }

    public void updateSettings(PluginSettings settings) {
        this.settings = settings;
    }

    public void executeActions(Player player, List<String> actions) {
        if (actions == null || actions.isEmpty()) {
            return;
        }

        for (String action : actions) {
            executeSingleAction(player, action);
        }
    }

    public void executeSingleAction(Player player, String rawAction) {
        if (rawAction == null || rawAction.trim().isEmpty()) {
            return;
        }

        String action = rawAction.trim();
        int closeBracket = action.indexOf(']');
        if (!action.startsWith("[") || closeBracket == -1) {
            TextFormatter.sendMessage(player, action);
            return;
        }

        String tag = action.substring(1, closeBracket).trim().toLowerCase();
        String content = action.substring(closeBracket + 1).trim();

        switch (tag) {
            case "console" -> executeConsole(player, content);
            case "player" -> executePlayerCommand(player, content);
            case "message" -> executeMessage(player, content);
            case "title" -> executeTitle(player, content);
            case "actionbar" -> executeActionBar(player, content);
            case "sound" -> executeSound(player, content);
            case "particle" -> executeParticle(player, content);
            case "bossbar" -> executeBossBar(player, content);
            case "achievement", "achivement" -> executeAchievement(player, content);
            default -> TextFormatter.sendMessage(player, content);
        }
    }

    private void executeConsole(Player player, String command) {
        String formatted = TextFormatter.formatPlaceholders(player, command);
        formatted = formatted.startsWith("/") ? formatted.substring(1) : formatted;
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), formatted);
    }

    private void executePlayerCommand(Player player, String command) {
        if (player == null) {
            return;
        }
        String formatted = TextFormatter.formatPlaceholders(player, command);
        formatted = formatted.startsWith("/") ? formatted.substring(1) : formatted;
        player.performCommand(formatted);
    }

    private void executeMessage(Player player, String message) {
        TextFormatter.sendMessage(player, message);
    }

    private void executeTitle(Player player, String content) {
        if (player == null) {
            return;
        }
        String[] parts = content.split(";", 2);
        String title = parts.length > 0 ? TextFormatter.formatPlaceholders(player, parts[0].trim()) : "";
        String subtitle = parts.length > 1 ? TextFormatter.formatPlaceholders(player, parts[1].trim()) : "";

        int fadeInTicks = Math.max(10, settings.titleOneD() > 100 ? settings.titleOneD() / 50 : settings.titleOneD());
        int stayTicks = Math.max(20, settings.titleTwoD() > 100 ? settings.titleTwoD() / 50 : settings.titleTwoD());
        int fadeOutTicks = Math.max(10, settings.titleThreeD() > 100 ? settings.titleThreeD() / 50 : settings.titleThreeD());

        player.sendTitle(title, subtitle, fadeInTicks, stayTicks, fadeOutTicks);
    }

    private void executeActionBar(Player player, String content) {
        if (player == null) {
            return;
        }
        String formatted = TextFormatter.formatPlaceholders(player, content);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(formatted));
    }

    private void executeSound(Player player, String content) {
        if (player == null) {
            return;
        }
        String[] parts = content.split("\\s+");
        if (parts.length == 0 || parts[0].isEmpty()) {
            return;
        }

        try {
            Sound sound = Sound.valueOf(parts[0].toUpperCase());
            float volume = parts.length > 1 ? Float.parseFloat(parts[1]) : settings.soundSafeVolume();
            float pitch = parts.length > 2 ? Float.parseFloat(parts[2]) : settings.soundSafePitch();
            player.playSound(player.getLocation(), sound, volume, pitch);
        } catch (Throwable ignored) {
        }
    }

    private void executeParticle(Player player, String content) {
        if (player == null) {
            return;
        }
        String[] parts = content.split("\\s+");
        if (parts.length == 0 || parts[0].isEmpty()) {
            return;
        }

        try {
            Particle particle = Particle.valueOf(parts[0].toUpperCase());
            int count = parts.length > 1 ? Integer.parseInt(parts[1]) : settings.particleSafeCount();
            double ox = parts.length > 2 ? Double.parseDouble(parts[2]) : settings.particleSafeOffSetX();
            double oy = parts.length > 3 ? Double.parseDouble(parts[3]) : settings.particleSafeOffSetY();
            double oz = parts.length > 4 ? Double.parseDouble(parts[4]) : settings.particleSafeOffSetZ();
            double speed = parts.length > 5 ? Double.parseDouble(parts[5]) : settings.particleSafeSpeed();

            Location loc = player.getLocation().add(0, 1.0, 0);
            player.spawnParticle(particle, loc, count, ox, oy, oz, speed);
        } catch (Throwable ignored) {
        }
    }

    private void executeBossBar(Player player, String content) {
        if (player == null) {
            return;
        }

        String formatted = TextFormatter.formatPlaceholders(player, content);
        BarColor color;
        try {
            color = BarColor.valueOf(settings.bossbarSafeColor().toUpperCase());
        } catch (Throwable throwable) {
            color = BarColor.YELLOW;
        }

        BarStyle style;
        try {
            style = BarStyle.valueOf(settings.bossbarSafeOverlay().toUpperCase());
        } catch (Throwable throwable) {
            style = BarStyle.SOLID;
        }

        BossBar bar = Bukkit.createBossBar(formatted, color, style);
        bar.setProgress(Math.min(1.0, Math.max(0.0, settings.bossbarSafeFloat())));
        bar.addPlayer(player);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            bar.removePlayer(player);
            bar.setVisible(false);
        }, 60L);
    }

    private void executeAchievement(Player player, String content) {
        if (player == null) {
            return;
        }
        String[] parts = content.split(";", 2);
        String title = parts.length > 0 ? TextFormatter.formatPlaceholders(player, parts[0].trim()) : "";
        String desc = parts.length > 1 ? TextFormatter.formatPlaceholders(player, parts[1].trim()) : settings.achivementSafeDescription();

        TextFormatter.sendMessage(player, title + " &7- &f" + desc);
    }
}
