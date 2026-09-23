package pluginsfix.glowwoodcutter.listener;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import pluginsfix.glowwoodcutter.action.ActionService;
import pluginsfix.glowwoodcutter.config.PluginSettings;
import pluginsfix.glowwoodcutter.restore.BlockRestoreService;
import pluginsfix.glowwoodcutter.zone.CuttingZone;
import pluginsfix.glowwoodcutter.zone.ZoneService;

import java.util.Optional;

public final class BlockBreakListener implements Listener {
    private final ZoneService zoneService;
    private final BlockRestoreService restoreService;
    private final ActionService actionService;
    private volatile PluginSettings settings;

    public BlockBreakListener(
            ZoneService zoneService,
            BlockRestoreService restoreService,
            ActionService actionService,
            PluginSettings settings
    ) {
        this.zoneService = zoneService;
        this.restoreService = restoreService;
        this.actionService = actionService;
        this.settings = settings;
    }

    public void updateSettings(PluginSettings settings) {
        this.settings = settings;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();

        Optional<CuttingZone> zoneOptional = zoneService.findZone(block.getLocation());
        if (zoneOptional.isEmpty()) {
            return;
        }

        CuttingZone zone = zoneOptional.get();

        if (!player.hasPermission("glowwoodcutter.cutting")) {
            event.setCancelled(true);
            actionService.executeActions(player, settings.getActionList("noPermsForBreak"));
            return;
        }

        if (!zone.drop()) {
            try {
                event.setDropItems(false);
            } catch (Throwable ignored) {
            }
            event.setExpToDrop(0);
        }

        if (zone.restore()) {
            restoreService.registerBrokenBlock(block, zone.restoreTime());
        }

        actionService.executeActions(player, zone.actions());
    }
}
