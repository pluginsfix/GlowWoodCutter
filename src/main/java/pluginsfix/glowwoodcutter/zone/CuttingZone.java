package pluginsfix.glowwoodcutter.zone;

import java.util.List;

public record CuttingZone(
        String id,
        String world,
        String region,
        boolean drop,
        boolean restore,
        int restoreTime,
        List<String> actions
) {
}
