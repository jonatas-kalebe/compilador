package entidades;

import estruturas.Arguments;

public class New implements Arguments {
    private final Name name;
    private final String nome;

    public New(String nome) {
        this.name = new Name(nome, "store");
        this.nome = nome;
    }

    @Override
    public String compileCode() {
        return String.format("new %s%n%s", nome, name.compileCode());
    }
}
