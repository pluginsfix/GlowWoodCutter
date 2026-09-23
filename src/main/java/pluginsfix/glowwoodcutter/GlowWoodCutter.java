package pluginsfix.glowwoodcutter;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import pluginsfix.glowwoodcutter.action.ActionService;
import pluginsfix.glowwoodcutter.command.WoodCutterCommand;
import pluginsfix.glowwoodcutter.config.PluginSettings;
import pluginsfix.glowwoodcutter.hook.WorldGuardHook;
import pluginsfix.glowwoodcutter.listener.BlockBreakListener;
import pluginsfix.glowwoodcutter.listener.BlockProtectionListener;
import pluginsfix.glowwoodcutter.restore.BlockRestoreService;
import pluginsfix.glowwoodcutter.zone.ZoneService;

public final class GlowWoodCutter extends JavaPlugin {
    private WorldGuardHook worldGuardHook;
    private ZoneService zoneService;
    private BlockRestoreService restoreService;
    private ActionService actionService;
    private BlockBreakListener breakListener;
    private PluginSettings settings;

    @Override
    public void onEnable() {
        long startTime = System.currentTimeMillis();

        saveDefaultConfig();
        reloadPluginConfig();

        worldGuardHook = new WorldGuardHook();
        zoneService = new ZoneService(worldGuardHook, settings);
        restoreService = new BlockRestoreService(this);
        actionService = new ActionService(this, settings);

        breakListener = new BlockBreakListener(zoneService, restoreService, actionService, settings);
        getServer().getPluginManager().registerEvents(breakListener, this);
        getServer().getPluginManager().registerEvents(new BlockProtectionListener(restoreService), this);

        PluginCommand command = getCommand("glowwoodcutter");
        if (command != null) {
            WoodCutterCommand executor = new WoodCutterCommand(this, restoreService, actionService);
            command.setExecutor(executor);
            command.setTabCompleter(executor);
        }

        long loadTime = System.currentTimeMillis() - startTime;
        printStartupBanner(loadTime);
    }

    @Override
    public void onDisable() {
        long startTime = System.currentTimeMillis();

        if (restoreService != null) {
            restoreService.shutdown();
        }

        long unloadTime = System.currentTimeMillis() - startTime;
        printShutdownBanner(unloadTime);
    }

    public void reloadPluginConfig() {
        reloadConfig();
        this.settings = PluginSettings.fromConfig(getConfig());
        if (zoneService != null) {
            zoneService.updateSettings(this.settings);
        }
        if (actionService != null) {
            actionService.updateSettings(this.settings);
        }
        if (breakListener != null) {
            breakListener.updateSettings(this.settings);
        }
    }

    public PluginSettings getSettings() {
        return settings;
    }

    public BlockRestoreService getRestoreService() {
        return restoreService;
    }

    public ActionService getActionService() {
        return actionService;
    }

    private void printStartupBanner(long loadTime) {
        String version = getDescription().getVersion();
        getLogger().info("§e◆ GlowWoodCutter | Загрузка плагина...");
        getLogger().info("§e ");
        getLogger().info("§e ████  █    ████  █   █  █   █  ████  ████  ███   ████  █   █  █████  █████  █████  ████ ");
        getLogger().info("§e █▄▄█  █▄▄▄ █▄▄█  █▄█▄█  █ █ █  █▄▄█  █▄▄█  █▄▄█  █▄▄█  █▄▄▄█    █      █    █▄▄▄   ██▄▄ ");
        getLogger().info("§e ");
        getLogger().info("§e                 (By pluginsfix for everyone)");
        getLogger().info("§e ");
        getLogger().info("§e          ▶ Плагин успешно включен и активирован!");
        getLogger().info("§e ");
        getLogger().info("§e             ◆ Версия плагина: v" + version);
        getLogger().info("§e            ◆ Время загрузки: " + loadTime + " мс.");
    }

    private void printShutdownBanner(long unloadTime) {
        String version = getDescription().getVersion();
        getLogger().info("§c◆ GlowWoodCutter | Начало выгрузки плагина...");
        getLogger().info("§c ");
        getLogger().info("§c ████  █    ████  █   █  █   █  ████  ████  ███   ████  █   █  █████  █████  █████  ████ ");
        getLogger().info("§c █▄▄█  █▄▄▄ █▄▄█  █▄█▄█  █ █ █  █▄▄█  █▄▄█  █▄▄█  █▄▄█  █▄▄▄█    █      █    █▄▄▄   ██▄▄ ");
        getLogger().info("§c ");
        getLogger().info("§c                 (By pluginsfix for everyone)");
        getLogger().info("§c ");
        getLogger().info("§c          ▶ Плагин успешно выгружен и выключен!");
        getLogger().info("§c ");
        getLogger().info("§c             ◆ Версия плагина: v" + version);
        getLogger().info("§c            ◆ Время выгрузки: " + unloadTime + " мс.");
    }
}
