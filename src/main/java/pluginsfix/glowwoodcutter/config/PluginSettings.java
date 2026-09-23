package pluginsfix.glowwoodcutter.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import pluginsfix.glowwoodcutter.zone.CuttingZone;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class PluginSettings {
    private final String unknown;
    private final float bossbarSafeFloat;
    private final String bossbarSafeOverlay;
    private final String bossbarSafeColor;
    private final int titleOneD;
    private final int titleTwoD;
    private final int titleThreeD;
    private final float soundSafeVolume;
    private final float soundSafePitch;
    private final int particleSafeCount;
    private final double particleSafeOffSetX;
    private final double particleSafeOffSetY;
    private final double particleSafeOffSetZ;
    private final double particleSafeSpeed;
    private final String achivementSafeIcon;
    private final String achivementSafeDescription;
    private final Map<String, CuttingZone> cuttingZones;
    private final Map<String, List<String>> actions;

    private PluginSettings(
            String unknown,
            float bossbarSafeFloat,
            String bossbarSafeOverlay,
            String bossbarSafeColor,
            int titleOneD,
            int titleTwoD,
            int titleThreeD,
            float soundSafeVolume,
            float soundSafePitch,
            int particleSafeCount,
            double particleSafeOffSetX,
            double particleSafeOffSetY,
            double particleSafeOffSetZ,
            double particleSafeSpeed,
            String achivementSafeIcon,
            String achivementSafeDescription,
            Map<String, CuttingZone> cuttingZones,
            Map<String, List<String>> actions
    ) {
        this.unknown = unknown;
        this.bossbarSafeFloat = bossbarSafeFloat;
        this.bossbarSafeOverlay = bossbarSafeOverlay;
        this.bossbarSafeColor = bossbarSafeColor;
        this.titleOneD = titleOneD;
        this.titleTwoD = titleTwoD;
        this.titleThreeD = titleThreeD;
        this.soundSafeVolume = soundSafeVolume;
        this.soundSafePitch = soundSafePitch;
        this.particleSafeCount = particleSafeCount;
        this.particleSafeOffSetX = particleSafeOffSetX;
        this.particleSafeOffSetY = particleSafeOffSetY;
        this.particleSafeOffSetZ = particleSafeOffSetZ;
        this.particleSafeSpeed = particleSafeSpeed;
        this.achivementSafeIcon = achivementSafeIcon;
        this.achivementSafeDescription = achivementSafeDescription;
        this.cuttingZones = Collections.unmodifiableMap(cuttingZones);
        this.actions = Collections.unmodifiableMap(actions);
    }

    public static PluginSettings fromConfig(FileConfiguration config) {
        ConfigurationSection settingsSection = config.getConfigurationSection("settings");
        String unknown = settingsSection != null ? settingsSection.getString("unknown", "отсутствует") : "отсутствует";

        ConfigurationSection bossbarSec = settingsSection != null ? settingsSection.getConfigurationSection("bossbarAction") : null;
        float bossbarSafeFloat = bossbarSec != null ? (float) bossbarSec.getDouble("safeFloat", 1.0) : 1.0f;
        String bossbarSafeOverlay = bossbarSec != null ? bossbarSec.getString("safeOverlay", "PROGRESS") : "PROGRESS";
        String bossbarSafeColor = bossbarSec != null ? bossbarSec.getString("safeColor", "YELLOW") : "YELLOW";

        ConfigurationSection titleSec = settingsSection != null ? settingsSection.getConfigurationSection("titleAction") : null;
        int titleOneD = titleSec != null ? titleSec.getInt("oneD", 3000) : 3000;
        int titleTwoD = titleSec != null ? titleSec.getInt("twoD", 3000) : 3000;
        int titleThreeD = titleSec != null ? titleSec.getInt("threeD", 4500) : 4500;

        ConfigurationSection soundSec = settingsSection != null ? settingsSection.getConfigurationSection("soundAction") : null;
        float soundSafeVolume = soundSec != null ? (float) soundSec.getDouble("safeVolume", 0.5) : 0.5f;
        float soundSafePitch = soundSec != null ? (float) soundSec.getDouble("safePitch", 1.0) : 1.0f;

        ConfigurationSection particleSec = settingsSection != null ? settingsSection.getConfigurationSection("particleAction") : null;
        int particleSafeCount = particleSec != null ? particleSec.getInt("safeCount", 20) : 20;
        double particleSafeOffSetX = particleSec != null ? particleSec.getDouble("safeOffSetX", 0.1) : 0.1;
        double particleSafeOffSetY = particleSec != null ? particleSec.getDouble("safeOffSetY", 0.1) : 0.1;
        double particleSafeOffSetZ = particleSec != null ? particleSec.getDouble("safeOffSetZ", 0.1) : 0.1;
        double particleSafeSpeed = particleSec != null ? particleSec.getDouble("safeSpeed", 0.1) : 0.1;

        ConfigurationSection achSec = settingsSection != null ? settingsSection.getConfigurationSection("achivementAction") : null;
        String achSafeIcon = achSec != null ? achSec.getString("safeIcon", "minecraft:bedrock") : "minecraft:bedrock";
        String achSafeDesc = achSec != null ? achSec.getString("safeDescription", "@pluginsfix") : "@pluginsfix";

        Map<String, CuttingZone> zones = new HashMap<>();
        ConfigurationSection zonesSec = settingsSection != null ? settingsSection.getConfigurationSection("cuttingZones") : null;
        if (zonesSec != null) {
            for (String key : zonesSec.getKeys(false)) {
                ConfigurationSection z = zonesSec.getConfigurationSection(key);
                if (z == null) {
                    continue;
                }
                String world = z.getString("world", "spawn");
                String region = z.getString("region", "cutting" + key);
                boolean drop = z.getBoolean("drop", true);
                boolean restore = z.getBoolean("restore", true);
                int restoreTime = z.getInt("restoreTime", 30);
                List<String> actions = z.getStringList("actions");
                zones.put(key, new CuttingZone(key, world, region, drop, restore, restoreTime, actions));
            }
        }

        Map<String, List<String>> actionsMap = new HashMap<>();
        ConfigurationSection actionsSec = config.getConfigurationSection("actions");
        if (actionsSec != null) {
            for (String key : actionsSec.getKeys(false)) {
                if (actionsSec.isList(key)) {
                    actionsMap.put(key, actionsSec.getStringList(key));
                } else if (actionsSec.isString(key)) {
                    actionsMap.put(key, List.of(actionsSec.getString(key)));
                }
            }
        }

        return new PluginSettings(
                unknown,
                bossbarSafeFloat,
                bossbarSafeOverlay,
                bossbarSafeColor,
                titleOneD,
                titleTwoD,
                titleThreeD,
                soundSafeVolume,
                soundSafePitch,
                particleSafeCount,
                particleSafeOffSetX,
                particleSafeOffSetY,
                particleSafeOffSetZ,
                particleSafeSpeed,
                achSafeIcon,
                achSafeDesc,
                zones,
                actionsMap
        );
    }

    public String unknown() {
        return unknown;
    }

    public float bossbarSafeFloat() {
        return bossbarSafeFloat;
    }

    public String bossbarSafeOverlay() {
        return bossbarSafeOverlay;
    }

    public String bossbarSafeColor() {
        return bossbarSafeColor;
    }

    public int titleOneD() {
        return titleOneD;
    }

    public int titleTwoD() {
        return titleTwoD;
    }

    public int titleThreeD() {
        return titleThreeD;
    }

    public float soundSafeVolume() {
        return soundSafeVolume;
    }

    public float soundSafePitch() {
        return soundSafePitch;
    }

    public int particleSafeCount() {
        return particleSafeCount;
    }

    public double particleSafeOffSetX() {
        return particleSafeOffSetX;
    }

    public double particleSafeOffSetY() {
        return particleSafeOffSetY;
    }

    public double particleSafeOffSetZ() {
        return particleSafeOffSetZ;
    }

    public double particleSafeSpeed() {
        return particleSafeSpeed;
    }

    public String achivementSafeIcon() {
        return achivementSafeIcon;
    }

    public String achivementSafeDescription() {
        return achivementSafeDescription;
    }

    public Map<String, CuttingZone> cuttingZones() {
        return cuttingZones;
    }

    public Map<String, List<String>> actions() {
        return actions;
    }

    public List<String> getActionList(String key) {
        return actions.getOrDefault(key, Collections.emptyList());
    }
}
