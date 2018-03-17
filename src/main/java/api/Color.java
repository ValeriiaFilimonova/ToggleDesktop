package api;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public class Color {
    private static Map<String, String> colors = ImmutableMap.of(
        "6", "rgb(4, 187, 155)",
        "8", "rgb(55, 80, 181)"
    );

    public static String getColorRgb(String colorId) {
        if (colors.containsKey(colorId)) {
            return colors.get(colorId);
        }

        throw new IllegalArgumentException(String.format("No color with '%s' id exists in map", colorId));
    }
}
