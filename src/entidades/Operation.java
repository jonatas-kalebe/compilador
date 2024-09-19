package entidades;

import java.util.HashMap;
import java.util.Map;

public class Operation {
    private Operation() {
    }

    private static final Map<String, String> operacoes = new HashMap<>();
    static {
        operacoes.put("+", "add\n");
        operacoes.put("-", "sub\n");
        operacoes.put("*", "mul\n");
        operacoes.put("/", "div\n");
    }

    public static String getOperation(String key) {
        return operacoes.get(key);
    }

}
