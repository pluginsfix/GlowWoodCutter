package pluginsfix.glowwoodcutter.restore;

import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.scheduler.BukkitTask;

public final class PendingBlock {
    private final Location location;
    private final BlockData blockData;
    private final long restoreTimeMillis;
    private BukkitTask task;

    public PendingBlock(Location location, BlockData blockData, long restoreTimeMillis) {
        this.location = location;
        this.blockData = blockData;
        this.restoreTimeMillis = restoreTimeMillis;
    }

    public Location location() {
        return location;
    }

    public BlockData blockData() {
        return blockData;
    }

    public long restoreTimeMillis() {
        return restoreTimeMillis;
    }

    public BukkitTask task() {
        return task;
    }

    public void setTask(BukkitTask task) {
        this.task = task;
    }

    public void restore() {
        if (task != null && !task.isCancelled()) {
            task.cancel();
        }
        if (location.getWorld() != null) {
            location.getBlock().setBlockData(blockData, false);
        }
    }
}
