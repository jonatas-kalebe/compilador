package entidades;

import estruturas.Arguments;

public class Number implements Arguments {
    private final int valor;

    public Number(int valor) {
        this.valor = valor;
    }

    @Override
    public String compileCode() {
        return String.format("const %s%n", valor);
    }
}
