package pluginsfix.glowwoodcutter.zone;

import org.bukkit.Location;
import org.bukkit.World;
import pluginsfix.glowwoodcutter.config.PluginSettings;
import pluginsfix.glowwoodcutter.hook.WorldGuardHook;

import java.util.Optional;

public final class ZoneService {
    private final WorldGuardHook worldGuardHook;
    private volatile PluginSettings settings;

    public ZoneService(WorldGuardHook worldGuardHook, PluginSettings settings) {
        this.worldGuardHook = worldGuardHook;
        this.settings = settings;
    }

    public void updateSettings(PluginSettings settings) {
        this.settings = settings;
    }

    public Optional<CuttingZone> findZone(Location location) {
        if (location == null) {
            return Optional.empty();
        }

        World world = location.getWorld();
        if (world == null) {
            return Optional.empty();
        }

        String worldName = world.getName();

        for (CuttingZone zone : settings.cuttingZones().values()) {
            if (!zone.world().equalsIgnoreCase(worldName) && !zone.world().equals("*")) {
                continue;
            }

            if (worldGuardHook.isInRegion(location, zone.region())) {
                return Optional.of(zone);
            }
        }

        return Optional.empty();
    }
}
