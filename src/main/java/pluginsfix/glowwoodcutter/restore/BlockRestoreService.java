package pluginsfix.glowwoodcutter.restore;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class BlockRestoreService {
    private final Plugin plugin;
    private final Map<Location, PendingBlock> pendingBlocks = new ConcurrentHashMap<>();

    public BlockRestoreService(Plugin plugin) {
        this.plugin = plugin;
    }

    public void registerBrokenBlock(Block block, int restoreSeconds) {
        if (block == null) {
            return;
        }

        Location loc = block.getLocation();
        BlockData data = block.getBlockData().clone();

        PendingBlock existing = pendingBlocks.remove(loc);
        if (existing != null) {
            existing.restore();
        }

        long restoreAt = System.currentTimeMillis() + (restoreSeconds * 1000L);
        PendingBlock pending = new PendingBlock(loc, data, restoreAt);
        pendingBlocks.put(loc, pending);

        BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            PendingBlock removed = pendingBlocks.remove(loc);
            if (removed != null) {
                removed.restore();
            }
        }, Math.max(1L, restoreSeconds * 20L));

        pending.setTask(task);
    }

    public boolean isPending(Location location) {
        if (location == null) {
            return false;
        }
        return pendingBlocks.containsKey(location);
    }

    public int restoreAll() {
        int count = pendingBlocks.size();
        for (PendingBlock pending : pendingBlocks.values()) {
            pending.restore();
        }
        pendingBlocks.clear();
        return count;
    }

    public void shutdown() {
        restoreAll();
    }
}
