package entidades;

import estruturas.BodyStatements;
import estruturas.IfStatements;

public class Return implements IfStatements, BodyStatements {
    private final Name nome;

    public Return(String nome) {
        this.nome = new Name(nome, "load");
    }

    @Override
    public String compileCode() {
        return String.format("%sret%n", nome.compileCode());
    }
}
