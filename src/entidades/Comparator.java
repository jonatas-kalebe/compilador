package entidades;

import java.util.HashMap;
import java.util.Map;

public class Comparator {
    private static final Map<String, String> comparadores = new HashMap<>();

    static {
        comparadores.put("eq", "eq\n");
        comparadores.put("ne", "ne\n");
        comparadores.put("gt", "gt\n");
        comparadores.put("ge", "ge\n");
        comparadores.put("lt", "lt\n");
        comparadores.put("le", "le\n");
    }

    public static String getComparator(String key) {
        return switch (key) {
            case "eq" -> comparadores.get("ne");
            case "ne" -> comparadores.get("eq");
            case "gt" -> comparadores.get("le");
            case "ge" -> comparadores.get("lt");
            case "lt" -> comparadores.get("ge");
            case "le" -> comparadores.get("gt");
            default -> null;
        };
    }

    public Map<String, String> getComparators() {
        return comparadores;
    }
}