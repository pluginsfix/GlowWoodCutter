package pluginsfix.glowwoodcutter.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import pluginsfix.glowwoodcutter.restore.BlockRestoreService;

public final class BlockProtectionListener implements Listener {
    private final BlockRestoreService restoreService;

    public BlockProtectionListener(BlockRestoreService restoreService) {
        this.restoreService = restoreService;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (restoreService.isPending(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }
}
