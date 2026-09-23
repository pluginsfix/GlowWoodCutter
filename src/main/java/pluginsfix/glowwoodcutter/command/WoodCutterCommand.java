package pluginsfix.glowwoodcutter.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pluginsfix.glowwoodcutter.GlowWoodCutter;
import pluginsfix.glowwoodcutter.action.ActionService;
import pluginsfix.glowwoodcutter.config.PluginSettings;
import pluginsfix.glowwoodcutter.restore.BlockRestoreService;
import pluginsfix.glowwoodcutter.text.TextFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class WoodCutterCommand implements CommandExecutor, TabCompleter {
    private final GlowWoodCutter plugin;
    private final BlockRestoreService restoreService;
    private final ActionService actionService;

    public WoodCutterCommand(
            GlowWoodCutter plugin,
            BlockRestoreService restoreService,
            ActionService actionService
    ) {
        this.plugin = plugin;
        this.restoreService = restoreService;
        this.actionService = actionService;
    }

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        PluginSettings settings = plugin.getSettings();

        if (!sender.hasPermission("glowwoodcutter.admin")) {
            sendActionOrMessage(sender, settings.getActionList("handleNoPerms"), "&cУ вас недостаточно прав!");
            return true;
        }

        if (args.length == 0) {
            sendActionOrMessage(sender, settings.getActionList("handleHelp"), "&eИспользуйте: /glowwoodcutter <reload|restore>");
            return true;
        }

        String sub = args[0].toLowerCase();
        switch (sub) {
            case "reload" -> {
                plugin.reloadPluginConfig();
                PluginSettings reloaded = plugin.getSettings();
                sendActionOrMessage(sender, reloaded.getActionList("handleReloaded"), "&aКонфигурация успешно перезагружена!");
            }
            case "restore" -> {
                int restoredCount = restoreService.restoreAll();
                sendActionOrMessage(sender, settings.getActionList("handleRestored"), "&aБлоки успешно восстановлены! (" + restoredCount + ")");
            }
            default -> sendActionOrMessage(sender, settings.getActionList("handleHelp"), "&eИспользуйте: /glowwoodcutter <reload|restore>");
        }

        return true;
    }

    private void sendActionOrMessage(CommandSender sender, List<String> actions, String fallback) {
        if (actions != null && !actions.isEmpty()) {
            if (sender instanceof Player player) {
                actionService.executeActions(player, actions);
            } else {
                for (String act : actions) {
                    TextFormatter.sendMessage(sender, act.replaceFirst("^\\[.*?\\]\\s*", ""));
                }
            }
        } else {
            TextFormatter.sendMessage(sender, fallback);
        }
    }

    @Override
    public List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        if (!sender.hasPermission("glowwoodcutter.admin")) {
            return Collections.emptyList();
        }

        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            String current = args[0].toLowerCase();
            for (String sub : List.of("reload", "restore")) {
                if (sub.startsWith(current)) {
                    completions.add(sub);
                }
            }
            return completions;
        }

        return Collections.emptyList();
    }
}
