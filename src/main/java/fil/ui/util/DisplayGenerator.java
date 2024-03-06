package fil.ui.util;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Utility class that helps to display objects in a readable way.
 */
public class DisplayGenerator {

    private static final String EMPTY_VALUE_DISPLAY = "Rien à afficher";

    private DisplayGenerator() {
    }

    /**
     * Displays a list of objects.
     *
     * @param list The list to display.
     * @return The string representation of the list.
     */
    public static String display(List<?> list) {
        if (list.isEmpty()) {
            return EMPTY_VALUE_DISPLAY;
        }
        return list.stream()
                .map(item -> "- " + item)
                .collect(Collectors.joining("\n"));
    }

    /**
     * Displays a map of objects.
     *
     * @param map The map to display.
     * @return The string representation of the map.
     */
    public static String display(Map<?, ?> map) {
        if (map.isEmpty()) {
            return EMPTY_VALUE_DISPLAY;
        }
        return map.entrySet().stream()
                .map(entry -> "- " + entry.getKey() + " : " + entry.getValue())
                .collect(Collectors.joining("\n"));
    }

    /**
     * Displays a set of objects.
     *
     * @param set The set to display.
     * @return The string representation of the set.
     */
    public static String display(Set<?> set) {
        return display(List.copyOf(set));
    }

    /**
     * Displays any object.
     *
     * @param object The object to display.
     * @return The string representation of the object.
     */
    public static String display(Object object) {
        return object == null ? EMPTY_VALUE_DISPLAY : object.toString();
    }

    /**
     * Displays a string, or EMPTY_VALUE_DISPLAY if the string is empty or blank.
     *
     * @param string The string to display.
     * @return A string representation for the string.
     */
    public static String display(String string) {
        return string.isBlank() ? EMPTY_VALUE_DISPLAY : string;
    }

    /**
     * Displays an array of objects.
     *
     * @param array The array to display.
     * @return The string representation of the array.
     */
    public static String display(Object[] array) {
        return display(List.of(array));
    }

}
