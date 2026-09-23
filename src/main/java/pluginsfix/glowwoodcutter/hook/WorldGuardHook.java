package pluginsfix.glowwoodcutter.hook;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public final class WorldGuardHook {
    private final boolean available;

    public WorldGuardHook() {
        this.available = Bukkit.getPluginManager().isPluginEnabled("WorldGuard");
    }

    public boolean isAvailable() {
        return available;
    }

    public boolean isInRegion(Location location, String regionName) {
        if (!available || location == null || location.getWorld() == null || regionName == null) {
            return false;
        }

        try {
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionQuery query = container.createQuery();
            ApplicableRegionSet regions = query.getApplicableRegions(BukkitAdapter.adapt(location));

            for (ProtectedRegion region : regions) {
                if (region.getId().equalsIgnoreCase(regionName)) {
                    return true;
                }
            }
        } catch (Throwable ignored) {
            return false;
        }

        return false;
    }
}
