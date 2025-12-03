package org.example.ebookstore.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Colors {
    private static final List<String> COLORS = Arrays.asList(
            "#1abc9c", // Turquoise
            "#3498db", // Peterriver
            "#9b59b6", // Amethyst
            "#34495e", // Wet Asphalt
            "#16a085", // Green Sea
            "#27ae60", // Nephritis
            "#2980b9", // Belize Hole
            "#8e44ad", // Wisteria
            "#2c3e50", // Midnight Blue
            "#f1c40f", // Sun Flower
            "#e67e22", // Carrot
            "#e74c3c", // Alizarin
            "#ecf0f1", // Clouds
            "#95a5a6", // Concrete
            "#f39c12", // Orange
            "#d35400", // Pumpkin
            "#c0392b", // Pomegranate
            "#bdc3c7", // Silver
            "#7f8c8d"  // Asbestos
    );

    private static final Random RANDOM = new Random();

    public static String getRandomColor() {
        return COLORS.get(RANDOM.nextInt(COLORS.size()));
    }

    public static List<String> getAllColors() {
        return Collections.unmodifiableList(COLORS);
    }
}
