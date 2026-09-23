package pluginsfix.glowwoodcutter.text;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pluginsfix.glowwoodcutter.hook.PlaceholderHook;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TextFormatter {
    private static final Pattern HEX_PATTERN_1 = Pattern.compile("&#([A-Fa-f0-9]{6})");
    private static final Pattern HEX_PATTERN_2 = Pattern.compile("#([A-Fa-f0-9]{6})");
    private static final Pattern HOVER_PATTERN = Pattern.compile("\\[HoverText:(url|cmd|suggest)\\s+([^,]+),\\s*text:\\s*([^\\]]+)\\]");

    private TextFormatter() {
    }

    public static String colorize(String message) {
        if (message == null || message.isEmpty()) {
            return "";
        }
        String parsed = message;
        Matcher matcher1 = HEX_PATTERN_1.matcher(parsed);
        StringBuffer buffer1 = new StringBuffer();
        while (matcher1.find()) {
            String hex = matcher1.group(1);
            matcher1.appendReplacement(buffer1, ChatColor.of("#" + hex).toString());
        }
        matcher1.appendTail(buffer1);
        parsed = buffer1.toString();

        Matcher matcher2 = HEX_PATTERN_2.matcher(parsed);
        StringBuffer buffer2 = new StringBuffer();
        while (matcher2.find()) {
            String hex = matcher2.group(1);
            matcher2.appendReplacement(buffer2, ChatColor.of("#" + hex).toString());
        }
        matcher2.appendTail(buffer2);
        parsed = buffer2.toString();

        return ChatColor.translateAlternateColorCodes('&', parsed);
    }

    public static String formatPlaceholders(Player player, String text) {
        if (text == null) {
            return "";
        }
        String result = text;
        if (player != null) {
            result = result.replace("{player}", player.getName());
            result = result.replace("%player_name%", player.getName());
            result = PlaceholderHook.setPlaceholders(player, result);
        }
        return colorize(result);
    }

    public static void sendMessage(CommandSender sender, String rawText) {
        if (sender == null || rawText == null) {
            return;
        }
        Player player = sender instanceof Player ? (Player) sender : null;
        String processed = rawText;
        if (player != null) {
            processed = processed.replace("{player}", player.getName());
            processed = processed.replace("%player_name%", player.getName());
            processed = PlaceholderHook.setPlaceholders(player, processed);
        }

        if (sender instanceof Player p && HOVER_PATTERN.matcher(processed).find()) {
            BaseComponent[] components = parseHoverComponents(processed);
            p.spigot().sendMessage(components);
            return;
        }

        sender.sendMessage(colorize(processed));
    }

    public static BaseComponent[] parseHoverComponents(String text) {
        List<BaseComponent> result = new ArrayList<>();
        Matcher matcher = HOVER_PATTERN.matcher(text);
        int lastEnd = 0;

        while (matcher.find()) {
            int start = matcher.start();
            if (start > lastEnd) {
                String plainPart = text.substring(lastEnd, start);
                for (BaseComponent c : TextComponent.fromLegacyText(colorize(plainPart))) {
                    result.add(c);
                }
            }

            String actionType = matcher.group(1).trim().toLowerCase();
            String actionValue = matcher.group(2).trim();
            String hoverText = colorize(matcher.group(3).trim());

            BaseComponent[] hoverBody = TextComponent.fromLegacyText(hoverText);
            ClickEvent clickEvent = switch (actionType) {
                case "url" -> new ClickEvent(ClickEvent.Action.OPEN_URL, actionValue);
                case "cmd" -> new ClickEvent(ClickEvent.Action.RUN_COMMAND, actionValue);
                case "suggest" -> new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, actionValue);
                default -> null;
            };

            HoverEvent hoverEvent;
            try {
                hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hoverBody));
            } catch (Throwable throwable) {
                hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverBody);
            }

            for (BaseComponent comp : hoverBody) {
                if (clickEvent != null) {
                    comp.setClickEvent(clickEvent);
                }
                comp.setHoverEvent(hoverEvent);
                result.add(comp);
            }

            lastEnd = matcher.end();
        }

        if (lastEnd < text.length()) {
            String trailing = text.substring(lastEnd);
            for (BaseComponent c : TextComponent.fromLegacyText(colorize(trailing))) {
                result.add(c);
            }
        }

        return result.toArray(new BaseComponent[0]);
    }
}
