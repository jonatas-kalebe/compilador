package entidades;

import java.util.List;
import java.util.stream.Collectors;

public class MethodHeader {
    private final String nome;
    private List<String> parametros;

    public MethodHeader(String nome) {
        this.nome = nome;
    }

    public MethodHeader(String nome, List<String> parametros) {
        this.nome = nome;
        this.parametros = parametros;
    }

    public String compileCode() {
        String params = (parametros != null && !parametros.isEmpty())
                ? String.join(", ", parametros)
                : "";

        return "method " + nome + "(" + params + ")\n";
    }

}
