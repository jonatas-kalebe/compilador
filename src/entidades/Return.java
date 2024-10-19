package entidades;

import estruturas.BodyStatements;
import estruturas.IfStatements;

public class Return implements IfStatements, BodyStatements {
    private final Name nome;

    public Return(String nome) {
        if(nome.matches("\\d+")){
            this.nome = new Name(nome, "const");
        }
        else {
            this.nome = new Name(nome, "load");
        }

    }
    public Return(Name nome) {
        this.nome = nome;
    }

    @Override
    public String compileCode() {
        return String.format("%sret%n", nome.compileCode());
    }
}
