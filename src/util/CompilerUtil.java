package util;

import java.util.List;

public class CompilerUtil {
    private CompilerUtil() {
    }

    public static String compileVariableDefinitions(List<String> definicoesVariaveis) {
        if (definicoesVariaveis != null && !definicoesVariaveis.isEmpty()) {
            return "vars " + String.join(", ", definicoesVariaveis) + "\n";
        }
        return "";
    }
}